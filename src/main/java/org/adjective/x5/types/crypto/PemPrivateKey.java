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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.adjective.x5.exception.EncryptionException;
import org.adjective.x5.exception.UnencodableObjectException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.PemOutput;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.types.EncodingSyntax;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5PrivateKey;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.Algorithm;
import org.adjective.x5.util.Lazy;
import org.adjective.x5.util.ObjectIdentifiers;
import org.adjective.x5.util.Values;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

public class PemPrivateKey implements X5PrivateKey {

    private final PrivateKeyInfo key;
    private final X5StreamInfo source;
    private final EncryptionInfo encryption;
    private final Supplier<Map<String, X5Object>> properties;
    private final Algorithm algorithm;

    public PemPrivateKey(PrivateKeyInfo key, X5StreamInfo source, EncryptionInfo encryption) {
        this.key = Objects.requireNonNull(key, "Key cannot be null");
        this.source = Objects.requireNonNull(source, "Source cannot be null");
        this.encryption = Objects.requireNonNull(encryption, "EncryptionInfo cannot be null");
        this.algorithm = Values.algorithm(key.getPrivateKeyAlgorithm(), getSource().withDescriptionPrefix("algorithm for"));
        this.properties = Lazy.uncheckedLazy(() -> {
            Map<String, X5Object> map = new LinkedHashMap<>();
            map.put("algorithm", algorithm);
            map.put("type", Values.string(getKeyType(), getSource()));
            map.put("encrypted", Values.bool(encryption().isEncrypted(), getSource()));
            if (encryption().isEncrypted()) {
                map.put("encryption", encryption());
            }
            ECKeyInfo.getAttributes(key, getSource()).entrySet().forEach(e -> map.put("ec." + e.getKey(), e.getValue()));
            return map;
        }).unchecked();
    }

    @Override
    public String getKeyType() {
        return ObjectIdentifiers.friendlyName(algorithm.oid()).orElse(algorithm.oid().value());
    }

    public PrivateKeyInfo getKey() {
        return key;
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public EncryptionInfo encryption() {
        return encryption;
    }

    @Override
    public PemPrivateKey withEncryption(EncryptionInfo newEncryption) {
        return new PemPrivateKey(this.key, this.source, newEncryption);
    }

    @Override
    public Map<String, X5Object> properties() {
        return properties.get();
    }

    @Override
    public byte[] encodedValue() throws X5Exception {
        try {
            return this.key.getEncoded();
        } catch (IOException e) {
            throw new UnencodableObjectException("Cannot get encoding of " + this, e);
        }
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, EncryptionException {
        PemOutput.write(this.key, out, encryption, source.getSyntax().orElse(EncodingSyntax.PKCS1));
    }
}
