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
import java.util.function.Function;

import org.adjective.x5.io.Debug;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.Values;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.anssi.ANSSINamedCurves;
import org.bouncycastle.asn1.cryptlib.CryptlibObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962NamedCurves;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

class ECKeyInfo {

    static Map<String, ? extends X5Object> getAttributes(PrivateKeyInfo key, X5StreamInfo source) {
        if (isECKey(key) == false) {
            return Map.of();
        }

        try {
            final Map<String, X5Object> map = new LinkedHashMap<>();
            ECPrivateKey ec = ECPrivateKey.getInstance(key.parsePrivateKey());
            map.put("key", Values.number(ec.getKey(), 16, source));
            map.put("bits", Values.number(ec.getKey().bitLength(), source));
            populateCurveNames(key.getPrivateKeyAlgorithm().getParameters(), source, map);
            try {
                final ECPrivateKeyParameters param = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(key);
                final ECCurve curve = param.getParameters().getCurve();
                populateParameters(param.getParameters(), curve, source, map);
                ECPoint publicKey = param.getParameters().getG().multiply(ec.getKey());
                map.put("public.raw", Values.hexString(publicKey.getEncoded(false), source, true));
                map.put("public.x", Values.number(publicKey.getRawXCoord().toBigInteger(), 16, source));
                map.put("public.y", Values.number(publicKey.getRawYCoord().toBigInteger(), 16, source));
            } catch (IOException | ClassCastException e) {
                Debug.error(e, "Cannot read private key parameters for %s", source.getSourceDescription());
            }
            return map;
        } catch (IOException e) {
            Debug.error(e, "Cannot process EC private key %s", source.getSourceDescription());
            return Map.of();
        }
    }

    static Map<String, ? extends X5Object> getAttributes(SubjectPublicKeyInfo key, X5StreamInfo source) {
        if (isECKey(key) == false) {
            return Map.of();
        }

        AlgorithmIdentifier algorithm = key.getAlgorithm();
        ASN1ObjectIdentifier oid = algorithm.getAlgorithm();
        final Map<String, X5Object> map = new LinkedHashMap<>();
        map.put("curve.id", Values.algorithm(oid, ECUtil.getCurveName(oid), source));
        try {
            ECPublicKeyParameters param = (ECPublicKeyParameters) PublicKeyFactory.createKey(key);
            final ECCurve curve = param.getParameters().getCurve();
            getCurveNames(oid).forEach((org, name) -> map.put("curve.name." + org, Values.string(name, source)));

            populateParameters(param.getParameters(), curve, source, map);
        } catch (IOException | ClassCastException e) {
            Debug.error(e, "Cannot read private key parameters for %s", source.getSourceDescription());
        }
        return map;
    }

    private static boolean isECKey(PrivateKeyInfo key) {
        return isECAlgorithm(key.getPrivateKeyAlgorithm());
    }

    private static boolean isECKey(SubjectPublicKeyInfo key) {
        return isECAlgorithm(key.getAlgorithm());
    }

    private static boolean isECAlgorithm(AlgorithmIdentifier algId) {
        return algId.getAlgorithm().equals(X9ObjectIdentifiers.id_ecPublicKey);
    }

    private static void populateCurveNames(ASN1Encodable parameters, X5StreamInfo source, Map<String, X5Object> map) {
        X962Parameters curveParam = X962Parameters.getInstance(parameters);
        if (curveParam.isNamedCurve()) {
            ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(curveParam.getParameters());
            map.put("curve.id", Values.algorithm(oid, ECUtil.getCurveName(oid), source));
            getCurveNames(oid).forEach((org, name) -> map.put("curve.name." + org, Values.string(name, source)));
        }
    }

    private static void populateParameters(ECDomainParameters parameters, ECCurve curve, X5StreamInfo source, Map<String, X5Object> map) {
        final ECFieldElement fieldA = curve.getA();
        final ECFieldElement fieldB = curve.getB();
        map.put("param.p", Values.number(curve.getField().getCharacteristic(), 16, source));
        map.put("param.a", Values.number(fieldA.toBigInteger(), 16, source));
        map.put("param.b", Values.number(fieldB.toBigInteger(), 16, source));
        ECPoint gPoint = parameters.getG();
        map.put("param.g.raw", Values.hexString(gPoint.getEncoded(false), source, true));
        map.put("param.g.x", Values.number(gPoint.getXCoord().toBigInteger(), 16, source));
        map.put("param.g.y", Values.number(gPoint.getYCoord().toBigInteger(), 16, source));
        map.put("param.n", Values.number(parameters.getN(), 16, source));
        map.put("param.h", Values.number(parameters.getH(), 0, source));
    }

    static final Map<String, Function<ASN1ObjectIdentifier, String>> CURVE_NAMES = new LinkedHashMap<>();

    static {
        CURVE_NAMES.put("x962", X962NamedCurves::getName);
        CURVE_NAMES.put("nist", NISTNamedCurves::getName);
        CURVE_NAMES.put("anssi", ANSSINamedCurves::getName);
        CURVE_NAMES.put("gost", ECGOST3410NamedCurves::getName);
        CURVE_NAMES.put("ccstc", GMNamedCurves::getName);
        CURVE_NAMES.put("sec", SECNamedCurves::getName);
        CURVE_NAMES.put("brainpool", TeleTrusTNamedCurves::getName);
        CURVE_NAMES.put("cryptlib", oid -> oid.equals(CryptlibObjectIdentifiers.curvey25519) ? "curve25519" : null);
    }

    static Map<String, String> getCurveNames(ASN1ObjectIdentifier oid) {
        final Map<String, String> map = new LinkedHashMap<>();
        CURVE_NAMES.forEach((category, func) -> {
            String name = func.apply(oid);
            if (name != null) {
                map.put(category, name);
            }
        });
        return map;
    }

}
