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

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.OID;
import org.adjective.x5.types.value.Password;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class Pkcs12EncryptionInfo extends AbstractEncryptionInfo implements EncryptionInfo {
    private final ASN1ObjectIdentifier digestId;
    private final Password password;

    public Pkcs12EncryptionInfo(X5StreamInfo source, OID digestId, Password password) throws IOException {
        this(source, ASN1ObjectIdentifier.getInstance(digestId.bytes()), password);
    }

    public Pkcs12EncryptionInfo(X5StreamInfo source, AlgorithmIdentifier digestAlgorithm, Password password) {
        this(source, digestAlgorithm.getAlgorithm(), password);
    }

    public Pkcs12EncryptionInfo(X5StreamInfo source, ASN1ObjectIdentifier digest, Password password) {
        super(source);
        this.digestId = digest;
        this.password = Objects.requireNonNull(password, "PKCS#12 password may not be null");
    }

    @Override
    public Password password() {
        return password;
    }

    @Override
    public Pkcs12EncryptionInfo withPassword(Password withPassword) {
        return new Pkcs12EncryptionInfo(getSource(), digestId, withPassword);
    }

    @Override
    public boolean isEncrypted() {
        return true;
    }

    @Override
    protected boolean isEqualTo(EncryptionInfo obj) {
        if (obj instanceof Pkcs12EncryptionInfo) {
            Pkcs12EncryptionInfo other = (Pkcs12EncryptionInfo) obj;

            if (Objects.equals(this.digestId, other.digestId) == false) {
                return false;
            }
            if (this.password == null) {
                return other.password == null;
            } else {
                return this.password.isEqualTo(other.password);
            }
        }
        return false;
    }

    @Override
    public Optional<String> getPkcs1DekAlgorithm() {
        return Optional.empty();
    }

    @Override
    public Optional<ASN1ObjectIdentifier> getPkcs8Algorithm() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + digestId + '}';
    }

    public ASN1ObjectIdentifier getDigest() {
        return digestId;
    }
}
