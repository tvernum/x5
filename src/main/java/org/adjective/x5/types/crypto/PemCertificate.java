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

import org.adjective.x5.exception.DnParseException;
import org.adjective.x5.exception.UnencodableObjectException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.PemOutput;
import org.adjective.x5.types.X5PublicKey;
import org.adjective.x5.types.X5Record;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.*;
import org.adjective.x5.util.Values;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;

public class PemCertificate extends AbstractX509Certificate {
    private final X509CertificateHolder certificate;

    public PemCertificate(X509CertificateHolder certificate, X5StreamInfo source) {
        super(source);
        this.certificate = certificate;
    }

    @Override
    public DN subject() throws DnParseException {
        return Values.dn(certificate.getSubject(), source);
    }

    @Override
    public DN issuer() throws DnParseException {
        return Values.dn(certificate.getIssuer(), source);
    }

    @Override
    public X5Date notBefore() {
        return Values.date(certificate.getNotBefore(), source);
    }

    @Override
    public X5Date notAfter() {
        return Values.date(certificate.getNotAfter(), source);
    }

    @Override
    public X5Number<?> serialNumber() {
        return Values.number(certificate.getSerialNumber(), 16, source);
    }

    @Override
    public Algorithm signatureAlgorithm() {
        return new Algorithm(certificate.getSignatureAlgorithm(), source);
    }

    @Override
    public X5Record basicConstraints() {
        final BasicConstraints constraints = BasicConstraints.fromExtensions(certificate.getExtensions());
        if (constraints == null) {
            return null;
        }
        return basicConstraints(constraints.isCA(), constraints.getPathLenConstraint());
    }

    @Override
    public X5Record subjectAlternativeName() {
        final GeneralNames names = GeneralNames.fromExtensions(certificate.getExtensions(), Extension.subjectAlternativeName);
        return new GeneralNamesRecord(names, source.withDescriptionPrefix("subject-alternative-name of"));
    }

    @Override
    public byte[] encodedValue() throws X5Exception {
        try {
            return certificate.getEncoded();
        } catch (IOException e) {
            throw new UnencodableObjectException("Cannot get encoded value for " + certificate, e);
        }
    }

    @Override
    public X5PublicKey publicKey() {
        return new PemPublicKey(certificate.getSubjectPublicKeyInfo(), source.withDescriptionPrefix("public key for"));
    }

    @Override
    public X5String certificateType() {
        return Values.string("X.509", source);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        PemOutput.write(certificate, out);
    }
}
