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

import org.adjective.x5.io.Debug;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.encoders.Hex;

public class ASN1Value extends AbstractValueType<byte[]> {

    public ASN1Value(ASN1Primitive primitive, X5StreamInfo source) {
        super(bytes(primitive), source);
    }

    private static byte[] bytes(ASN1Primitive primitive) {
        try {
            return primitive.getEncoded();
        } catch (IOException e) {
            Debug.printf("Bad encoding for ASN1Primitive: %s", e);
            return new byte[0];
        }
    }

    @Override
    public boolean isEqualTo(String val) {
        return false;
    }

    @Override
    protected String valueDescription() {
        return Hex.toHexString(value());
    }

    public ASN1Value(byte[] value, X5StreamInfo source) {
        super(value, source);
    }

    @Override
    public X5Type getType() {
        return X5Type.ASN1;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(value);
    }
}
