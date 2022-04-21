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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.adjective.x5.command.ToCommand;
import org.adjective.x5.exception.DuplicateEntryException;
import org.adjective.x5.exception.InvalidTargetException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.io.encrypt.Unencrypted;
import org.adjective.x5.types.CryptoStore;
import org.adjective.x5.types.StoreEntry;
import org.adjective.x5.types.X5StreamInfo;

public class SimpleKeyStore implements CryptoStore {

    private final X5StreamInfo source;
    private final EncryptionInfo storeEncryption;
    private final List<StoreEntry> entries;
    private final Map<StoreEntry, EncryptionInfo> entryEncryption;

    public SimpleKeyStore(X5StreamInfo source) {
        this.source = source;
        this.storeEncryption = Unencrypted.INSTANCE;
        this.entries = new ArrayList<>();
        this.entryEncryption = new HashMap<>();
    }

    public SimpleKeyStore(
        X5StreamInfo source,
        EncryptionInfo storeEncryption,
        List<StoreEntry> entries,
        Map<StoreEntry, EncryptionInfo> entryEncryption
    ) {
        this.source = source;
        this.storeEncryption = storeEncryption;
        this.entries = entries;
        this.entryEncryption = entryEncryption;
    }

    @Override
    public List<StoreEntry> entries() throws X5Exception {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public void addEntry(StoreEntry entry, Optional<EncryptionInfo> encryption) throws X5Exception {
        if (this.findEntry(entry.name()).isPresent()) {
            throw new DuplicateEntryException("Store entry '" + entry.name() + "' already exists");
        }
        entries.add(entry);
        entryEncryption.put(entry, encryption.orElse(storeEncryption));
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        throw new InvalidTargetException(
            this,
            "Cannot write an in-memory keystore to disk - use the `" + ToCommand.NAME + "` command to convert it to PKCS#12 or JKS first"
        );
    }

    @Override
    public EncryptionInfo encryption() {
        return this.storeEncryption;
    }

    @Override
    public Optional<EncryptionInfo> getEncryption(StoreEntry entry) {
        return Optional.ofNullable(this.entryEncryption.get(entry));
    }

    @Override
    public EncryptedObject withEncryption(EncryptionInfo encryption, boolean recurse) {
        final Map<StoreEntry, EncryptionInfo> entryEncryption;
        if (recurse) {
            entryEncryption = this.entryEncryption.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, kv -> {
                if (this.storeEncryption.equals(kv.getValue())) {
                    return encryption;
                } else {
                    return kv.getValue();
                }
            }));
        } else {
            entryEncryption = new HashMap<>(this.entryEncryption);
        }
        return new SimpleKeyStore(source, encryption, new ArrayList<>(entries), entryEncryption);
    }

}
