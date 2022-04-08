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

import static org.adjective.x5.util.Lazy.uncheckedLazy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.function.Supplier;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.PemOutput;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.types.KeyPair;
import org.adjective.x5.types.PathInfo;
import org.adjective.x5.types.PrivateCredential;
import org.adjective.x5.types.PublicCredential;
import org.adjective.x5.types.X5StreamInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;

public class PemKeyPair implements KeyPair {
    private final PEMKeyPair pair;
    private final PathInfo source;
    private final EncryptionInfo encryption;
    private final Supplier<? extends PrivateCredential> privateCredential;
    private final Supplier<? extends PublicCredential> publicCredential;

    public PemKeyPair(PEMKeyPair pair, PathInfo source, EncryptionInfo encryption) {
        this.pair = Objects.requireNonNull(pair, "Null key pair");
        Objects.requireNonNull(pair.getPrivateKeyInfo(), "Null private key");
        Objects.requireNonNull(pair.getPublicKeyInfo(), "Null public key");
        this.source = source;
        this.encryption = encryption;
        this.privateCredential = uncheckedLazy(() -> new PemPrivateKey(pair.getPrivateKeyInfo(), source, encryption)).unchecked();
        this.publicCredential = uncheckedLazy(() -> new PemPublicKey(pair.getPublicKeyInfo(), source)).unchecked();
    }

    public PemKeyPair(PrivateKeyInfo privateKey, SubjectPublicKeyInfo publicKey, PathInfo source, EncryptionInfo encryption) {
        this(new PEMKeyPair(publicKey, privateKey), source, encryption);
    }

    @Override
    public PrivateCredential privateCredential() {
        return privateCredential.get();
    }

    @Override
    public PublicCredential publicCredential() {
        return publicCredential.get();
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append(getClass().getSimpleName()).append("{");
        builder.append("pair=").append(pair);
        builder.append(", source=").append(source);
        builder.append(", encryption=").append(encryption);
        builder.append('}');
        return builder.toString();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        PemOutput.write(pair.getPrivateKeyInfo(), out);
    }
}
