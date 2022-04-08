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

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.EncodingSyntax;
import org.adjective.x5.types.FileType;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.Password;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;

public class EncryptionProvider {

    public static final String DEFAULT_PKCS1_ALGORITHM = "AES-128-CBC";
    public static final ASN1ObjectIdentifier DEFAULT_PKCS8_ALGORITHM = NISTObjectIdentifiers.id_aes128_CBC;

    public static EncryptionInfo getDefaultEncryption(X5Object object, Password password) throws X5Exception {
        return getDefaultEncryption(object.getSource(), password);
    }

    private static AbstractEncryptionInfo getDefaultEncryption(X5StreamInfo source, Password password) {
        final X5StreamInfo encSource = source.withDescriptionPrefix("Encryption of");
        if (source.getFileType().filter(t -> t == FileType.PKCS12).isPresent()) {
            return new Pkcs12EncryptionInfo(encSource, OIWObjectIdentifiers.idSHA1, password);
        } else if (source.getSyntax().filter(s -> s == EncodingSyntax.PKCS1).isPresent()) {
            return new Pkcs1EncryptionInfo(encSource, DEFAULT_PKCS1_ALGORITHM, password);
        } else {
            return new Pkcs8EncryptionInfo(encSource, DEFAULT_PKCS8_ALGORITHM, null, password);
        }
    }

}
