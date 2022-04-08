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

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;

public class X5Null extends AbstractValueType<Void> {
    public X5Null(X5StreamInfo source) {
        super(null, source);
    }

    @Override
    public X5Type getType() {
        return X5Type.NULL;
    }

    @Override
    public boolean isEqualTo(String str) {
        return "null".equalsIgnoreCase(str);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        // Nothing
    }
}
