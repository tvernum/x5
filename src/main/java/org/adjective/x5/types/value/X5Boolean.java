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

import org.adjective.x5.io.IO;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;

public class X5Boolean extends AbstractValueType<Boolean> {

    public X5Boolean(boolean value, X5StreamInfo source) {
        super(value, source);
    }

    @Override
    public String description() {
        return String.valueOf(value());
    }

    @Override
    public X5Type getType() {
        return X5Type.BOOLEAN;
    }

    @Override
    public boolean isEqualTo(String val) {
        if (this.value == true && val.equalsIgnoreCase("true")) {
            return true;
        }
        if (this.value == false && val.equalsIgnoreCase("false")) {
            return true;
        }
        return false;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        IO.writeUtf8(String.valueOf(value), out);
    }
}
