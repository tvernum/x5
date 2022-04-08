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
import java.nio.file.Path;
import java.security.Provider;
import java.util.Optional;

import org.adjective.x5.exception.BadFileContentException;
import org.adjective.x5.exception.BadPasswordException;
import org.adjective.x5.exception.ExceptionInfo;
import org.adjective.x5.exception.FileReadException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.io.encrypt.Pkcs1EncryptionInfo;
import org.adjective.x5.io.encrypt.Pkcs8EncryptionInfo;
import org.adjective.x5.io.encrypt.Unencrypted;
import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.types.CryptoValue;
import org.adjective.x5.types.EncodingSyntax;
import org.adjective.x5.types.FileType;
import org.adjective.x5.types.PathInfo;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.crypto.PemCertificate;
import org.adjective.x5.types.crypto.PemKeyPair;
import org.adjective.x5.types.crypto.PemPrivateKey;
import org.adjective.x5.types.crypto.PemTrustedCertificate;
import org.adjective.x5.types.value.Password;
import org.adjective.x5.util.Values;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.X509TrustedCertificateBlock;
import org.bouncycastle.openssl.bc.BcPEMDecryptorProvider;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;

class PemTranslator {

    private static final Unencrypted UNENCRYPTED = new Unencrypted();
    private final Provider securityProvider;

    public PemTranslator() {
        securityProvider = new BouncyCastleProvider();
    }

    X5Object translatePem(Object pemObj, Path path, int index, PasswordSupplier passwordSupplier) throws X5Exception {
        if (pemObj instanceof X509CertificateHolder) {
            final PathInfo source = new PathInfo(path, index, FileType.PEM);
            return new PemCertificate((X509CertificateHolder) pemObj, source);
        }
        if (pemObj instanceof X509TrustedCertificateBlock) {
            final PathInfo source = new PathInfo(path, index, FileType.PEM);
            return new PemTrustedCertificate((X509TrustedCertificateBlock) pemObj, source);
        }
        if (pemObj instanceof PEMEncryptedKeyPair) {
            PEMEncryptedKeyPair encrypted = (PEMEncryptedKeyPair) pemObj;
            final Password password = passwordSupplier.get(path);
            try {
                final PEMKeyPair pair = encrypted.decryptKeyPair(new BcPEMDecryptorProvider(password.chars()));
                final Optional<EncodingSyntax> syntax = Optional.ofNullable(traditionalOpensslSyntax(pair.getPrivateKeyInfo()));
                final PathInfo source = new PathInfo(path, index, FileType.PEM, syntax);
                final EncryptionInfo encryption = new Pkcs1EncryptionInfo(
                    encryptionSource(source, EncodingSyntax.PKCS1),
                    encrypted,
                    password
                );
                if (pair.getPublicKeyInfo() == null) {
                    return new PemPrivateKey(pair.getPrivateKeyInfo(), source, encryption);
                }
                return new PemKeyPair(pair, source, encryption);
            } catch (IOException e) {
                throw new FileReadException(path, e);
            }
        }
        if (pemObj instanceof PEMKeyPair) {
            PEMKeyPair pair = (PEMKeyPair) pemObj;
            final Optional<EncodingSyntax> syntax = Optional.ofNullable(traditionalOpensslSyntax(pair.getPrivateKeyInfo()));
            return new PemKeyPair(pair, new PathInfo(path, index, FileType.PEM, syntax), UNENCRYPTED);
        }
        if (pemObj instanceof PKCS8EncryptedPrivateKeyInfo) {
            final PKCS8EncryptedPrivateKeyInfo encrypted = (PKCS8EncryptedPrivateKeyInfo) pemObj;
            final Password password = passwordSupplier.get(path);
            final PathInfo source = getSource(path, index);
            final EncryptionInfo encryption = new Pkcs8EncryptionInfo(encryptionSource(source, EncodingSyntax.PKCS8), encrypted, password);
            Debug.printf("Read encrypted PKCS#8: %s", encryption);
            try {
                InputDecryptorProvider decryptor = new JcePKCSPBEInputDecryptorProviderBuilder().setProvider(securityProvider)
                    .build(password.chars());
                final PrivateKeyInfo keyInfo = encrypted.decryptPrivateKeyInfo(decryptor);
                return translatePrivateKey(source, keyInfo, encryption);
            } catch (PKCSException e) {
                if (new ExceptionInfo(e).hasCauseOrSuppressed(javax.crypto.BadPaddingException.class)) {
                    throw new BadPasswordException(path, e);
                } else {
                    throw new FileReadException(path, e);
                }
            }
        }
        if (pemObj instanceof PrivateKeyInfo) {
            final PathInfo source = getSource(path, index);
            return translatePrivateKey(source, (PrivateKeyInfo) pemObj, UNENCRYPTED);
        }
        if (pemObj instanceof ASN1ObjectIdentifier) {
            final PathInfo source = getSource(path, index);
            return Values.algorithm((ASN1ObjectIdentifier) pemObj, source);
        }
        throw new BadFileContentException("Unsupported PEM object " + pemObj.getClass() + " in " + path, path);
    }

    private X5StreamInfo encryptionSource(PathInfo source, EncodingSyntax syntax) {
        return source.withDescriptionPrefix("Encryption of").withSyntax(syntax);
    }

    private EncodingSyntax traditionalOpensslSyntax(PrivateKeyInfo privateKeyInfo) {
        ASN1ObjectIdentifier algorithm = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (algorithm.equals(PKCSObjectIdentifiers.rsaEncryption)) {
            return EncodingSyntax.PKCS1;
        } else if (algorithm.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
            return EncodingSyntax.SEC1;
        } else if (algorithm.equals(X9ObjectIdentifiers.id_dsa)) {
            return EncodingSyntax.OPENSSL;
        } else if (algorithm.equals(OIWObjectIdentifiers.dsaWithSHA1)) {
            return EncodingSyntax.OPENSSL;
        }
        return null;
    }

    private PathInfo getSource(Path path, int index) {
        return new PathInfo(path, index, FileType.PEM, EncodingSyntax.PKCS8);
    }

    private CryptoValue translatePrivateKey(PathInfo source, PrivateKeyInfo keyInfo, EncryptionInfo encryption) {
        final SubjectPublicKeyInfo publicKey = getPublicKey(keyInfo);
        if (publicKey != null) {
            return new PemKeyPair(keyInfo, publicKey, source, encryption);
        }
        Debug.printf("No public key available in %s", source.getPath());
        return new PemPrivateKey(keyInfo, source, encryption);
    }

    private SubjectPublicKeyInfo getPublicKey(PrivateKeyInfo privateKey) {
        if (privateKey.hasPublicKey()) {
            try {
                return SubjectPublicKeyInfo.getInstance(privateKey.parsePublicKey());
            } catch (IOException e) {
                Debug.error(e, "Failed to parse public key");
            }
        } else {}
        return null;
    }
}
