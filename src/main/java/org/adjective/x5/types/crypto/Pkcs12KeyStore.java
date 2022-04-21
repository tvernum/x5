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

import java.security.KeyStore;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.io.encrypt.Pkcs12EncryptionInfo;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.Lazy;
import org.adjective.x5.util.Values;
import org.bouncycastle.pkcs.PKCS12PfxPdu;

public class Pkcs12KeyStore extends JavaKeyStore {
    private final PKCS12PfxPdu pfx;
    private final Supplier<Map<String, X5Object>> properties;

    public Pkcs12KeyStore(KeyStore keyStore, PKCS12PfxPdu pfx, X5StreamInfo source, Pkcs12EncryptionInfo encryption) {
        this(keyStore, pfx, source, encryption, new HashMap<>());
    }

    private Pkcs12KeyStore(
        KeyStore keyStore,
        PKCS12PfxPdu pfx,
        X5StreamInfo source,
        Pkcs12EncryptionInfo encryption,
        Map<String, EncryptionInfo> encryptionByEntry
    ) {
        super(keyStore, source, encryption, encryptionByEntry);
        this.pfx = pfx;
        this.properties = Lazy.uncheckedLazy(() -> {
            Map<String, X5Object> map = new LinkedHashMap<>();
            map.put("encrypted", Values.bool(encryption.isEncrypted(), source));
            if (encryption.isEncrypted()) {
                map.put("encryption.digest", Values.algorithm(encryption.getDigest(), source));
            }
            map.putAll(super.properties());
            return map;
        }).unchecked();
    }

    @Override
    protected Pkcs12KeyStore newStore(KeyStore ks, EncryptionInfo encryption, Map<String, EncryptionInfo> encryptionByEntry) {
        if (encryption instanceof Pkcs12EncryptionInfo) {
            return new Pkcs12KeyStore(ks, pfx, this.getSource(), (Pkcs12EncryptionInfo) encryption, encryptionByEntry);
        } else {
            throw new IllegalArgumentException(
                "Encryption for PKCS#12 Keystore must be "
                    + Pkcs12EncryptionInfo.class.getSimpleName()
                    + " (not "
                    + encryption.getClass().getSimpleName()
                    + ")"
            );
        }
    }

    @Override
    public Map<String, X5Object> properties() {
        return properties.get();
    }
}
