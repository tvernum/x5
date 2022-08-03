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

package org.adjective.x5.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;

import javax.security.auth.x500.X500Principal;

import org.adjective.x5.exception.DnParseException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.EncodingSyntax;
import org.adjective.x5.types.FileType;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.ASN1Value;
import org.adjective.x5.types.value.Algorithm;
import org.adjective.x5.types.value.DN;
import org.adjective.x5.types.value.OID;
import org.adjective.x5.types.value.X5BigInt;
import org.adjective.x5.types.value.X5Boolean;
import org.adjective.x5.types.value.X5Date;
import org.adjective.x5.types.value.X5Decimal;
import org.adjective.x5.types.value.X5Null;
import org.adjective.x5.types.value.X5Number;
import org.adjective.x5.types.value.X5String;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class Values {

    private static final X5StreamInfo NO_SOURCE = source("(unknown)");

    public static X5StreamInfo source(String description) {
        return new X5StreamInfo() {
            @Override
            public String getSourceDescription() {
                return description;
            }

            @Override
            public Optional<Path> getPath() {
                return Optional.empty();
            }

            @Override
            public Optional<FileType> getFileType() {
                return Optional.empty();
            }

            @Override
            public Optional<EncodingSyntax> getSyntax() {
                return Optional.empty();
            }
        };
    }

    public static X5String string(String s) {
        return string(s, NO_SOURCE);
    }

    public static X5String string(String s, X5StreamInfo source) {
        return new X5String(s, source);
    }

    public static X5String hexString(byte[] bytes, X5StreamInfo source, boolean includeSeparator) {
        return string(Functions.hex(bytes, includeSeparator ? ':' : 0), source);
    }

    public static X5String binary(byte[] bytes) {
        return string(Functions.hex(bytes, (char) 0));
    }

    public static Algorithm algorithm(AlgorithmIdentifier algorithmIdentifier, X5StreamInfo source) {
        return new Algorithm(algorithmIdentifier, source);
    }

    public static Algorithm algorithm(ASN1ObjectIdentifier id, X5StreamInfo source) {
        return new Algorithm(new OID(id, source));
    }

    public static X5Object algorithm(ASN1ObjectIdentifier id, String name, X5StreamInfo source) {
        return new Algorithm(new OID(id, source), name);
    }

    public static ASN1Value asn1(ASN1Primitive primitive, X5StreamInfo source) {
        return new ASN1Value(primitive, source);
    }

    public static ASN1Value asn1(ASN1Encodable encodable, X5StreamInfo source) {
        return asn1(encodable.toASN1Primitive(), source);
    }

    public static X5Boolean bool(boolean value, X5StreamInfo source) {
        return new X5Boolean(value, source);
    }

    public static DN dn(X500Principal principal, X5StreamInfo source) throws DnParseException {
        return dn(X500Name.getInstance(principal.getEncoded()), source);
    }

    public static DN dn(X500Name principal, X5StreamInfo source) throws DnParseException {
        return DN.parse(RFC4519Style.INSTANCE.toString(principal), source);
    }

    public static X5Date date(Date date, X5StreamInfo source) {
        return new X5Date(date, source);
    }

    public static X5Number<BigInteger> number(BigInteger bigInt, int radix, X5StreamInfo source) {
        return new X5BigInt(bigInt, radix, source);
    }

    public static X5Number<BigDecimal> number(BigDecimal bigDecimal, X5StreamInfo source) {
        return new X5Decimal(bigDecimal, source);
    }

    public static X5Object number(int i, X5StreamInfo source) {
        return new X5Number<>(Integer.valueOf(i), source);
    }

    public static X5Object number(long l, X5StreamInfo source) {
        return new X5Number<>(Long.valueOf(l), source);
    }

    public static X5Null nullValue(X5StreamInfo source) {
        return new X5Null(source);
    }

    public static X5String error(X5Exception exception) {
        final StringBuilder str = new StringBuilder();

        for (Throwable th = exception; th != null; th = th.getCause()) {
            if (str.length() != 0) {
                str.append(" ⤆ ");
            }
            str.append(th.getClass().getSimpleName()).append(": ").append(th.getMessage());
        }
        return string(str.toString());
    }
}
