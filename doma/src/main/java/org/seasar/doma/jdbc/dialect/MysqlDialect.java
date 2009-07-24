/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.doma.jdbc.dialect;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.seasar.doma.DomaIllegalArgumentException;
import org.seasar.doma.domain.BigIntegerDomain;
import org.seasar.doma.domain.BlobDomain;
import org.seasar.doma.domain.BooleanDomain;
import org.seasar.doma.domain.ByteDomain;
import org.seasar.doma.domain.BytesDomain;
import org.seasar.doma.domain.ClobDomain;
import org.seasar.doma.domain.DateDomain;
import org.seasar.doma.domain.Domain;
import org.seasar.doma.domain.IntegerDomain;
import org.seasar.doma.domain.LongDomain;
import org.seasar.doma.domain.ShortDomain;
import org.seasar.doma.domain.TimestampDomain;
import org.seasar.doma.internal.jdbc.dialect.MysqlPagingTransformer;
import org.seasar.doma.jdbc.JdbcMappingVisitor;
import org.seasar.doma.jdbc.SelectForUpdateType;
import org.seasar.doma.jdbc.SqlLogFormattingVisitor;
import org.seasar.doma.jdbc.SqlNode;

/**
 * @author taedium
 * 
 */
public class MysqlDialect extends StandardDialect {

    protected static final Set<Integer> UNIQUE_CONSTRAINT_VIOLATION_ERROR_CODES = new HashSet<Integer>(
            Arrays.asList(1022, 1062));

    public MysqlDialect() {
        this(new MysqlJdbcMappingVisitor(), new MysqlSqlLogFormattingVisitor());
    }

    public MysqlDialect(JdbcMappingVisitor jdbcMappingVisitor,
            SqlLogFormattingVisitor sqlLogFormattingVisitor) {
        super(jdbcMappingVisitor, sqlLogFormattingVisitor);

        domainClassMap.put("bigint unsigned", BigIntegerDomain.class);
        domainClassMap.put("datetime", TimestampDomain.class);
        domainClassMap.put("int", IntegerDomain.class);
        domainClassMap.put("int unsigned", LongDomain.class);
        domainClassMap.put("longblob", BlobDomain.class);
        domainClassMap.put("longtext", ClobDomain.class);
        domainClassMap.put("mediumblob", BlobDomain.class);
        domainClassMap.put("mediumint", IntegerDomain.class);
        domainClassMap.put("mediumint unsigned", IntegerDomain.class);
        domainClassMap.put("mediumtext", ClobDomain.class);
        domainClassMap.put("smallint unsigned", IntegerDomain.class);
        domainClassMap.put("tinyblob", BlobDomain.class);
        domainClassMap.put("tinyint", ByteDomain.class);
        domainClassMap.put("tinyint unsigned", ShortDomain.class);
        domainClassMap.put("tinytext", ClobDomain.class);
        domainClassMap.put("text", ClobDomain.class);
        domainClassMap.put("year", DateDomain.class);
    }

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public boolean isUniqueConstraintViolated(SQLException sqlException) {
        if (sqlException == null) {
            throw new DomaIllegalArgumentException("sqlException", sqlException);
        }
        int code = getErrorCode(sqlException);
        return UNIQUE_CONSTRAINT_VIOLATION_ERROR_CODES.contains(code);
    }

    @Override
    public boolean supportsAutoGeneratedKeys() {
        return true;
    }

    @Override
    public boolean supportsIdentity() {
        return true;
    }

    @Override
    public boolean supportsSelectForUpdate(SelectForUpdateType type,
            boolean withTargets) {
        return type == SelectForUpdateType.NORMAL && !withTargets;
    }

    @Override
    protected SqlNode toPagingSqlNode(SqlNode sqlNode, int offset, int limit) {
        MysqlPagingTransformer transformer = new MysqlPagingTransformer(offset,
                limit);
        return transformer.transform(sqlNode);
    }

    @Override
    public Class<? extends Domain<?, ?>> getDomainClass(String typeName,
            int sqlType, int length, int precision, int scale) {
        if ("bit".equalsIgnoreCase(typeName)) {
            return length == 1 ? BooleanDomain.class : BytesDomain.class;
        }
        return super
                .getDomainClass(typeName, sqlType, length, precision, scale);
    }

    @Override
    public SqlBlockContext createSqlBlockContext() {
        return new MysqlSqlBlockContext();
    }

    @Override
    public String getSqlBlockDelimiter() {
        return "/";
    }

    public static class MysqlJdbcMappingVisitor extends
            StandardJdbcMappingVisitor {
    }

    public static class MysqlSqlLogFormattingVisitor extends
            StandardSqlLogFormattingVisitor {
    }

    public static class MysqlSqlBlockContext extends StandardSqlBlockContext {

        protected MysqlSqlBlockContext() {
            sqlBlockStartKeywordsList.add(Arrays.asList("create", "procedure"));
            sqlBlockStartKeywordsList.add(Arrays.asList("create", "function"));
            sqlBlockStartKeywordsList.add(Arrays.asList("create", "trigger"));
            sqlBlockStartKeywordsList.add(Arrays.asList("alter", "procedure"));
            sqlBlockStartKeywordsList.add(Arrays.asList("alter", "function"));
            sqlBlockStartKeywordsList.add(Arrays.asList("alter", "trigger"));
            sqlBlockStartKeywordsList.add(Arrays.asList("declare"));
            sqlBlockStartKeywordsList.add(Arrays.asList("begin"));
        }
    }

}
