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

package org.adjective.x5.io.encrypt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.IO;
import org.adjective.x5.types.FixedRecord;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Record;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.util.Values;

public abstract class AbstractEncryptionInfo implements EncryptionInfo {

    private final X5StreamInfo source;

    protected AbstractEncryptionInfo(X5StreamInfo source) {
        this.source = source;
    }

    @Override
    public boolean isEncrypted() {
        return password() != null;
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public X5Type getType() {
        return X5Type.ENCRYPTION;
    }

    @Override
    public Map<String, X5Object> properties() {
        Map<String, X5Object> props = new LinkedHashMap<>();
        Optional.ofNullable(password()).ifPresent(p -> props.put("password", p));
        getPkcs1DekAlgorithm().ifPresent(a -> props.put("algorithm.dek", Values.string(a, getSource())));
        getPkcs8Algorithm().ifPresent(a -> props.put("algorithm.pkcs8", Values.algorithm(a, getSource())));
        return props;
    }

    @Override
    public boolean isEqualTo(String str) {
        return false;
    }

    @Override
    public boolean isEqualTo(X5Object other) throws X5Exception {
        if (other instanceof EncryptionInfo) {
            return isEqualTo((EncryptionInfo) other);
        }
        return false;
    }

    protected abstract boolean isEqualTo(EncryptionInfo other) throws X5Exception;

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        IO.writeProperties(properties(), out);
    }

    @Override
    public <X extends X5Object> Optional<X> as(Class<X> type) {
        if (type.isInstance(this)) {
            return Optional.of(type.cast(this));
        }
        if (type.isAssignableFrom(X5Record.class)) {
            return Optional.of(type.cast(asRecord()));
        }
        return Optional.empty();
    }

    protected X5Record asRecord() {
        return new FixedRecord(properties(), getSource());
    }

    @Override
    public String description() {
        Map<String, String> algorithms = new LinkedHashMap<>();
        getPkcs1DekAlgorithm().ifPresent(a -> algorithms.put("DEK", a));
        getPkcs8Algorithm().ifPresent(a -> algorithms.put("PKCS#8", a.getId()));
        if (algorithms.isEmpty()) {
            return "Encryption(no algorithm)";
        }
        return "Encryption{"
            + algorithms.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(", "))
            + "}";
    }

}
