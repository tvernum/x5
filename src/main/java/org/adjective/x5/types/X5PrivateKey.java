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

import java.util.Arrays;

import org.adjective.x5.exception.X5Exception;

public interface X5PrivateKey extends PrivateCredential, CryptoElement, BinaryEncoded {
    @Override
    default X5Type getType() {
        return X5Type.PRIVATE_KEY;
    }

    @Override
    default boolean isEqualTo(X5Object other) throws X5Exception {
        if (other instanceof X5PrivateKey) {
            return Arrays.equals(this.encodedValue(), ((X5PrivateKey) other).encodedValue());
        } else {
            return false;
        }
    }
}
