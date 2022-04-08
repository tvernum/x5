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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.adjective.x5.exception.BadFileContentException;
import org.adjective.x5.exception.FileReadException;
import org.adjective.x5.exception.UnsupportedFileTypeException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.encrypt.JksEncryptionInfo;
import org.adjective.x5.io.encrypt.Pkcs12EncryptionInfo;
import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.types.EncodingSyntax;
import org.adjective.x5.types.FileType;
import org.adjective.x5.types.ObjectSequence;
import org.adjective.x5.types.PathInfo;
import org.adjective.x5.types.X509Certificate;
import org.adjective.x5.types.X5File;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.crypto.JavaKeyStore;
import org.adjective.x5.types.crypto.Pkcs12KeyStore;
import org.adjective.x5.types.crypto.X5CertificateChain;
import org.adjective.x5.types.value.DN;
import org.adjective.x5.types.value.Password;
import org.adjective.x5.util.Iterables;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.util.encoders.Hex;

public class FileParser {

    private static final byte[] PEM_MARKER1 = "-----".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] PEM_MARKER2 = "--BEGIN".getBytes(StandardCharsets.US_ASCII);

    private static final FileParser INSTANCE;

    static {
        INSTANCE = new FileParser();
    }

    public static FileParser getInstance() {
        return INSTANCE;
    }

    private PemTranslator pemTranslator;

    public FileParser() {
        pemTranslator = new PemTranslator();
    }

    public X5Object read(InputStream in, X5File file, PasswordSupplier passwordSupplier) throws IOException, X5Exception {
        try (BufferedInputStream buf = buffer(in)) {
            byte[] magic = new byte[1024];
            buf.mark(magic.length);
            buf.read(magic);
            buf.reset();
            return read(buf, magic, file, passwordSupplier);
        }
    }

    private X5Object read(BufferedInputStream in, byte[] headerBytes, X5File file, PasswordSupplier passwordSupplier) throws IOException,
        X5Exception {
        byte byte0 = headerBytes[0];
        byte byte1 = headerBytes[1];
        if (byte0 == 0x30 && (byte1 == (byte) 0x82 || byte1 == 0x56)) {
            return readPkcs12(in, file, passwordSupplier);
        }
        if (byte0 == 0xFE && byte1 == 0xED) {
            return readJks(in, file, passwordSupplier);
        }
        if (containsBytes(headerBytes, PEM_MARKER1) || containsBytes(headerBytes, PEM_MARKER2)) {
            return readPem(in, file, passwordSupplier);
        }
        Debug.printf("Cannot parse header bytes: [%s]", Hex.toHexString(headerBytes));
        // TODO DER/BER/raw-Base64
        throw new UnsupportedFileTypeException(file.path());
    }

    private X5Object readPkcs12(BufferedInputStream in, X5File file, PasswordSupplier passwordSupplier) throws FileReadException {
        try {
            in.mark((int) file.size());
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            Password password = passwordSupplier.get(file.path());
            keyStore.load(in, password.chars());

            in.reset();
            ASN1InputStream asn1 = new ASN1InputStream(in);
            PKCS12PfxPdu pfx = new PKCS12PfxPdu(Pfx.getInstance(asn1.readObject()));
            final PathInfo source = new PathInfo(file.path(), 0, FileType.PKCS12);
            Pkcs12EncryptionInfo encryption = new Pkcs12EncryptionInfo(source, pfx.getMacAlgorithmID(), password);
            return new Pkcs12KeyStore(keyStore, pfx, source, encryption);
        } catch (IOException | GeneralSecurityException e) {
            throw new FileReadException(file.path(), e);
        }
    }

    private X5Object readJks(BufferedInputStream in, X5File file, PasswordSupplier passwordSupplier) throws FileReadException {
        try {
            final PathInfo source = new PathInfo(file.path(), 0, FileType.JKS);
            final Password password = passwordSupplier.get(file.path());
            final JksEncryptionInfo encryption = new JksEncryptionInfo(source.withDescriptionPrefix("Encryption of"), password);
            final KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(in, password.chars());
            return new JavaKeyStore(keyStore, source, encryption);
        } catch (IOException | GeneralSecurityException e) {
            throw new FileReadException(file.path(), e);
        }
    }

    private X5Object readPem(InputStream in, X5File file, PasswordSupplier passwordSupplier) throws IOException, X5Exception {
        try (Reader reader = new InputStreamReader(in)) {
            final PEMParser parser = new PEMParser(reader);
            final List<X5Object> objects = new ArrayList<>();
            int index = 1;
            while (true) {
                Object pemObj = parser.readObject();
                if (pemObj == null) {
                    break;
                }
                X5Object x5Obj = pemTranslator.translatePem(pemObj, file.path(), index, passwordSupplier);
                Debug.printf("(%d) Translated PEM object %s to X5 object %s", index, pemObj, x5Obj);
                objects.add(x5Obj);
                index++;
            }
            switch (objects.size()) {
                case 0:
                    throw new BadFileContentException("PEM file " + file + " does not contain any objects", file.path());
                case 1:
                    return objects.get(0);
                default:
                    return handleObjectSequence(file, objects);
            }
        }
    }

    private X5Object handleObjectSequence(X5File file, List<X5Object> objects) throws X5Exception {
        final Set<Optional<EncodingSyntax>> encodings = objects.stream()
            .map(X5Object::getSource)
            .map(X5StreamInfo::getSyntax)
            .collect(Collectors.toSet());
        final Optional<EncodingSyntax> syntax = encodings.size() == 1 ? Iterables.head(encodings) : Optional.empty();
        final PathInfo source = new PathInfo(file.path(), 0, FileType.PEM, syntax);

        if (objects.stream().allMatch(o -> o.getType() == X5Type.CERTIFICATE)) {
            final List<X509Certificate> certificates = objects.stream()
                .map(o -> o.as(X509Certificate.class).orElse(null))
                .collect(Collectors.toList());
            if (isValidCertificateChain(certificates)) {
                return new X5CertificateChain(certificates, source);
            }
        }

        return new ObjectSequence(objects, source);
    }

    private boolean isValidCertificateChain(List<X509Certificate> certificates) throws X5Exception {
        if (certificates.stream().anyMatch(Objects::isNull)) {
            return false;
        }
        final X509Certificate leaf = certificates.get(0);
        var prevIssuer = leaf.issuer();
        for (int i = 1; i < certificates.size(); i++) {
            var cert = certificates.get(i);
            final DN subject = cert.subject();
            if (subject.isEqualTo(prevIssuer) == false) {
                return false;
            }
            prevIssuer = cert.issuer();
        }
        return true;
    }

    private static boolean containsBytes(byte[] headerBytes, byte[] marker) {
        for (int i = 0; i < headerBytes.length - marker.length; i++) {
            if (Arrays.mismatch(headerBytes, i, i + marker.length, marker, 0, marker.length) == -1) {
                return true;
            }
        }
        return false;
    }

    private static BufferedInputStream buffer(InputStream in) {
        if (in instanceof BufferedInputStream) {
            return (BufferedInputStream) in;
        } else {
            return new BufferedInputStream(in);
        }
    }
}
