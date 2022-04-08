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
import java.util.Map;

import org.adjective.x5.exception.UnencodableObjectException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.io.encrypt.Unencrypted;
import org.adjective.x5.types.CryptoElement;
import org.adjective.x5.types.PrivateCredential;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.bouncycastle.util.Encodable;

public class GenericPrivateCredential implements PrivateCredential, CryptoElement {
    private final Object credential;
    private final X5StreamInfo source;

    public GenericPrivateCredential(Object credential, X5StreamInfo source) {
        this.credential = credential;
        this.source = source;
    }

    @Override
    public boolean isEqualTo(X5Object other) throws X5Exception {
        return other instanceof GenericPrivateCredential && this.credential.equals(((GenericPrivateCredential) other).credential);
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public X5Type getType() {
        return X5Type.PRIVATE_CREDENTIAL;
    }

    @Override
    public Map<String, ? extends X5Object> properties() {
        return Map.of();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        if (credential instanceof X5Object) {
            ((X5Object) credential).writeTo(out);
        } else if (credential instanceof Encodable) {
            out.write(((Encodable) credential).getEncoded());
        } else {
            throw new UnencodableObjectException("Cannot encode " + credential + " (" + credential.getClass() + ") to an output stream");
        }
    }

    @Override
    public EncryptionInfo encryption() {
        return Unencrypted.INSTANCE;
    }

    @Override
    public EncryptedObject withEncryption(EncryptionInfo encryption) throws X5Exception {
        return null;
    }
}
