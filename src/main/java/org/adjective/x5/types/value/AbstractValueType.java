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

package org.adjective.x5.types.value;

import java.util.Objects;
import java.util.Optional;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.X5Value;
import org.adjective.x5.util.Values;

public abstract class AbstractValueType<T> implements X5Value<T> {
    protected final T value;
    protected final X5StreamInfo source;

    public AbstractValueType(T value, X5StreamInfo source) {
        this.value = value;
        this.source = source;
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public boolean isEqualTo(X5Object other) throws X5Exception {
        return other instanceof X5Value && Objects.deepEquals(this.value, ((X5Value) other).value());
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public String description() {
        return toTextValue() + " (" + getTypeName() + ")";
    }

    @Override
    public <X extends X5Object> Optional<X> as(X5Type<X> type) {
        if (type == X5Type.STRING && this.value instanceof CharSequence) {
            return Optional.of(type.cast(Values.string(String.valueOf(value), source)));
        }
        return X5Value.super.as(type);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
