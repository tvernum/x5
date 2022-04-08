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

package org.adjective.x5.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.Provider;

import org.adjective.x5.exception.EncryptionException;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.io.encrypt.EncryptionProvider;
import org.adjective.x5.types.EncodingSyntax;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.MiscPEMGenerator;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;

public class PemOutput {

    private static final Provider BOUNCY_CASTLE_PROVIDER = new BouncyCastleProvider();

    public static void write(Object obj, OutputStream out) throws IOException {
        write(new MiscPEMGenerator(obj), out);
    }

    public static void write(PrivateKeyInfo key, OutputStream out, EncryptionInfo encryption, EncodingSyntax syntax)
        throws EncryptionException, IOException {
        if (syntax == EncodingSyntax.PKCS8) {
            writePkcs8(key, out, encryption);
        } else {
            writePkcs1(key, out, encryption);
        }
    }

    private static void write(PemObjectGenerator generator, OutputStream out) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(out); PemWriter pem = new PemWriter(writer)) {
            pem.writeObject(generator);
        }
    }

    private static void writePkcs1(ASN1Object obj, OutputStream out, EncryptionInfo encryption) throws IOException {
        if (encryption.isEncrypted()) {
            String algorithm = encryption.getPkcs1DekAlgorithm().orElse(EncryptionProvider.DEFAULT_PKCS1_ALGORITHM);
            PEMEncryptor encryptor = new JcePEMEncryptorBuilder(algorithm).setProvider(BOUNCY_CASTLE_PROVIDER)
                .build(encryption.password().chars());
            write(new MiscPEMGenerator(obj, encryptor), out);
        } else {
            write(obj, out);
        }
    }

    private static void writePkcs8(PrivateKeyInfo key, OutputStream out, EncryptionInfo encryption) throws EncryptionException,
        IOException {
        if (encryption.isEncrypted() == false) {
            write(new PKCS8Generator(key, null), out);
        } else {
            ASN1ObjectIdentifier algorithm = encryption.getPkcs8Algorithm().orElse(EncryptionProvider.DEFAULT_PKCS8_ALGORITHM);
            try {
                OutputEncryptor encryptor = new JcePKCSPBEOutputEncryptorBuilder(algorithm).setProvider(BOUNCY_CASTLE_PROVIDER)
                    .build(encryption.password().chars());
                write(new PKCS8Generator(key, encryptor), out);
            } catch (OperatorCreationException e) {
                throw new EncryptionException("Cannot configure PKCS#8 encryption: " + algorithm, e);
            }
        }
    }

}
