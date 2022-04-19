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

import java.util.Locale;
import java.util.Optional;

public enum FileType {
    PEM,
    PKCS12,
    JKS,
    TEXT,
    UNSPECIFIED;

    public static Optional<FileType> parse(String name) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "pem":
                return Optional.of(PEM);
            case "pkcs12":
            case "pkcs#12":
            case "pfx":
            case "p12":
                return Optional.of(PKCS12);
            case "jks":
                return Optional.of(JKS);
            case "txt":
            case "text":
                return Optional.of(TEXT);
            case "unspecified":
            case "unknown":
                return Optional.of(UNSPECIFIED);
        }
        return Optional.empty();
    }
}
