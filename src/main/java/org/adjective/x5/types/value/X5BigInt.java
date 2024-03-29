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

import java.math.BigInteger;

import org.adjective.x5.types.X5StreamInfo;

public class X5BigInt extends X5Number<BigInteger> {
    private final int radix;

    public X5BigInt(BigInteger value, int radix, X5StreamInfo source) {
        super(value, source);
        this.radix = radix;
    }

    @Override
    public String toTextValue() {
        if (radix == 0) {
            return value.toString();
        } else {
            return value.toString(radix) + " [radix:" + radix + "]";
        }
    }

}
