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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.adjective.x5.exception.DnParseException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.IO;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.util.DnStringParser;
import org.adjective.x5.util.Equals;

public class RDN extends AbstractValueType<String> {

    private final AVA[] attributes;

    private RDN(String value, AVA[] attributes, X5StreamInfo source) {
        super(value, source);
        this.attributes = attributes;
    }

    public static RDN parse(DnStringParser parser, X5StreamInfo source) throws DnParseException {
        final List<AVA> list = new ArrayList<>(3);
        parser.skipWhitespace();
        int end = -1;
        final int start = parser.pos();
        loop: while (parser.hasNext()) {
            list.add(AVA.parse(parser, source.withDescriptionPrefix("ava of ")));
            switch (parser.current().type) {
                case RDN_PLUS:
                    continue;
                case END_STR:
                    end = parser.pos();
                    break loop;
                default:
                    end = parser.pos() - 1;
                    break loop;
            }
        }
        return new RDN(parser.text(start, end), list.toArray(AVA[]::new), source);
    }

    @Override
    public boolean isEqualTo(String val) {
        if (value.equals(val)) {
            return true;
        }
        try {
            final RDN other = RDN.parse(new DnStringParser(val), this.source);
            return this.isEqualTo(other);
        } catch (X5Exception e) {
            return false;
        }
    }

    @Override
    public boolean isEqualTo(X5Object other) throws X5Exception {
        if (super.isEqualTo(other)) {
            return true;
        }
        if (other instanceof RDN) {
            final RDN otherRdn = (RDN) other;
            return Equals.equals(this.attributes, otherRdn.attributes);
        }
        return false;
    }

    @Override
    public X5Type getType() {
        return X5Type.RELATIVE_DISTINGUISHED_NAME;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        IO.writeUtf8(value, out);
    }

    public Collection<AVA> getAttributes() {
        return Collections.unmodifiableCollection(Arrays.asList(attributes));
    }
}
