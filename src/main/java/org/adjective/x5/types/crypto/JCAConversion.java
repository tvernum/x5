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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.Key;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.Optional;

import org.adjective.x5.exception.InvalidTargetException;
import org.adjective.x5.exception.LibraryException;
import org.adjective.x5.exception.UnencodableObjectException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.Certificate;
import org.adjective.x5.types.CertificateChain;
import org.adjective.x5.types.PrivateCredential;
import org.adjective.x5.types.PublicCredential;
import org.adjective.x5.types.X509Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class JCAConversion {

    static java.security.cert.Certificate certificate(Certificate x5) throws X5Exception {
        if (x5 instanceof JavaCertificate<?>) {
            return ((JavaCertificate<?>) x5).certificate();
        }
        if (x5 instanceof X509Certificate) {
            try (ByteArrayInputStream in = new ByteArrayInputStream(x5.encodedValue())) {
                return x509Factory().generateCertificate(in);
            } catch (CertificateException e) {
                throw new UnencodableObjectException("Cannot construct X509Certificate", e);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        throw new InvalidTargetException(x5, "Cannot convert " + x5 + " to JCA certificate");
    }

    public static java.security.cert.Certificate[] chain(PublicCredential x5) throws X5Exception {
        if (x5 instanceof Certificate) {
            return chain0((Certificate) x5);
        }
        if (x5 instanceof CertificateChain) {
            return chain0((CertificateChain) x5);
        }
        {
            final Optional<CertificateChain> opt = x5.as(CertificateChain.class);
            if (opt.isPresent()) {
                return chain0(opt.get());
            }
        }
        {
            final Optional<Certificate> opt = x5.as(Certificate.class);
            if (opt.isPresent()) {
                return chain0(opt.get());
            }
        }
        throw new InvalidTargetException(x5, "Cannot convert " + x5.description() + " to JCA certificate chain");
    }

    private static java.security.cert.Certificate[] chain0(Certificate x5) throws X5Exception {
        return new java.security.cert.Certificate[] { certificate(x5) };
    }

    private static java.security.cert.Certificate[] chain0(CertificateChain x5) throws X5Exception {
        final List<? extends Certificate> certificates = x5.certificates();
        java.security.cert.Certificate[] chain = new java.security.cert.Certificate[certificates.size()];
        for (int i = 0; i < chain.length; i++) {
            chain[i] = certificate(certificates.get(i));
        }
        return chain;
    }

    public static Key key(PrivateCredential x5) throws X5Exception {
        if (x5 instanceof JavaKey) {
            return ((JavaPrivateKey) x5).key();
        }
        if (x5 instanceof PemPrivateKey) {
            final PemPrivateKey pem = (PemPrivateKey) x5;
            try {
                return BouncyCastleProvider.getPrivateKey(pem.getKey());
            } catch (IOException e) {
                throw new LibraryException("Cannot convert PEM private key " + pem + " to JCA key", e);
            }
        }
        throw new InvalidTargetException(x5, "Cannot convert " + x5 + " to JCA key");
    }

    private static CertificateFactory x509Factory() throws LibraryException {
        try {
            return CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new LibraryException("Cannot construct X.509 CertificateFactory", e);
        }
    }

}
