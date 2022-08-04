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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.IO;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;

public class RDN extends AbstractValueType<String> {

    private static final Pattern PATTERN = Pattern.compile("([^=]+)=(.*)");
    private final List<String> attributeNames;
    private final String attributeValue;

    public RDN(String value, X5StreamInfo source) {
        super(value, source);
        final Matcher matcher = PATTERN.matcher(value);
        if(matcher.matches()) {
            this.attributeNames = List.of(matcher.group(1).split("\\+"));
            this.attributeValue = matcher.group(2);
        } else {
            throw new IllegalArgumentException("Invalid RDN: " + value);
        }
    }

    @Override
    public boolean isEqualTo(String val) {
        // TODO : Implement DN comparison rules
        return value.equals(val);
    }

    @Override
    public X5Type getType() {
        return X5Type.RELATIVE_DISTINGUISHED_NAME;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        IO.writeUtf8(value, out);
    }

    public List<String> getAttributeNames() {
        return attributeNames;
    }

    public String getAttributeValue() {
        return attributeValue;
    }
}
