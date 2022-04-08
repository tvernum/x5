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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.types.crypto.JavaSecretKey;
import org.adjective.x5.types.value.ASN1Value;
import org.adjective.x5.types.value.Algorithm;
import org.adjective.x5.types.value.DN;
import org.adjective.x5.types.value.Password;
import org.adjective.x5.types.value.X5Boolean;
import org.adjective.x5.types.value.X5Date;
import org.adjective.x5.types.value.X5Null;
import org.adjective.x5.types.value.X5Number;
import org.adjective.x5.types.value.X5String;

public class X5Type<X extends X5Object> {

    private static final List<X5Type<?>> VALUES = new ArrayList<>();

    public static final X5Type<Certificate> CERTIFICATE = new X5Type<>("Certificate", Certificate.class);
    public static final X5Type<CertificateChain> CERTIFICATE_CHAIN = new X5Type<>("Certificate Chain", CertificateChain.class, "chain");
    public static final X5Type<X5PublicKey> PUBLIC_KEY = new X5Type<>("Public Key", X5PublicKey.class);
    public static final X5Type<X5PrivateKey> PRIVATE_KEY = new X5Type<>("Private Key", X5PrivateKey.class);
    public static final X5Type<KeyPair> KEY_PAIR = new X5Type<>("Key Pair", KeyPair.class, "pair");
    public static final X5Type<PrivateCredential> PRIVATE_CREDENTIAL = new X5Type<>("Private Credential", PrivateCredential.class);
    public static final X5Type<PublicCredential> PUBLIC_CREDENTIAL = new X5Type<>("Public Credential", PublicCredential.class);
    public static final X5Type<JavaSecretKey> SECRET_KEY = new X5Type<>("Secret Key", JavaSecretKey.class);
    public static final X5Type<CryptoValue> ANY_CRYPTO = new X5Type<>("Cryptographic Object", CryptoValue.class);

    public static final X5Type<CryptoStore> STORE = new X5Type<>("Store", CryptoStore.class, "keystore", "key_store");
    public static final X5Type<StoreEntry> STORE_ENTRY = new X5Type<>("Store Entry", StoreEntry.class);

    public static final X5Type<Sequence> SEQUENCE = new X5Type<>("Sequence", Sequence.class);
    public static final X5Type<X5Record> RECORD = new X5Type<>("Record", X5Record.class);
    public static final X5Type<EncryptionInfo> ENCRYPTION = new X5Type<>("Encryption", EncryptionInfo.class);

    public static final X5Type<X5Null> NULL = new X5Type<>("Null", X5Null.class);
    public static final X5Type<X5String> STRING = new X5Type<>("String", X5String.class);
    public static final X5Type<X5Number> NUMBER = new X5Type<>("Number", X5Number.class);
    public static final X5Type<X5Boolean> BOOLEAN = new X5Type<>("Boolean", X5Boolean.class);
    public static final X5Type<X5Date> DATE = new X5Type<>("Date", X5Date.class);
    public static final X5Type<Password> PASSWORD = new X5Type<>("Password", Password.class);
    public static final X5Type<Algorithm> ALGORITHM = new X5Type<>("Algorithm", Algorithm.class);
    public static final X5Type<org.adjective.x5.types.value.OID> OID = new X5Type<>("OID", org.adjective.x5.types.value.OID.class);
    public static final X5Type<ASN1Value> ASN1 = new X5Type<>("ASN.1", ASN1Value.class, "asn1");
    public static final X5Type<DN> DISTINGUISHED_NAME = new X5Type<>("Distinguished Name", DN.class);

    private final String name;
    private final Class<X> objectClass;
    private final Set<String> alternateNames;

    private X5Type(String properName, Class<X> objectClass, String... alternateNames) {
        this.objectClass = objectClass;
        this.alternateNames = new HashSet<>(Arrays.asList(alternateNames));
        if (properName.contains(" ")) {
            this.alternateNames.add(properName);
            this.alternateNames.add(properName.replace(" ", "-"));
            this.alternateNames.add(properName.replace(" ", "_"));
            this.name = properName.replace(" ", "");
        } else {
            this.name = properName;
        }
        VALUES.add(this);
    }

    public static Collection<X5Type<?>> values() {
        return Collections.unmodifiableList(VALUES);
    }

    public static Optional<X5Type<?>> find(String typeName) {
        return VALUES.stream().filter(v -> v.is(typeName)).findAny();
    }

    private boolean is(String typeName) {
        return is(typeName::equalsIgnoreCase);
    }

    private boolean is(Predicate<String> typeName) {
        return typeName.test(this.name) || this.alternateNames.stream().anyMatch(typeName);
    }

    public Class<X> objectClass() {
        return objectClass;
    }

    public String name() {
        return name;
    }

}
