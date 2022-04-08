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

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.types.X5Object;

public interface EncryptedObject extends X5Object {
    EncryptionInfo encryption();

    EncryptedObject withEncryption(EncryptionInfo encryption) throws X5Exception;

}
