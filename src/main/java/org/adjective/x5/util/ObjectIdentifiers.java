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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.adjective.x5.types.value.OID;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

public class ObjectIdentifiers {

    private static final Map<String, String> NAMES_BY_OID = new HashMap<>();

    static {
        // TODO - Need to add more of these
        define("PKCS#1", PKCSObjectIdentifiers.pkcs_1);
        define("PKCS#3", PKCSObjectIdentifiers.pkcs_3);
        define("PKCS#5", PKCSObjectIdentifiers.pkcs_5);
        define("PKCS#7", PKCSObjectIdentifiers.pkcs_7);
        define("PKCS#9", PKCSObjectIdentifiers.pkcs_9);

        define("RSA (PKCS#1)", PKCSObjectIdentifiers.rsaEncryption);
        define("MD2-with-RSA (PKCS#1)", PKCSObjectIdentifiers.md2WithRSAEncryption);
        define("MD4-with-RSA (PKCS#1)", PKCSObjectIdentifiers.md4WithRSAEncryption);
        define("MD5-with-RSA (PKCS#1)", PKCSObjectIdentifiers.md5WithRSAEncryption);
        define("SHA1-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha1WithRSAEncryption);
        define("SHA256-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha256WithRSAEncryption);
        define("SHA384-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha384WithRSAEncryption);
        define("SHA512-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        define("SHA512-224-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha512_224WithRSAEncryption);
        define("SHA512-256-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha512_256WithRSAEncryption);
        define("SHA224-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha224WithRSAEncryption);
        define("PBE-SHA1-DES-CBS (PKCS#5)", PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
        define("SHA1", OIWObjectIdentifiers.idSHA1);
        define("Friendly Name (PKCS#9)", PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
        define("Local Key Id (PKCS#9)", PKCSObjectIdentifiers.pkcs_9_at_localKeyId);
        define("EC Public Key (ANSI X9.62)", X9ObjectIdentifiers.id_ecPublicKey);
        define("Extended Key Usage (X.509)", Extension.extendedKeyUsage);
        define("Any Key Usage (X.509)", Extension.extendedKeyUsage.branch("0"));
        defineStr("TrustStore Tag (Oracle Ext)", "2.16.840.1.113894.746875.1.1");
    }

    private static void define(String name, ASN1ObjectIdentifier oid) {
        String strId = oid.getId();
        defineStr(name, strId);
    }

    private static void defineStr(String name, String strId) {
        if (NAMES_BY_OID.containsKey(strId)) {
            throw new IllegalArgumentException("OID " + strId + " is already defined");
        }
        NAMES_BY_OID.put(strId, name);
    }

    public static Optional<String> name(OID oid) {
        return Optional.ofNullable(NAMES_BY_OID.get(oid.value()));
    }
}
