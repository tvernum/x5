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
import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.adjective.x5.exception.DnParseException;
import org.adjective.x5.exception.UnencodableObjectException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.PemOutput;
import org.adjective.x5.types.X5Record;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.Algorithm;
import org.adjective.x5.types.value.DN;
import org.adjective.x5.types.value.OID;
import org.adjective.x5.types.value.X5Date;
import org.adjective.x5.types.value.X5Number;
import org.adjective.x5.util.Values;
import org.bouncycastle.cert.X509CertificateHolder;

public class JavaX509Certificate extends AbstractX509Certificate implements JavaCertificate<X509Certificate> {
    private final X509Certificate certificate;

    public JavaX509Certificate(X509Certificate certificate, X5StreamInfo source) {
        super(source);
        this.certificate = certificate;
    }

    @Override
    public X509Certificate certificate() {
        return certificate;
    }

    @Override
    public DN subject() throws DnParseException {
        return Values.dn(certificate.getSubjectX500Principal(), getSource());
    }

    @Override
    public DN issuer() throws DnParseException {
        return Values.dn(certificate.getIssuerX500Principal(), getSource());
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
        return new Algorithm(new OID(certificate.getSigAlgOID(), source));
    }

    public X5Record basicConstraints() {
        final int pathLenConstraint = certificate.getBasicConstraints();
        return basicConstraints(
            pathLenConstraint >= 0,
            pathLenConstraint < 0 || pathLenConstraint == Integer.MAX_VALUE ? null : BigInteger.valueOf(pathLenConstraint)
        );
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        try {
            PemOutput.write(new X509CertificateHolder(certificate.getEncoded()), out);
        } catch (CertificateEncodingException e) {
            throw new UnencodableObjectException("Cannot extract encoding from " + certificate, e);
        }
    }
}
