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
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.Certificate;
import org.adjective.x5.types.CertificateChain;
import org.adjective.x5.types.ObjectSequence;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.Lazy;

abstract class AbstractCertificateChain implements CertificateChain {
    private final X5StreamInfo source;
    private final Supplier<Map<String, ? extends Certificate>> properties;

    public AbstractCertificateChain(X5StreamInfo source) {
        this.source = source;
        this.properties = Lazy.<Map<String, ? extends Certificate>>uncheckedLazy(() -> certificatesAsMap(this.certificates())).unchecked();
    }

    @Override
    public boolean isEqualTo(X5Object other) throws X5Exception {
        if (other instanceof CertificateChain) {
            return this.certificates().equals(((CertificateChain) other).certificates());
        } else {
            return false;
        }
    }

    @Override
    public <X extends X5Object> Optional<X> as(Class<X> type) {
        if (type.isAssignableFrom(Sequence.class) == true && CertificateChain.class.isAssignableFrom(type) == false) {
            return Optional.of(type.cast(new ObjectSequence(certificates(), source)));
        }
        if (type.isInstance(this)) {
            return Optional.of(type.cast(this));
        }
        if (type.isAssignableFrom(Certificate.class)) {
            return Optional.of(type.cast(certificates().get(0)));
        }
        return CertificateChain.super.as(type);
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public Map<String, ? extends X5Object> properties() {
        return properties.get();
    }

    private static Map<String, ? extends Certificate> certificatesAsMap(List<? extends Certificate> certificates) {
        Map<String, Certificate> map = new HashMap<>();
        for (int i = 0; i < certificates.size(); i++) {
            map.put("#" + i, certificates.get(i));
        }
        return map;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        for (Certificate c : this.certificates()) {
            c.writeTo(out);
        }
    }
}
