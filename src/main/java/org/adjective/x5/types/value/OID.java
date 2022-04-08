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

package org.adjective.x5.types.value;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Supplier;

import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.util.Lazy;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class OID extends AbstractValueType<String> {

    private final Supplier<ASN1ObjectIdentifier> asn1;

    public OID(ASN1ObjectIdentifier oid, X5StreamInfo source) {
        this(oid.getId(), source, () -> oid);
    }

    public OID(String value, X5StreamInfo source) {
        this(value, source, Lazy.uncheckedLazy(() -> new ASN1ObjectIdentifier(value)).unchecked());
    }

    private OID(String value, X5StreamInfo source, Supplier<ASN1ObjectIdentifier> asn1) {
        super(value, source);
        this.asn1 = asn1;
    }

    @Override
    public boolean isEqualTo(String val) {
        return this.value.equals(val);
    }

    @Override
    public X5Type getType() {
        return X5Type.OID;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        asn1.get().encodeTo(out);
    }

    public byte[] bytes() throws IOException {
        return asn1.get().getEncoded();
    }
}
