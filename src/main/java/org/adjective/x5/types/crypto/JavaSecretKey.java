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

import java.util.Map;

import javax.crypto.SecretKey;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.io.encrypt.Unencrypted;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;

public class JavaSecretKey implements JavaKey, X5SecretKey {

    private final SecretKey key;
    private final X5StreamInfo source;

    public JavaSecretKey(SecretKey key, X5StreamInfo source) {
        this.key = key;
        this.source = source;
    }

    @Override
    public SecretKey key() {
        return key;
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public X5Type getType() {
        return X5Type.SECRET_KEY;
    }

    @Override
    public EncryptionInfo encryption() {
        // TODO
        return Unencrypted.INSTANCE;
    }

    @Override
    public EncryptedObject withEncryption(EncryptionInfo encryption, boolean recurse) throws X5Exception {
        throw new UnsupportedOperationException("Cannot support encrypting secret keys at this time");
    }

    @Override
    public Map<String, X5Object> properties() {
        // TODO
        return Map.of();
    }

}
