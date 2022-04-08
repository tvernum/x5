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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.adjective.x5.exception.UncheckedException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.Digest;
import org.adjective.x5.types.Certificate;
import org.adjective.x5.types.CertificateChain;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5PublicKey;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.Values;

abstract class AbstractCertificate implements Certificate {
    protected final X5StreamInfo source;

    public AbstractCertificate(X5StreamInfo source) {
        this.source = source;
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public Map<String, ? extends X5Object> properties() {
        try {
            byte[] encoded = encodedValue();
            Map<String, X5Object> map = new LinkedHashMap<>();
            map.put("type", this.certificateType());
            map.put("key", this.publicKey());
            map.put("fingerprint.sha1", Values.hexString(Digest.sha1(encoded), source, true));
            map.put("fingerprint.sha256", Values.hexString(Digest.sha256(encoded), source, true));
            return map;
        } catch (X5Exception e) {
            throw new UncheckedException("Failed to get properties for " + description(), e);
        }
    }

    @Override
    public <X extends X5Object> Optional<X> as(Class<X> type) {
        if (type.isInstance(this)) {
            return Optional.of(type.cast(this));
        }
        if (type.isAssignableFrom(CertificateChain.class)) {
            return Optional.of(type.cast(new X5CertificateChain(List.of(this), getSource())));
        }
        if (type.isAssignableFrom(X5PublicKey.class)) {
            return Optional.of(type.cast(publicKey()));
        }
        return Optional.empty();
    }

}
