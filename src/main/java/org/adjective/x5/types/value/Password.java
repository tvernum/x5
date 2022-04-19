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
import java.util.Arrays;

import org.adjective.x5.io.IO;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;

public class Password extends AbstractValueType<char[]> {

    public Password(String value, X5StreamInfo source) {
        this(value.toCharArray(), source);
    }

    public Password(char[] chars, X5StreamInfo source) {
        super(chars, source);
    }

    @Override
    public X5StreamInfo getSource() {
        return this.source;
    }

    @Override
    public X5Type getType() {
        return X5Type.PASSWORD;
    }

    public char[] chars() {
        return value();
    }

    @Override
    public boolean isEqualTo(String val) {
        return Arrays.equals(value, val.toCharArray());
    }

    @Override
    public boolean isEqualTo(X5Object other) {
        if (other instanceof X5String) {
            return isEqualTo(((X5String) other).value);
        } else if (other instanceof Password) {
            return Arrays.equals(this.value, ((Password) other).value);
        } else {
            return false;
        }
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        IO.writeUtf8(value, out);
    }

    @Override
    protected String valueDescription() {
        return "****"; // TODO should we do this?
    }
}
