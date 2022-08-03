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

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.adjective.x5.exception.DnParseException;
import org.adjective.x5.types.FixedRecord;
import org.adjective.x5.types.X509Certificate;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Record;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.DN;
import org.adjective.x5.util.Lazy;
import org.adjective.x5.util.Values;

public abstract class AbstractX509Certificate extends AbstractCertificate implements X509Certificate {
    private final Supplier<Map<String, X5Object>> properties;

    public AbstractX509Certificate(X5StreamInfo source) {
        super(source);
        this.properties = Lazy.lazy(() -> {
            Map<String, X5Object> map = new LinkedHashMap<>();
            map.putAll(super.properties());
            map.put("subject", subject());
            map.put("issuer", issuer());
            map.put("serial", serialNumber());
            map.put("validity.not_before", notBefore());
            map.put("validity.not_after", notAfter());
            map.put("signature.algorithm", signatureAlgorithm());
            map.put("basic-constraints", basicConstraints());
            return map;
        }).unchecked();
    }

    @Override
    public String description() {
        try {
            final DN sub = subject();
            return super.description() + " (" + sub + ")";
        } catch (DnParseException e) {
           return super.description();
        }
    }

    @Override
    public Map<String, ? extends X5Object> properties() {
        return properties.get();
    }

    protected X5Record basicConstraints(boolean isCA, BigInteger pathLength) {
        final X5StreamInfo source = getSource().withDescriptionPrefix("basic constraints of");
        final Map<String, X5Object> map = new LinkedHashMap<>();
        map.put("CA", Values.bool(isCA, source));
        map.put("Path Length", pathLength == null ? Values.nullValue(source) : Values.number(pathLength, 10, source));
        return new FixedRecord(map, source);
    }
}
