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
import java.util.List;
import java.util.Optional;

import org.adjective.x5.exception.DnParseException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.IO;
import org.adjective.x5.types.ObjectSequence;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.util.DnStringParser;
import org.adjective.x5.util.Equals;
import org.adjective.x5.util.Strings;
import org.bouncycastle.asn1.x500.style.IETFUtils;

public class DN extends AbstractValueType<String> {

    private RDN[] rdn;

    public static DN parse(String value, X5StreamInfo source) throws DnParseException {
        return new DN(value, parseRDNs(value, source.withDescriptionPrefix("rdn of")), source);
    }

    private DN(String value, RDN[] rdn, X5StreamInfo source) {
        super(value, source);
        this.rdn = rdn;
    }

    private static RDN[] parseRDNs(String dn, X5StreamInfo source) throws DnParseException {
        if (dn.length() == 0) {
            return new RDN[0];
        }

        final List<RDN> rdnList = new ArrayList<>(Strings.count(',', dn) + 1);
        final DnStringParser parser = new DnStringParser(dn);
        while (parser.hasNext()) {
            RDN rdn = RDN.parse(parser, source);
            rdnList.add(rdn);
            final DnStringParser.Element current = parser.current();
            assert current.type == DnStringParser.ElementType.DN_COMMA || current.type == DnStringParser.ElementType.END_STR
                : "Unexpected parse element [" + current + "]";
        }
        return rdnList.toArray(RDN[]::new);
    }

    @Override
    public boolean isEqualTo(String val) {
        if (value.equals(val)) {
            return true;
        }
        try {
            final DN other = DN.parse(val, this.source);
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
        if (other instanceof DN) {
            final DN otherDn = (DN) other;
            return Equals.equals(this.rdn, otherDn.rdn);
        }
        return false;
    }

    @Override
    public X5Type getType() {
        return X5Type.DISTINGUISHED_NAME;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        IO.writeUtf8(value, out);
    }

    public List<RDN> rdnList() {
        return Arrays.asList(rdn);
    }

    @Override
    public <X extends X5Object> Optional<X> as(Class<X> type) {
        return super.as(type).or(() -> {
            if (type.isAssignableFrom(Sequence.class)) {
                return Optional.of(type.cast(new ObjectSequence(rdnList(), source)));
            }
            return Optional.empty();
        });
    }

    /**
     * @return The leaf (that is the most specific) RDN. This will often contain an identifying name, such as `cn`
     */
    public RDN leaf() {
        if (rdn.length == 0) {
            throw new IllegalStateException("DN is empty");
        }
        return rdn[0];
    }

    /**
     * @return The root RDN.
     */
    public RDN root() {
        if (rdn.length == 0) {
            throw new IllegalStateException("DN is empty");
        }
        return rdn[rdn.length - 1];
    }
}
