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

import org.seasar.doma.internal.jdbc.entity.AbstractPostDeleteContext;
import org.seasar.doma.internal.jdbc.entity.AbstractPreDeleteContext;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.SqlKind;
import org.seasar.doma.jdbc.entity.EntityType;
import org.seasar.doma.jdbc.entity.VersionPropertyType;

/**
 * @author taedium
 * 
 */
public class SqlFileDeleteQuery extends SqlFileModifyQuery implements
        DeleteQuery {

    protected EntityHandler<?> entityHandler;

    protected boolean versionIgnored;

    protected boolean optimisticLockExceptionSuppressed;

    public SqlFileDeleteQuery() {
        super(SqlKind.DELETE);
    }

    @Override
    public void prepare() {
        assertNotNull(method, config, sqlFilePath, callerClassName,
                callerMethodName);
        preDelete();
        prepareOptions();
        prepareOptimisticLock();
        prepareSql();
        assertNotNull(sql);
    }

    protected void preDelete() {
        if (entityHandler != null) {
            entityHandler.preDelete();
        }
    }

    protected void prepareOptimisticLock() {
        if (entityHandler != null) {
            entityHandler.prepareOptimisticLock();
        }
    }

    @Override
    public void complete() {
        if (entityHandler != null) {
            entityHandler.postDelete();
        }
    }

    @Override
    public <E> void setEntityAndEntityType(E entity, EntityType<E> entityType) {
        entityHandler = new EntityHandler<E>(entity, entityType);
    }

    public void setVersionIgnored(boolean versionIgnored) {
        this.versionIgnored = versionIgnored;
    }

    public void setOptimisticLockExceptionSuppressed(
            boolean optimisticLockExceptionSuppressed) {
        this.optimisticLockExceptionSuppressed = optimisticLockExceptionSuppressed;
    }

    protected class EntityHandler<E> {

        protected E entity;

        protected EntityType<E> entityType;

        protected VersionPropertyType<? super E, E, ?, ?> versionPropertyType;

        protected EntityHandler(E entity, EntityType<E> entityType) {
            assertNotNull(entity, entityType);
            this.entity = entity;
            this.entityType = entityType;
            this.versionPropertyType = entityType.getVersionPropertyType();
        }

        protected void preDelete() {
            SqlFilePreDeleteContext context = new SqlFilePreDeleteContext(
                    entityType, method, config);
            entityType.preDelete(entity, context);
        }

        protected void postDelete() {
            SqlFilePostDeleteContext context = new SqlFilePostDeleteContext(
                    entityType, method, config);
            entityType.postDelete(entity, context);
        }

        protected void prepareOptimisticLock() {
            if (versionPropertyType != null && !versionIgnored) {
                if (!optimisticLockExceptionSuppressed) {
                    optimisticLockCheckRequired = true;
                }
            }
        }
    }

    protected static class SqlFilePreDeleteContext extends
            AbstractPreDeleteContext {

        public SqlFilePreDeleteContext(EntityType<?> entityType, Method method,
                Config config) {
            super(entityType, method, config);
        }
    }

    protected static class SqlFilePostDeleteContext extends
            AbstractPostDeleteContext {

        public SqlFilePostDeleteContext(EntityType<?> entityType,
                Method method, Config config) {
            super(entityType, method, config);
        }
    }
}
