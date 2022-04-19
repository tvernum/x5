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

package org.adjective.x5.exception;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.bouncycastle.pkcs.PKCSException;

public class CryptoStoreException extends X5Exception {
    public CryptoStoreException(String message, GeneralSecurityException cause) {
        super(message, cause);
    }

    public CryptoStoreException(String message, IOException cause) {
        super(message, cause);
    }

    public CryptoStoreException(String message, PKCSException cause) {
        super(message, cause);
    }
}
