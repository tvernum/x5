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

package org.adjective.x5.io.encrypt;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.Algorithm;
import org.adjective.x5.types.value.Password;
import org.adjective.x5.util.Lazy;
import org.adjective.x5.util.Values;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;

public class Pkcs8EncryptionInfo extends AbstractEncryptionInfo implements EncryptionInfo {
    private final AlgorithmIdentifier algorithm;
    private final Optional<EncryptionScheme> encryptionScheme;
    private final byte[] data;
    private final Password password;
    private final Supplier<Map<String, X5Object>> properties;

    public Pkcs8EncryptionInfo(X5StreamInfo source, PKCS8EncryptedPrivateKeyInfo encrypted, Password password) {
        this(source, encrypted.toASN1Structure(), password);
    }

    public Pkcs8EncryptionInfo(X5StreamInfo source, EncryptedPrivateKeyInfo encrypted, Password password) {
        this(source, encrypted.getEncryptionAlgorithm(), encrypted.getEncryptedData(), password);
    }

    public Pkcs8EncryptionInfo(X5StreamInfo source, AlgorithmIdentifier algorithm, byte[] data, Password password) {
        super(source);
        this.algorithm = algorithm;
        this.data = data;
        this.password = password;
        this.encryptionScheme = Optional.of(this.algorithm)
            .filter(alg -> alg.getAlgorithm().equals(PKCSObjectIdentifiers.id_PBES2))
            .map(alg -> PBES2Parameters.getInstance(alg.getParameters()))
            .map(p -> p.getEncryptionScheme());
        this.properties = Lazy.lazy(() -> {
            Map<String, X5Object> map = new LinkedHashMap<>();
            map.putAll(super.properties());
            if (data != null) {
                map.put("parameters.pkcs8.raw", Values.binary(data));
            }
            return map;
        }).unchecked();
    }

    @Override
    public Password password() {
        return password;
    }

    @Override
    public Pkcs8EncryptionInfo withPassword(Password withPassword) {
        return new Pkcs8EncryptionInfo(getSource(), algorithm, data, withPassword);
    }

    @Override
    public boolean isEncrypted() {
        return true;
    }

    @Override
    public Map<String, X5Object> properties() {
        return properties.get();
    }

    @Override
    protected boolean isEqualTo(EncryptionInfo enc) {
        if (enc instanceof Pkcs8EncryptionInfo) {
            Pkcs8EncryptionInfo other = (Pkcs8EncryptionInfo) enc;
            return this.algorithm.equals(other.algorithm) && this.password.isEqualTo(other.password);
        }
        return false;
    }

    @Override
    public Optional<String> getPkcs1DekAlgorithm() {
        // TODO
        return Optional.empty();
    }

    @Override
    public Optional<Algorithm> getPkcs8Algorithm() {
        return Optional.of(Values.algorithm(algorithm, getSource()));
    }

    @Override
    public Optional<Algorithm> getEncryptionScheme() {
        return encryptionScheme.map(es -> Values.algorithm(es.getAlgorithm(), getSource().withDescriptionPrefix("PBES2:")))
            .or(this::getPkcs8Algorithm);
    }

    @Override
    public String toString() {
        if (encryptionScheme.isEmpty()) {
            return "Pkcs#8EncryptionInfo{" + algorithm + '}';
        } else {
            return "Pkcs#8EncryptionInfo{" + algorithm + " ; " + encryptionScheme.get() + '}';
        }
    }
}
