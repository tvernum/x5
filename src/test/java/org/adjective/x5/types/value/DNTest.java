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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.adjective.x5.exception.DnParseException;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.Values;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;

class DNTest {

    private static final X5StreamInfo SOURCE = Values.source(DNTest.class.getSimpleName());

    @Test
    public void testSimpleParsing() throws DnParseException {
        final DN dn = DN.parse("cn=person,ou=people,dc=example,dc=org", SOURCE);

        final RDN leaf = dn.leaf();
        assertThat(leaf.value()).isEqualTo("cn=person");
        assertThat(leaf.getAttributes()).singleElement().matches(a -> a.getAttributeName().equals("cn"));
        assertThat(leaf.getAttributes()).singleElement().matches(a -> a.getAttributeValue().equals("person"));

        final RDN root = dn.root();
        assertThat(root.value()).isEqualTo("dc=org");
        assertThat(root.getAttributes()).singleElement().matches(a -> a.getAttributeName().equals("dc"));
        assertThat(root.getAttributes()).singleElement().matches(a -> a.getAttributeValue().equals("org"));

        assertThat(dn.rdnList().stream().map(RDN::getAttributes).flatMap(Collection::stream).map(AVA::getAttributeValue)).containsExactly(
            "person",
            "people",
            "example",
            "org"
        );
        assertThat(dn.rdnList().stream().map(RDN::getAttributes).flatMap(Collection::stream).map(AVA::getAttributeName)).containsExactly(
            "cn",
            "ou",
            "dc",
            "dc"
        );
    }

    @Test
    public void testComplexParsing() throws DnParseException {
        final DN dn = DN.parse("cn=name+mail=name\\40example.net,ou=This\\, That and Other,ou=M\\+A,dc=example,dc=org", SOURCE);

        final RDN leaf = dn.leaf();
        assertThat(leaf.value()).isEqualTo("cn=name+mail=name\\40example.net");
        assertThat(leaf.getAttributes().stream().map(AVA::getAttributeName).collect(Collectors.toList())).containsExactly("cn", "mail");
        assertThat(leaf.getAttributes().stream().map(AVA::getAttributeValue).collect(Collectors.toList())).containsExactly(
            "name",
            "name@example.net"
        );

        final RDN root = dn.root();
        assertThat(root.value()).isEqualTo("dc=org");
        assertThat(root.getAttributes()).singleElement().matches(a -> a.getAttributeName().equals("dc"));
        assertThat(root.getAttributes()).singleElement().matches(a -> a.getAttributeValue().equals("org"));

        assertThat(dn.rdnList().stream().map(RDN::getAttributes).flatMap(Collection::stream).map(AVA::getAttributeValue)).contains(
            "name",
            "name@example.net",
            "This, That and Other",
            "M+A",
            "example",
            "org"
        );
        assertThat(dn.rdnList().stream().map(RDN::getAttributes).flatMap(Collection::stream).map(AVA::getAttributeName)).containsExactly(
            "cn",
            "mail",
            "ou",
            "ou",
            "dc",
            "dc"
        );
    }

    /**
     * @see <a href="https://docs.ldap.com/specs/rfc4514.txt">RFC 4514</a>
     */
    @Test
    public void testParsingRfcExamples() throws DnParseException {
        assertParsing("UID=jsmith,DC=example,DC=net", "uid", "jsmith", "dc", "example", "dc", "net");
        assertParsing("OU=Sales+CN=J.  Smith,DC=example,DC=net", "ou", "Sales", "cn", "J.  Smith", "dc", "example", "dc", "net");
        assertParsing(
            "CN=James \\\"Jim\\\" Smith\\, III,DC=example,DC=net",
            "cn",
            "James \"Jim\" Smith, III",
            "dc",
            "example",
            "dc",
            "net"
        );
        assertParsing("CN=Before\\0dAfter,DC=example,DC=net", "cn", "Before\rAfter", "dc", "example", "dc", "net");
    }

    private void assertParsing(String dnString, String... parts) throws DnParseException {
        final DN dn = DN.parse(dnString, SOURCE);
        assertThat(
            dn.rdnList()
                .stream()
                .map(RDN::getAttributes)
                .flatMap(Collection::stream)
                .map(ava -> List.of(ava.getAttributeName(), ava.getAttributeValue()))
                .flatMap(Collection::stream)
        ).containsExactly(parts);
    }

    @Test
    public void testIsEqualToString() throws DnParseException {
        final DN sam = DN.parse("cn=sam,ou=people,dc=example,dc=com", SOURCE);

        assertDn(sam).isEqualTo("cn=sam,ou=people,dc=example,dc=com");
        assertDn(sam).isEqualTo("cn=sam, ou=people, dc=example, dc=com");
        assertDn(sam).isEqualTo("CN=sam, OU=people, DC=example, DC=com");
    }

    private DnAssert assertDn(DN dn) {
        return new DnAssert(dn);
    }

    private static class DnAssert extends ObjectAssert<DN> {

        public DnAssert(DN dn) {
            super(dn);
        }

        public ObjectAssert<DN> isEqualTo(String other) {
            return super.matches(o -> o.isEqualTo(other), "isEqualTo(" + other + ")");
        }
    }
}
