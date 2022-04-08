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

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.adjective.x5.exception.UnencodableObjectException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5PublicKey;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.X5String;
import org.adjective.x5.util.Values;

public interface JavaCertificate<CERT extends Certificate> extends org.adjective.x5.types.Certificate {

    CERT certificate();

    @Override
    default byte[] encodedValue() throws X5Exception {
        try {
            return certificate().getEncoded();
        } catch (CertificateEncodingException e) {
            throw new UnencodableObjectException("Cannot get encoded value of " + certificate(), e);
        }
    }

    @Override
    default X5PublicKey publicKey() {
        return new JavaPublicKey(certificate().getPublicKey(), getSource().withDescriptionPrefix("public key for "));
    }

    @Override
    default X5String certificateType() {
        return Values.string(certificate().getType(), getSource());
    }

    static JavaCertificate<?> create(Certificate certificate, X5StreamInfo source) {
        if (certificate instanceof X509Certificate) {
            return new JavaX509Certificate((X509Certificate) certificate, source);
        } else {
            return new MiscJavaCertificate(certificate, source);
        }
    }
}
