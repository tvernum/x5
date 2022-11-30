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
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

public class ObjectIdentifiers {

    private static final Map<String, String> NAMES_BY_OID = new HashMap<>();
    private static final Map<String, String> SHORT_NAME_BY_OID = new HashMap<>();

    static {
        // TODO - Need to add more of these
        define("PKCS#1", PKCSObjectIdentifiers.pkcs_1);
        define("PKCS#3", PKCSObjectIdentifiers.pkcs_3);
        define("PKCS#5", PKCSObjectIdentifiers.pkcs_5);
        define("PKCS#7", PKCSObjectIdentifiers.pkcs_7);
        define("PKCS#9", PKCSObjectIdentifiers.pkcs_9);

        define("RSA (PKCS#1)", PKCSObjectIdentifiers.rsaEncryption, "RSA");
        define("MD2-with-RSA (PKCS#1)", PKCSObjectIdentifiers.md2WithRSAEncryption, "RSA+MD2");
        define("MD4-with-RSA (PKCS#1)", PKCSObjectIdentifiers.md4WithRSAEncryption, "RSA+MD4");
        define("MD5-with-RSA (PKCS#1)", PKCSObjectIdentifiers.md5WithRSAEncryption, "RSA+MD5");
        define("SHA1-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha1WithRSAEncryption, "RSA+SHA1");
        define("SHA256-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha256WithRSAEncryption, "RSA+SHA256");
        define("SHA384-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha384WithRSAEncryption, "RSA+SHA384");
        define("SHA512-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha512WithRSAEncryption, "RSA+SHA512");
        define("SHA512-224-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha512_224WithRSAEncryption, "RSA+SHA512-224");
        define("SHA512-256-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha512_256WithRSAEncryption, "RSA+SHA512-256");
        define("SHA224-with-RSA (PKCS#1)", PKCSObjectIdentifiers.sha224WithRSAEncryption, "RSA+SHA224");
        define("PBE-SHA1-DES-CBS (PKCS#5)", PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC, "DES+SHA1");
        define("SHA1", OIWObjectIdentifiers.idSHA1, "SHA1");
        define("Friendly Name (PKCS#9)", PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
        define("Local Key Id (PKCS#9)", PKCSObjectIdentifiers.pkcs_9_at_localKeyId);
        define("EC Public Key (ANSI X9.62)", X9ObjectIdentifiers.id_ecPublicKey, "EC");
        define("Extended Key Usage (X.509)", Extension.extendedKeyUsage);
        define("Any Key Usage (X.509)", Extension.extendedKeyUsage.branch("0"));

        defineStr("TrustStore Tag (Oracle Ext)", "TrustStore", "2.16.840.1.113894.746875.1.1");

        define("emailAddress", PKCSObjectIdentifiers.pkcs_9_at_emailAddress, "email");

        define("businessCategory", BCStyle.BUSINESS_CATEGORY);
        define("commonName", BCStyle.CN, "cn");
        define("countryName", BCStyle.C, "c");
        define("domainComponent", BCStyle.DC, "dc");
        define("organizationName", BCStyle.O, "o");
        define("organizationalUnitName", BCStyle.OU, "ou");
        define("surname", BCStyle.SURNAME, "sn");
        define("uid", BCStyle.UID);
    }

    private static void define(String name, ASN1ObjectIdentifier oid) {
        define(name, oid, null);
    }

    private static void define(String name, ASN1ObjectIdentifier oid, String shortName) {
        String strId = oid.getId();
        defineStr(name, shortName, strId);
    }

    private static void defineStr(String name, String shortName, String strId) {
        defineMap(name, strId, NAMES_BY_OID);
        if (shortName != null) {
            defineMap(shortName, strId, SHORT_NAME_BY_OID);
        }
    }

    private static void defineMap(String name, String strId, Map<String, String> map) {
        if (map.containsKey(strId)) {
            throw new IllegalArgumentException("OID " + strId + " is already defined");
        }
        map.put(strId, name);
    }

    public static Optional<String> name(OID oid) {
        return Optional.ofNullable(NAMES_BY_OID.get(oid.value()));
    }

    public static Optional<String> shortName(OID oid) {
        return Optional.ofNullable(SHORT_NAME_BY_OID.get(oid.value()));
    }

    public static Optional<String> friendlyName(OID oid) {
        return shortName(oid).or(() -> ObjectIdentifiers.name(oid));
    }
}
