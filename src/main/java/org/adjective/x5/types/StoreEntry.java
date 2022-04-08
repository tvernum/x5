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

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StoreEntry extends X5Object {

    String name();

    CryptoValue value();

    @Override
    default <X extends X5Object> Optional<X> as(Class<X> type) {
        if (type.isInstance(this)) {
            return Optional.of(type.cast(this));
        }
        if (type.isAssignableFrom(Sequence.class)) {
            return Optional.of(type.cast(new ObjectSequence(List.of(value()), getSource())));
        }
        if (type.isAssignableFrom(X5Record.class)) {
            return Optional.of(type.cast(new FixedRecord(Map.of(name(), value()), getSource())));
        }
        return Optional.empty();
    }

    @Override
    default X5Type getType() {
        return X5Type.STORE_ENTRY;
    }

}
