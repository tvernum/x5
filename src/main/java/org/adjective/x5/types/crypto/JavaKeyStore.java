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
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.adjective.x5.exception.CryptoStoreException;
import org.adjective.x5.exception.InvalidTargetException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.Debug;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.types.CryptoStore;
import org.adjective.x5.types.CryptoValue;
import org.adjective.x5.types.KeyPair;
import org.adjective.x5.types.PrivateCredential;
import org.adjective.x5.types.PublicCredential;
import org.adjective.x5.types.StoreEntry;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.OID;
import org.adjective.x5.util.Lazy;
import org.adjective.x5.util.ObjectIdentifiers;
import org.adjective.x5.util.Values;

public class JavaKeyStore implements CryptoStore {
    private final KeyStore keyStore;
    private final X5StreamInfo source;
    private final EncryptionInfo encryption;
    private final Map<String, EncryptionInfo> encryptionByEntry;

    private final Lazy<List<StoreEntry>, X5Exception> entries;

    public JavaKeyStore(KeyStore keyStore, X5StreamInfo source, EncryptionInfo encryption) {
        this.keyStore = keyStore;
        this.source = source;
        this.encryption = encryption;
        this.encryptionByEntry = new HashMap<>();
        this.entries = Lazy.lazy(() -> {
            try {
                final List<StoreEntry> list = new ArrayList<>(keyStore.size());
                final Enumeration<String> e = keyStore.aliases();
                while (e.hasMoreElements()) {
                    String alias = e.nextElement();
                    if (keyStore.isCertificateEntry(alias)) {
                        list.add(certificateEntry(alias, keyStore.getCertificate(alias)));
                    } else if (keyStore.isKeyEntry(alias)) {
                        // TODO support a different password for the key
                        list.add(
                            keyEntry(alias, keyStore.getKey(alias, encryption.password().chars()), keyStore.getCertificateChain(alias))
                        );
                    }
                }
                return list;
            } catch (GeneralSecurityException e) {
                throw new CryptoStoreException("Cannot process " + source.getSourceDescription(), e);
            }
        });
    }

    private JavaKeyStore(
        KeyStore keyStore,
        X5StreamInfo source,
        EncryptionInfo encryption,
        Lazy<List<StoreEntry>, X5Exception> entries,
        Map<String, EncryptionInfo> encryptionByEntry
    ) {
        this.keyStore = keyStore;
        this.source = source;
        this.encryption = encryption;
        this.entries = entries;
        this.encryptionByEntry = encryptionByEntry;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        try {
            keyStore.store(out, encryption.password().chars());
        } catch (GeneralSecurityException e) {
            throw new CryptoStoreException("Cannot export KeyStore " + this, e);
        }
    }

