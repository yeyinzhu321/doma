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
package org.seasar.doma.domain;

import java.sql.Array;
import java.sql.SQLException;

import org.seasar.doma.DomaIllegalArgumentException;
import org.seasar.doma.DomaIllegalStateException;

/**
 * @author taedium
 * 
 */
public abstract class AbstractArrayDomain<D extends AbstractArrayDomain<D, E>, E>
        extends AbstractDomain<Array, AbstractArrayDomain<D, E>> {

    protected AbstractArrayDomain() {
        this(null);
    }

    protected AbstractArrayDomain(Array v) {
        super(Array.class, v);
    }

    @SuppressWarnings("unchecked")
    public E[] getArray() {
        try {
            return (E[]) value.getArray();
        } catch (SQLException e) {
            throw new DomaIllegalStateException(e);
        }
    }

    @Override
    public <R, P, TH extends Throwable> R accept(
            DomainVisitor<R, P, TH> visitor, P p) throws TH {
        if (visitor == null) {
            throw new DomaIllegalArgumentException("visitor", visitor);
        }
        if (AbstractArrayDomainVisitor.class.isInstance(visitor)) {
            @SuppressWarnings("unchecked")
            AbstractArrayDomainVisitor<R, P, TH> v = AbstractArrayDomainVisitor.class
                    .cast(visitor);
            return v.visitAbstractArrayDomain(this, p);
        }
        return visitor.visitUnknownDomain(this, p);
    }

}