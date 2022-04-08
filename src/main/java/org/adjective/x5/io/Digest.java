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

package org.adjective.x5.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.adjective.x5.exception.UncheckedException;
import org.adjective.x5.exception.UnsupportedAlgorithmException;

public class Digest {

    private static final MessageDigest SHA1;
    private static final MessageDigest SHA256;

    static {
        SHA1 = load("SHA-1");
        SHA256 = load("SHA-256");
    }

    private static MessageDigest load(String name) {
        try {
            return MessageDigest.getInstance(name);
        } catch (NoSuchAlgorithmException e) {
            throw new UncheckedException(
                "Cannot load expected message digest",
                new UnsupportedAlgorithmException("MessageDigest", name, e)
            );
        }
    }

    public static byte[] sha1(byte[] encoded) {
        return SHA1.digest(encoded);
    }

    public static byte[] sha256(byte[] encoded) {
        return SHA256.digest(encoded);
    }
}