    @Override
    public boolean isEqualTo(X5Object other) throws X5Exception {
        return other instanceof JavaKeyStore && this.keyStore.equals(((JavaKeyStore) other).keyStore);
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public List<StoreEntry> entries() throws X5Exception {
        return Collections.unmodifiableList(this.entries.get());
    }

    @Override
    public void addEntry(StoreEntry entry, Optional<EncryptionInfo> encryption) throws X5Exception {
        final CryptoValue value = entry.value();
        if (value instanceof org.adjective.x5.types.Certificate) {
            addCertificateEntry(entry.name(), (org.adjective.x5.types.Certificate) value);
        } else if (value instanceof KeyPair) {
            addKeyPair(entry.name(), (KeyPair) value, encryption);
        } else {
            throw new InvalidTargetException(
                entry.value(),
                "Cannot store entry " + entry + " with type " + entry.value().getTypeName() + " in " + this
            );
        }
        this.entries.clear();
    }

    private void addKeyPair(String name, KeyPair pair, Optional<EncryptionInfo> encryption) throws X5Exception {
        try {
            final EncryptionInfo entryEncryption = encryption.orElse(this.encryption);
            this.keyStore.setKeyEntry(
                name,
                JCAConversion.key(pair.privateCredential()),
                entryEncryption.password().chars(),
                JCAConversion.chain(pair.publicCredential())
            );
            this.encryptionByEntry.put(name, entryEncryption);
        } catch (KeyStoreException e) {
            throw new CryptoStoreException("Failed to store entry " + name + " of " + pair, e);
        }
    }

    private void addCertificateEntry(String name, org.adjective.x5.types.Certificate certificate) throws X5Exception {
        try {
            this.keyStore.setCertificateEntry(name, JCAConversion.certificate(certificate));
        } catch (KeyStoreException e) {
            throw new CryptoStoreException("Failed to store entry " + name + " of " + certificate, e);
        }
    }

    @Override
    public EncryptionInfo encryption() {
        return this.encryption;
    }

    @Override
    public Optional<EncryptionInfo> getEncryption(StoreEntry entry) {
        return Optional.ofNullable(encryptionByEntry.get(entry.name()));
    }

    @Override
    public JavaKeyStore withEncryption(EncryptionInfo withEncryption) throws X5Exception {
        // TODO: Change key password if it's the same as the store encryption?
        return new JavaKeyStore(keyStore, source, withEncryption, this.entries, this.encryptionByEntry);
    }

    private KeyStoreEntry certificateEntry(String name, Certificate certificate) {
        final X5StreamInfo entrySource = entrySource(name);
        JavaCertificate<?> certObj = JavaCertificate.create(certificate, entrySource);
        final Map<String, X5Object> properties = readProperties(name, entrySource);
        properties.put("certificate", certObj);
        return new KeyStoreEntry(name, entrySource, certObj, properties);
    }

    private KeyStoreEntry keyEntry(String name, Key key, Certificate[] certificates) throws CryptoStoreException {
        final X5StreamInfo entrySource = entrySource(name);
        final Map<String, X5Object> properties = readProperties(name, entrySource);

        final PublicCredential publicCredential;
        final PrivateCredential privateCredential;
        if (certificates == null || certificates.length == 0) {
            publicCredential = null;
        } else {
            publicCredential = new JavaCertificateChain(certificates, entrySource);
        }
        if (key instanceof PrivateKey) {
            privateCredential = new JavaPrivateKey((PrivateKey) key, entrySource, encryption);
        } else if (key instanceof SecretKey) {
            privateCredential = new JavaSecretKey((SecretKey) key, entrySource);
        } else {
            privateCredential = new GenericPrivateCredential(key, entrySource);
        }

        properties.put("private", privateCredential);
        if (publicCredential == null) {
            properties.put("public", Values.nullValue(source));
            return new KeyStoreEntry(name, entrySource, privateCredential, properties);
        } else {
            properties.put("public", publicCredential);
            return new KeyStoreEntry(name, entrySource, new BasicKeyPair(privateCredential, publicCredential, entrySource), properties);
        }
    }

    private Map<String, X5Object> readProperties(String name, X5StreamInfo entrySource) {
        final Map<String, X5Object> properties = new LinkedHashMap<>();
        try {
            KeyStore.PasswordProtection protection = keyStore.isKeyEntry(name)
                ? new KeyStore.PasswordProtection(encryption.password().chars())
                : null;
            KeyStore.Entry e = keyStore.getEntry(name, protection);
            for (KeyStore.Entry.Attribute attribute : e.getAttributes()) {
                String attrName = attribute.getName();
                OID oid = new OID(attrName, source.withDescriptionPrefix("attribute in"));
                Optional<String> oidName = ObjectIdentifiers.name(oid);
                if (oidName.isPresent()) {
                    attrName = oidName.get();
                }
                properties.put(attrName, Values.string(attribute.getValue(), entrySource));
            }
        } catch (GeneralSecurityException e) {
            Debug.error(e, "Failed to read attributes for " + entrySource.getSourceDescription());
            // Ignore
        }
        return properties;
    }

    private X5StreamInfo entrySource(String name) {
        return getSource().withDescriptionPrefix("Entry[" + name + "] in");
    }

    public final class KeyStoreEntry extends AbstractStoreEntry {
        private final X5StreamInfo source;
        private final Map<String, X5Object> properties;

        private KeyStoreEntry(String name, X5StreamInfo source, CryptoValue value, Map<String, X5Object> properties) {
            super(name, value);
            this.source = source;
            this.properties = new LinkedHashMap<>();
            this.properties.putAll(super.properties());
            this.properties.putAll(properties);
        }

        @Override
        protected Optional<JavaKeyStore> keyStore() {
            return Optional.of(JavaKeyStore.this);
        }

        @Override
        public X5StreamInfo getSource() {
            return this.source;
        }

        @Override
        public Map<String, X5Object> properties() {
            return this.properties;
        }

    }
}
