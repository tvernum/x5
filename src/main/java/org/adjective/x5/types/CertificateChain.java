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

package org.adjective.x5.types;

import org.adjective.x5.util.Iterables;

import java.util.List;

public interface CertificateChain extends PublicCredential, Sequence {
    @Override
    default X5Type getType() {
        return X5Type.CERTIFICATE_CHAIN;
    }

    List<? extends Certificate> certificates();

    @Override
    default Iterable<? extends X5Object> items() {
        return certificates();
    }

    default Certificate leaf() {
        return Iterables.head(certificates());
    };
}
