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

package org.adjective.x5.types.value;

import static org.adjective.x5.util.Values.asn1;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.util.CheckedConsumer;
import org.adjective.x5.util.Lazy;
import org.adjective.x5.util.ObjectIdentifiers;
import org.adjective.x5.util.Values;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class Algorithm extends AbstractValueType<OID> {

    private final Optional<String> name;
    private final Supplier<Map<String, X5Object>> properties;
    private final CheckedConsumer<OutputStream, IOException> writer;

    public Algorithm(AlgorithmIdentifier algorithm, X5StreamInfo source) {
        super(new OID(algorithm.getAlgorithm(), source), source);
        name = ObjectIdentifiers.name(value());
        this.properties = Lazy.uncheckedLazy(() -> {
            Map<String, X5Object> map = new LinkedHashMap<>();
            name.ifPresent(n -> map.put("name", Values.string(n, getSource())));
            map.put("parameters", asn1(algorithm.getParameters(), getSource().withDescriptionPrefix("parameters for")));
            return Map.copyOf(map);
        }).unchecked();
        this.writer = algorithm::encodeTo;
    }

    public Algorithm(OID oid) {
        this(oid, ObjectIdentifiers.name(oid));
    }

    public Algorithm(OID oid, String name) {
        this(oid, name == null ? ObjectIdentifiers.name(oid) : Optional.of(name));
    }

    public Algorithm(OID oid, Optional<String> name) {
        super(oid, oid.source);
        this.name = name;
        this.properties = Lazy.uncheckedLazy(() -> {
            Map<String, X5Object> map = new LinkedHashMap<>();
            name.ifPresent(n -> map.put("name", Values.string(n, getSource())));
            return Map.copyOf(map);
        }).unchecked();
        this.writer = oid::writeTo;
    }

    public OID oid() {
        return value();
    }

    @Override
    public boolean isEqualTo(String val) {
        return val.equals(this.value.toString());
    }

    @Override
    public boolean isEqualTo(X5Object other) {
        if (other instanceof Algorithm) {
            return this.value.equals(((Algorithm) other).value);
        }
        if (other instanceof OID) {
            return this.value.equals(other);
        }
        if (other instanceof X5String) {
            return this.value.equals(((X5String) other).value);
        }
        return false;
    }

    @Override
    public String toTextValue() {
        return oid().toTextValue() + name.map(n -> " [" + n + "]").orElse("");
    }

    @Override
    public X5Type getType() {
        return X5Type.ALGORITHM;
    }

    @Override
    public Map<String, X5Object> properties() {
        return properties.get();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        writer.accept(out);
    }
}
