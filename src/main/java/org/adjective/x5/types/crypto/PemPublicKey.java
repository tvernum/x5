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

import static org.adjective.x5.util.Values.algorithm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.adjective.x5.exception.UnencodableObjectException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.PemOutput;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5PublicKey;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.Lazy;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class PemPublicKey implements X5PublicKey {

    private final SubjectPublicKeyInfo key;
    private final X5StreamInfo source;
    private final Supplier<Map<String, X5Object>> properties;

    public PemPublicKey(SubjectPublicKeyInfo key, X5StreamInfo source) {
        this.key = Objects.requireNonNull(key, "Null public key info");
        this.source = source;
        this.properties = Lazy.uncheckedLazy(() -> {
            Map<String, X5Object> map = new java.util.LinkedHashMap<>();
            X5StreamInfo x = getSource().withDescriptionPrefix("algorithm for");
            AlgorithmIdentifier a = key.getAlgorithm();
            map.put("algorithm", algorithm(a, x));
            addChildInfo(map, RSAKeyInfo.getAttributes(key, getSource()), "rsa");
            addChildInfo(map, ECKeyInfo.getAttributes(key, getSource()), "ec");
            return map;
        }).unchecked();
    }

    private void addChildInfo(Map<String, X5Object> map, Map<String, ? extends X5Object> childAttributes, String prefix) {
        childAttributes.entrySet().forEach(e -> map.put(prefix + "." + e.getKey(), e.getValue()));
    }

    @Override
    public byte[] encodedValue() throws X5Exception {
        try {
            return key.getEncoded();
        } catch (IOException e) {
            throw new UnencodableObjectException("Cannot get encoded value of " + this, e);
        }
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public Map<String, ? extends X5Object> properties() {
        return properties.get();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        PemOutput.write(this.key, out);
    }
}
