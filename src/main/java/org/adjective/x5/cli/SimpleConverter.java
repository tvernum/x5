/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adjective.x5.cli;

import java.util.function.Function;

import joptsimple.ValueConverter;

public class SimpleConverter<T> implements ValueConverter<T> {

    private Class<? extends T> type;
    private Function<String, T> conversion;

    public SimpleConverter(Class<? extends T> type, Function<String, T> conversion) {
        this.conversion = conversion;
        this.type = type;
    }

    @Override
    public T convert(String value) {
        return conversion.apply(value);
    }

    @Override
    public Class<? extends T> valueType() {
        return type;
    }

    @Override
    public String valuePattern() {
        return null;
    }
}
