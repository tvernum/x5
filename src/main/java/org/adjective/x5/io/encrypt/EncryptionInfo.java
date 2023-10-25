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

import java.util.Optional;

import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.value.Algorithm;
import org.adjective.x5.types.value.Password;

public interface EncryptionInfo extends X5Object {
    boolean isEncrypted();

    Password password();

    EncryptionInfo withPassword(Password password);

    Optional<String> getPkcs1DekAlgorithm();

    Optional<Algorithm> getPkcs8Algorithm();

    /**
     * This will often be one of {@link #getPkcs8Algorithm()} or {@link #getPkcs1DekAlgorithm()}, but if either of those as container
     * algorithms (such as PBES2) then this will return the true underlying encryption algorithm (scheme).
     */
    Optional<Algorithm> getEncryptionScheme();
}
