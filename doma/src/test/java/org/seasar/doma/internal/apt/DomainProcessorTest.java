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
package org.seasar.doma.internal.apt;

import org.seasar.doma.internal.apt.domain.AccessorNotFoundDomain;
import org.seasar.doma.internal.apt.domain.ConstrutorNotFoundDomain;
import org.seasar.doma.internal.apt.domain.Outer;
import org.seasar.doma.internal.apt.domain.Salary;
import org.seasar.doma.internal.apt.domain.UnsupportedValueTypeDomain;
import org.seasar.doma.message.DomaMessageCode;

/**
 * @author taedium
 * 
 */
public class DomainProcessorTest extends AptTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addSourcePath("src/test/java");
    }

    public void testSalary() throws Exception {
        DomainProcessor processor = new DomainProcessor();
        addProcessor(processor);
        addCompilationUnit(Salary.class.getName());
        compile();
        assertTrue(getCompiledResult());
    }

    public void testUnsupportedValueType() throws Exception {
        DomainProcessor processor = new DomainProcessor();
        addProcessor(processor);
        addCompilationUnit(UnsupportedValueTypeDomain.class);
        compile();
        assertFalse(getCompiledResult());
        assertMessageCode(DomaMessageCode.DOMA4102);
    }

    public void testConstrutorNotFound() throws Exception {
        DomainProcessor processor = new DomainProcessor();
        addProcessor(processor);
        addCompilationUnit(ConstrutorNotFoundDomain.class);
        compile();
        assertFalse(getCompiledResult());
        assertMessageCode(DomaMessageCode.DOMA4103);
    }

    public void testAccessorNotFound() throws Exception {
        DomainProcessor processor = new DomainProcessor();
        addProcessor(processor);
        addCompilationUnit(AccessorNotFoundDomain.class);
        compile();
        assertFalse(getCompiledResult());
        assertMessageCode(DomaMessageCode.DOMA4104);
    }

    public void testInner() throws Exception {
        DomainProcessor processor = new DomainProcessor();
        addProcessor(processor);
        addCompilationUnit(Outer.class);
        compile();
        assertFalse(getCompiledResult());
        assertMessageCode(DomaMessageCode.DOMA4106);
    }
}