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
package org.adjective.x5.util;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import org.adjective.x5.types.value.Password;

public class KeyStoreIterator implements Iterator<KeyStoreIterator.Entry> {

    private final KeyStore store;
    private final Iterator<String> aliasIterator;

    public KeyStoreIterator(KeyStore store) throws KeyStoreException {
        this.store = store;
        this.aliasIterator = Collections.list(store.aliases()).iterator();
    }

    @Override
    public boolean hasNext() {
        return aliasIterator.hasNext();
    }

    @Override
    public Entry next() {
        return new Entry(aliasIterator.next());
    }

    public final class Entry {

        private final String alias;

        public Entry(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }

        public boolean isCertificate() throws KeyStoreException {
            return store.isCertificateEntry(this.alias);
        }

        public boolean isKey() throws KeyStoreException {
            return store.isKeyEntry(this.alias);
        }

        public Optional<? extends Certificate> getCertificate() throws KeyStoreException {
            if (store.isCertificateEntry(alias)) {
                return Optional.of(store.getCertificate(alias));
            } else {
                return Optional.empty();
            }
        }

        public Optional<Tuple<Key, Certificate[]>> getKeyPair(Password password) throws GeneralSecurityException {
            if (store.isKeyEntry(alias)) {
                return Optional.of(new Tuple<>(store.getKey(alias, password.chars()), store.getCertificateChain(alias)));
            } else {
                return Optional.empty();
            }
        }

        public void copyTo(KeyStore otherStore, Password sourcePassword, Password destPassword) throws GeneralSecurityException {
            if (this.isCertificate()) {
                otherStore.setCertificateEntry(this.alias, store.getCertificate(this.alias));
            } else {
                otherStore.setKeyEntry(
                    this.alias,
                    store.getKey(alias, sourcePassword.chars()),
                    destPassword.chars(),
                    store.getCertificateChain(alias)
                );
            }
        }
    }

}
