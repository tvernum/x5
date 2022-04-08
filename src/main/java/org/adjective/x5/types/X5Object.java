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

package org.adjective.x5.types;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;

import org.adjective.x5.exception.X5Exception;

/**
 * The base interface for all types in X5
 */
public interface X5Object {
    X5StreamInfo getSource();

    X5Type getType();

    default String getTypeName() {
        return getType().name();
    };

    default String description() {
        return getTypeName() + " : " + getSource().getSourceDescription();
    }

    Map<String, ? extends X5Object> properties();

    void writeTo(OutputStream out) throws IOException, X5Exception;

    boolean isEqualTo(String str);

    boolean isEqualTo(X5Object other) throws X5Exception;

    default <X extends X5Object> Optional<X> as(Class<X> type) {
        if (type.isInstance(this)) {
            return Optional.of(type.cast(this));
        } else {
            return Optional.empty();
        }
    }

    default <X extends X5Object> Optional<X> as(X5Type<X> type) {
        return as(type.objectClass());
    }
}
