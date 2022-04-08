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
import java.math.BigDecimal;
import java.math.BigInteger;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.Debug;
import org.adjective.x5.io.IO;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;

public class X5Number<T extends Number> extends AbstractValueType<T> {

    public X5Number(T value, X5StreamInfo source) {
        super(value, source);
    }

    public long asLong() {
        return value.longValue();
    }

    @Override
    public boolean isEqualTo(String str) {
        if (value.toString().equals(str)) {
            return true;
        }
        try {
            if (this.value instanceof BigInteger) {
                return this.value.equals(new BigInteger(str));
            }
            if (this.value instanceof BigDecimal) {
                return this.value.equals(new BigDecimal(str));
            }
            if (this.value.doubleValue() == Double.parseDouble(str)) {
                return true;
            }
            if (isIntegerType()) {
                return this.value.longValue() == Long.parseLong(str);
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            Debug.printf("Cannot parse %s as a number, so it is not equal to %s", str, this);
            return false;
        }
    }

    private boolean isIntegerType() {
        return this.value instanceof BigInteger
            || this.value instanceof Long
            || this.value instanceof Integer
            || this.value instanceof Short
            || this.value instanceof Byte;
    }

    @Override
    public X5Type getType() {
        return X5Type.NUMBER;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        IO.writeUtf8(value.toString(), out);
    }
}
