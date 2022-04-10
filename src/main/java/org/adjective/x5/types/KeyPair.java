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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.adjective.x5.exception.X5Exception;

public interface KeyPair extends CryptoValue, ToTextValue {

    PrivateCredential privateCredential();

    PublicCredential publicCredential();

    @Override
    default boolean isEqualTo(X5Object obj) throws X5Exception {
        if (obj instanceof KeyPair) {
            KeyPair other = (KeyPair) obj;
            return this.privateCredential().isEqualTo(other.privateCredential())
                && this.publicCredential().isEqualTo(other.publicCredential());
        }
        return false;
    }

    @Override
    default <X extends X5Object> Optional<X> as(Class<X> type) {
        if (type.isInstance(this)) {
            return Optional.of(type.cast(this));
        }
        if (type.isAssignableFrom(Sequence.class)) {
            return Optional.of(type.cast(new ObjectSequence(List.of(privateCredential(), publicCredential()), getSource())));
        }
        if (type.isInstance(privateCredential())) {
            return Optional.of(type.cast(privateCredential()));
        }
        if (type.isInstance(publicCredential())) {
            return Optional.of(type.cast(publicCredential()));
        }
        return Optional.empty();
    }

    @Override
    default X5Type getType() {
        return X5Type.KEY_PAIR;
    }

    @Override
    default String description() {
        return toTextValue() + " : " + getSource().getSourceDescription();
    }

    @Override
    default String toTextValue() {
        return getTypeName() + "{" + privateCredential().getTypeName() + "," + publicCredential().getTypeName() + "}";
    }

    @Override
    default Map<String, ? extends X5Object> properties() {
        final Map<String, X5Object> map = new LinkedHashMap<>();
        map.put("private", privateCredential());
        map.put("public", publicCredential());
        return map;
    }

}
