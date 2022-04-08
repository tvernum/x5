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
import java.util.LinkedHashMap;
import java.util.Map;

import org.adjective.x5.io.Debug;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.Values;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

class RSAKeyInfo {
    public static Map<String, X5Object> getAttributes(SubjectPublicKeyInfo key, X5StreamInfo source) {
        if (isRsaKey(key) == false) {
            return Map.of();
        }

        Map<String, X5Object> map = new LinkedHashMap<>();
        try {
            RSAPublicKey rsa = RSAPublicKey.getInstance(key.parsePublicKey());
            map.put("modulus", Values.number(rsa.getModulus(), 16, source));
            map.put("exponent", Values.number(rsa.getPublicExponent(), 16, source));
        } catch (IOException e) {
            Debug.error(e, "Failed to parse RSA key %s", source.getSourceDescription());
        }
        return map;
    }

    private static boolean isRsaKey(SubjectPublicKeyInfo key) {
        return PKCSObjectIdentifiers.rsaEncryption.equals(key.getAlgorithm().getAlgorithm());
    }

}
