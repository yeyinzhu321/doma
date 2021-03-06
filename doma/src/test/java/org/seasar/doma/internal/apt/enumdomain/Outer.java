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
package org.seasar.doma.internal.apt.enumdomain;

import org.seasar.doma.EnumDomain;

@SuppressWarnings("deprecation")
public class Outer {

    @EnumDomain(valueType = String.class)
    enum Inner {

        AAA("01"), BBB("02");

        private final String value;

        private Inner(String value) {
            this.value = value;
        }

        static Inner of(String value) {
            for (Inner inner : Inner.values()) {
                if (inner.value.equals(value)) {
                    return inner;
                }
            }
            return null;
        }

        String getValue() {
            return value;
        }

    }
}
