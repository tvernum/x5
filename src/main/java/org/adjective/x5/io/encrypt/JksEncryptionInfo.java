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
import org.adjective.x5.types.value.Password;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class JksEncryptionInfo extends AbstractEncryptionInfo implements EncryptionInfo {
    private final Password password;

    public JksEncryptionInfo(X5StreamInfo source, Password password) {
        super(source);
        this.password = password;
    }

    @Override
    public Password password() {
        return password;
    }

    @Override
    public boolean isEncrypted() {
        return true;
    }

    @Override
    protected boolean isEqualTo(EncryptionInfo enc) throws X5Exception {
        if (enc instanceof JksEncryptionInfo) {
            JksEncryptionInfo other = (JksEncryptionInfo) enc;
            return this.password.isEqualTo(other.password);
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
    public JksEncryptionInfo withPassword(Password withPassword) {
        return new JksEncryptionInfo(getSource(), withPassword);
    }

}
