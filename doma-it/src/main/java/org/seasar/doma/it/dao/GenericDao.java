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
package org.seasar.doma.it.dao;

import java.util.List;

import org.seasar.doma.BatchDelete;
import org.seasar.doma.BatchInsert;
import org.seasar.doma.BatchUpdate;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Update;

@org.seasar.doma.GenericDao
public interface GenericDao<E> {

    @Insert
    int insert(E entity);

    @Insert(excludeNull = true)
    int insert_excludesNull(E entity);

    @BatchInsert
    int[] insert(List<E> entities);

    @Update
    int update(E entity);

    @Update(excludeNull = true)
    int update_excludesNull(E entity);

    @Update(includeVersion = true)
    int update_includesVersion(E entity);

    @Update(suppressOptimisticLockException = true)
    int update_suppressesOptimisticLockException(E entity);

    @BatchUpdate
    int[] update(List<E> entities);

    @BatchUpdate(includesVersion = true)
    int[] update_includesVersion(List<E> entities);

    @BatchUpdate(suppressOptimisticLockException = true)
    int[] update_suppressesOptimisticLockException(List<E> entities);

    @Delete
    int delete(E entity);

    @Delete(ignoreVersion = true)
    int delete_ignoresVersion(E entity);

    @Delete(suppressOptimisticLockException = true)
    int delete_suppressesOptimisticLockException(E entity);

    @BatchDelete
    int[] delete(List<E> entity);

    @BatchDelete(ignoreVersion = true)
    int[] delete_ignoresVersion(List<E> entity);

    @BatchDelete(suppressOptimisticLockException = true)
    int[] delete_suppressesOptimisticLockException(List<E> entity);
}