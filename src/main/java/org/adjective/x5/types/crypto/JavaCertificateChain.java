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

import java.util.ArrayList;
import java.util.List;

import org.adjective.x5.types.Certificate;
import org.adjective.x5.types.X5StreamInfo;

public class JavaCertificateChain extends AbstractCertificateChain {
    private final List<Certificate> chain;

    public JavaCertificateChain(java.security.cert.Certificate[] chain, X5StreamInfo source) {
        super(source);
        this.chain = new ArrayList<>(chain.length);
        for (int i = 0; i < chain.length; i++) {
            this.chain.add(JavaCertificate.create(chain[i], source.withDescriptionPrefix("certificate#" + i + " of")));
        }
    }

    @Override
    public List<Certificate> certificates() {
        return chain;
    }

}
