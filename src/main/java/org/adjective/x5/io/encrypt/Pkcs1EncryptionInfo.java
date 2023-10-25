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

import java.util.Optional;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.Algorithm;
import org.adjective.x5.types.value.Password;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;

public class Pkcs1EncryptionInfo extends AbstractEncryptionInfo implements EncryptionInfo {
    private final String dekAlgName;
    private final Password password;

    public Pkcs1EncryptionInfo(X5StreamInfo source, PEMEncryptedKeyPair keyPair, Password password) {
        this(source, keyPair.getDekAlgName(), password);
    }

    public Pkcs1EncryptionInfo(X5StreamInfo source, String dekAlgName, Password password) {
        super(source);
        this.dekAlgName = dekAlgName;
        this.password = password;
    }

    @Override
    public Password password() {
        return password;
    }

    @Override
    public EncryptionInfo withPassword(Password withPassword) {
        return new Pkcs1EncryptionInfo(getSource(), dekAlgName, withPassword);
    }

    @Override
    public boolean isEncrypted() {
        return true;
    }

    @Override
    protected boolean isEqualTo(EncryptionInfo enc) throws X5Exception {
        if (enc instanceof Pkcs1EncryptionInfo) {
            Pkcs1EncryptionInfo other = (Pkcs1EncryptionInfo) enc;
            return this.password.isEqualTo(other.password) && this.dekAlgName.equals(other.dekAlgName);
        }
        return false;
    }

    @Override
    public Optional<String> getPkcs1DekAlgorithm() {
        return Optional.of(dekAlgName);
    }

    @Override
    public Optional<Algorithm> getPkcs8Algorithm() {
        // TODO
        return Optional.empty();
    }

    @Override
    public Optional<Algorithm> getEncryptionScheme() {
        // TODO convert DEK to an OID
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "DekEncryptionInfo{" + dekAlgName + '}';
    }
}
