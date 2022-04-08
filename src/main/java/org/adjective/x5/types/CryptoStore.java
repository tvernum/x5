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
import java.util.stream.Collectors;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.Debug;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.types.crypto.EncryptedObject;

public interface CryptoStore extends X5Object, EncryptedObject, Sequence {

    List<StoreEntry> entries() throws X5Exception;

    void addEntry(StoreEntry entry, Optional<EncryptionInfo> encryption) throws X5Exception;

    @Override
    default Map<String, X5Object> properties() {
        try {
            return entries().stream().collect(Collectors.toMap(e -> "entry." + e.name(), StoreEntry::value));
        } catch (X5Exception e) {
            Debug.error(e, "Cannot read entries of %s", this.description());
            return Map.of();
        }
    }

    @Override
    default boolean isEqualTo(String str) {
        return false;
    }

    @Override
    default Iterable<? extends X5Object> items() throws X5Exception {
        return entries();
    }

    @Override
    default X5Type getType() {
        return X5Type.STORE;
    }

}
