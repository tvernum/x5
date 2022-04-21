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

import static org.adjective.x5.util.Values.bool;
import static org.adjective.x5.util.Values.string;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.adjective.x5.exception.EncryptionException;
import org.adjective.x5.io.PemOutput;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.types.EncodingSyntax;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5PrivateKey;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.Lazy;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

public class JavaPrivateKey implements X5PrivateKey, JavaKey {

    private final PrivateKey key;
    private final Optional<EncodingSyntax> syntax;
    private final X5StreamInfo source;
    private final EncryptionInfo encryption;
    private final Supplier<Map<String, X5Object>> properties;

    public JavaPrivateKey(PrivateKey key, X5StreamInfo source, EncryptionInfo encryption) {
        this.key = key;
        this.syntax = parseKeyFormat(key.getFormat());
        this.source = syntax.map(source::withSyntax).orElse(source);
        this.encryption = encryption;
        this.properties = Lazy.uncheckedLazy(() -> {
            Map<String, X5Object> map = new java.util.LinkedHashMap<>();
            map.put("algorithm", string(key.getAlgorithm(), getSource()));
            map.put("format", string(key.getFormat(), getSource()));
            map.put("encrypted", bool(encryption.isEncrypted(), getSource()));
            return map;
        }).unchecked();
    }

    @Override
    public String getKeyType() {
        return key.getAlgorithm();
    }

    private Optional<EncodingSyntax> parseKeyFormat(String format) {
        // TODO more formats?
        if ("PKCS#8".equals(format)) {
            return Optional.of(EncodingSyntax.PKCS8);
        } else {
            return Optional.empty();
        }
    }

    public PrivateKey key() {
        return key;
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public EncryptionInfo encryption() {
        return this.encryption;
    }

    @Override
    public JavaPrivateKey withEncryption(EncryptionInfo withEncryption, boolean recurse) {
        return new JavaPrivateKey(this.key, this.source, withEncryption);
    }

    @Override
    public Map<String, X5Object> properties() {
        return properties.get();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, EncryptionException {
        PrivateKeyInfo pemKey = PrivateKeyInfo.getInstance(key.getEncoded());
        PemOutput.write(pemKey, out, encryption, syntax.orElse(EncodingSyntax.PKCS8));
    }
}
