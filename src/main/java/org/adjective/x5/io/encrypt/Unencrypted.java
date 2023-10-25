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

import org.adjective.x5.types.EmptyRecord;
import org.adjective.x5.types.X5Record;
import org.adjective.x5.types.value.Algorithm;
import org.adjective.x5.types.value.Password;
import org.adjective.x5.util.Values;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class Unencrypted extends AbstractEncryptionInfo implements EncryptionInfo {
    public static final Unencrypted INSTANCE = new Unencrypted();

    public Unencrypted() {
        super(Values.source("(unencrypted)"));
    }

    @Override
    public Password password() {
        return null;
    }

    @Override
    public EncryptionInfo withPassword(Password password) {
        throw new UnsupportedOperationException("Cannot apply password to unencrypted objects");
    }

    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    protected boolean isEqualTo(EncryptionInfo other) {
        return other instanceof Unencrypted;
    }

    @Override
    public Optional<String> getPkcs1DekAlgorithm() {
        return Optional.empty();
    }

    @Override
    public Optional<Algorithm> getPkcs8Algorithm() {
        return Optional.empty();
    }

    @Override
    public Optional<Algorithm> getEncryptionScheme() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "(Unencrypted)";
    }

    @Override
    public X5Record asRecord() {
        return new EmptyRecord(getSource());
    }
}
