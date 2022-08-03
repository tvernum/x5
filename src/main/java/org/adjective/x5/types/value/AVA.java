
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
import java.util.Optional;

import org.adjective.x5.exception.DnParseException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.Debug;
import org.adjective.x5.io.IO;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.util.DnStringParser;
import org.bouncycastle.asn1.x500.style.IETFUtils;

public class AVA extends AbstractValueType<String> {

    private final Optional<OID> oid;
    private final String providedName;
    private final String attributeValue;

    private AVA(String value, X5StreamInfo source, OID oid, String providedName, String attributeValue) {
        super(value, source);
        this.oid = Optional.ofNullable(oid);
        this.providedName = providedName;
        this.attributeValue = attributeValue;
    }

    public static AVA parse(DnStringParser parser, X5StreamInfo source) throws DnParseException {
        final int start = parser.pos();
        final String name = IETFUtils.canonicalize(parseName(parser));
        final String value = parseValue(parser);
        // TODO : OID
        return new AVA(parser.text(start), source, null, name, value);
    }

    private static String parseName(DnStringParser parser) throws DnParseException {
        final int start = parser.pos();
        final StringBuilder str = new StringBuilder();
        while (parser.hasNext()) {
            final DnStringParser.Element element = parser.next();
            if (element.type == DnStringParser.ElementType.AVA_ASSIGN) {
                return str.toString();
            }
            if (element.type.isSpecial()) {
                Debug.printf("Illegal unescaped character [" + element.value + "] in [" + parser.text(start) + "]");
            }
            str.append(element.value);
        }
        throw new DnParseException("Unterminated AVA [" + parser.text(start) + "] expecting '='");
    }

    private static String parseValue(DnStringParser parser) throws DnParseException {
        final StringBuilder str = new StringBuilder();
        while (true) {
            final DnStringParser.Element element = parser.next();
            switch (element.type) {
                case RDN_PLUS:
                case DN_COMMA:
                case END_STR:
                    return str.toString();
                default:
                    str.append(element.value);
            }
        }
    }

    @Override
    public boolean isEqualTo(String val) {
        // TODO : Implement AVA comparison rules
        return value.equals(val);
    }

    @Override
    public boolean isEqualTo(X5Object other) throws X5Exception {
        if (super.isEqualTo(other)) {
            return true;
        }
        if (other instanceof AVA) {
            AVA that = (AVA) other;
            if (this.attributeValue.equals(that.attributeValue)) {
                if (this.providedName.equals(that.providedName)) {
                    return true;
                }
                if (this.oid.isPresent() && that.oid.isPresent()) {
                    return this.oid.get().isEqualTo(that.oid.get());
                }
            }
        }
        return false;
    }

    @Override
    public X5Type getType() {
        return X5Type.ATTRIBUTE_VALUE_ASSERTION;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        IO.writeUtf8(value, out);
    }

    public Optional<OID> getAttributeId() {
        return oid;
    }

    public String getAttributeName() {
        return providedName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

}
