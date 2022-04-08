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

import org.adjective.x5.types.value.Algorithm;
import org.adjective.x5.types.value.DN;
import org.adjective.x5.types.value.X5Date;
import org.adjective.x5.types.value.X5Number;

public interface X509Certificate extends Certificate {

    DN subject();

    DN issuer();

    X5Date notBefore();

    X5Date notAfter();

    X5Number<?> serialNumber();

    Algorithm signatureAlgorithm();

    X5Record basicConstraints();
}
