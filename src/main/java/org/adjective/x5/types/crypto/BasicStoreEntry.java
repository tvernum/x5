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

package org.adjective.x5.types.crypto;

import java.util.Objects;
import java.util.Optional;

import org.adjective.x5.types.CryptoStore;
import org.adjective.x5.types.CryptoValue;
import org.adjective.x5.types.X5StreamInfo;

public class BasicStoreEntry extends AbstractStoreEntry {
    private final Optional<CryptoStore> store;

    public BasicStoreEntry(CryptoStore store, String name, CryptoValue value) {
        this(Optional.of(store), name, value);
    }

    public BasicStoreEntry(Optional<CryptoStore> store, String name, CryptoValue value) {
        super(name, value);
        this.store = Objects.requireNonNull(store);
    }

    @Override
    protected Optional<CryptoStore> keyStore() {
        return store;
    }

    @Override
    public X5StreamInfo getSource() {
        return value.getSource().withDescriptionPrefix("entry from");
    }

}
