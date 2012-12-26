/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.jdbc.query;

import static org.seasar.doma.internal.util.AssertionUtil.*;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.seasar.doma.internal.jdbc.entity.AbstractPostInsertContext;
import org.seasar.doma.internal.jdbc.entity.AbstractPreInsertContext;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.internal.jdbc.sql.PreparedSqlBuilder;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.SqlKind;
import org.seasar.doma.jdbc.entity.EntityPropertyType;
import org.seasar.doma.jdbc.entity.EntityType;
import org.seasar.doma.jdbc.entity.GeneratedIdPropertyType;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.id.IdGenerationConfig;
import org.seasar.doma.message.Message;

/**
 * @author taedium
 * 
 */
public class AutoBatchInsertQuery<E> extends AutoBatchModifyQuery<E> implements
        BatchInsertQuery {

    protected GeneratedIdPropertyType<? super E, E, ?, ?> generatedIdPropertyType;

    protected IdGenerationConfig idGenerationConfig;

    protected boolean batchSupported = true;

    public AutoBatchInsertQuery(EntityType<E> entityType) {
        super(entityType);
    }

    @Override
    public void prepare() {
        assertNotNull(method, config, callerClassName, callerMethodName,
                entities, sqls);
        Iterator<E> it = entities.iterator();
        if (it.hasNext()) {
            executable = true;
            executionSkipCause = null;
            currentEntity = it.next();
            preInsert();
            prepareIdAndVersionPropertyTypes();
            prepareOptions();
            prepareTargetPropertyTypes();
            prepareIdValue();
            prepareVersionValue();
            prepareSql();
        } else {
            return;
        }
        while (it.hasNext()) {
            currentEntity = it.next();
            preInsert();
            prepareIdValue();
            prepareVersionValue();
            prepareSql();
        }
        currentEntity = null;
        assertEquals(entities.size(), sqls.size());
    }

    protected void preInsert() {
        PreInsertContext context = new AutoBatchPreInsertContext(entityType,
                method, config);
        entityType.preInsert(currentEntity, context);
    }

    @Override
    protected void prepareIdAndVersionPropertyTypes() {
        super.prepareIdAndVersionPropertyTypes();
        generatedIdPropertyType = entityType.getGeneratedIdPropertyType();
        if (generatedIdPropertyType != null) {
            if (idGenerationConfig == null) {
                idGenerationConfig = new IdGenerationConfig(config, entityType);
                generatedIdPropertyType
                        .validateGenerationStrategy(idGenerationConfig);
                autoGeneratedKeysSupported = generatedIdPropertyType
                        .isAutoGeneratedKeysSupported(idGenerationConfig);
                batchSupported = generatedIdPropertyType
                        .isBatchSupported(idGenerationConfig);
            }
        }
    }

    protected void prepareTargetPropertyTypes() {
        targetPropertyTypes = new ArrayList<EntityPropertyType<E, ?>>(
                entityType.getEntityPropertyTypes().size());
        for (EntityPropertyType<E, ?> p : entityType.getEntityPropertyTypes()) {
            if (!p.isInsertable()) {
                continue;
            }
            if (p.isId()) {
                if (p != generatedIdPropertyType
                        || generatedIdPropertyType
                                .isIncluded(idGenerationConfig)) {
                    targetPropertyTypes.add(p);
                }
                if (generatedIdPropertyType == null
                        && p.getWrapper(currentEntity).get() == null) {
                    throw new JdbcException(Message.DOMA2020,
                            entityType.getName(), p.getName());
                }
                continue;
            }
            if (!isTargetPropertyName(p.getName())) {
                continue;
            }
            targetPropertyTypes.add(p);
        }
    }

    protected void prepareIdValue() {
        if (generatedIdPropertyType != null && idGenerationConfig != null) {
            generatedIdPropertyType
                    .preInsert(currentEntity, idGenerationConfig);
        }
    }

    protected void prepareVersionValue() {
        if (versionPropertyType != null) {
            versionPropertyType.setIfNecessary(currentEntity, 1);
        }
    }

    protected void prepareSql() {
        PreparedSqlBuilder builder = new PreparedSqlBuilder(config,
                SqlKind.BATCH_INSERT);
        builder.appendSql("insert into ");
        builder.appendSql(entityType.getQualifiedTableName());
        builder.appendSql(" (");
        for (EntityPropertyType<E, ?> p : targetPropertyTypes) {
            builder.appendSql(p.getColumnName());
            builder.appendSql(", ");
        }
        builder.cutBackSql(2);
        builder.appendSql(") values (");
        for (EntityPropertyType<E, ?> p : targetPropertyTypes) {
            builder.appendWrapper(p.getWrapper(currentEntity));
            builder.appendSql(", ");
        }
        builder.cutBackSql(2);
        builder.appendSql(")");
        PreparedSql sql = builder.build();
        sqls.add(sql);
    }

    @Override
    public boolean isBatchSupported() {
        return batchSupported;
    }

    @Override
    public void generateId(Statement statement, int index) {
        if (generatedIdPropertyType != null && idGenerationConfig != null) {
            generatedIdPropertyType.postInsert(entities.get(index),
                    idGenerationConfig, statement);
        }
    }

    @Override
    public void complete() {
        for (E entity : entities) {
            currentEntity = entity;
            postInsert();
        }
    }

    protected void postInsert() {
        PostInsertContext context = new AutoBatchPostInsertContext(entityType,
                method, config);
        entityType.postInsert(currentEntity, context);
    }

    protected static class AutoBatchPreInsertContext extends
            AbstractPreInsertContext {

        public AutoBatchPreInsertContext(EntityType<?> entityType,
                Method method, Config config) {
            super(entityType, method, config);
        }
    }

    protected static class AutoBatchPostInsertContext extends
            AbstractPostInsertContext {

        public AutoBatchPostInsertContext(EntityType<?> entityType,
                Method method, Config config) {
            super(entityType, method, config);
        }
    }
}
