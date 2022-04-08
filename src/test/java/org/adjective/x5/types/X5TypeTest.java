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

package org.adjective.x5.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class X5TypeTest {

    @Test
    public void testFindByName() {
        assertThat(X5Type.find("certificate")).hasValue(X5Type.CERTIFICATE);
        assertThat(X5Type.find("Certificate")).hasValue(X5Type.CERTIFICATE);
        assertThat(X5Type.find("CERTIFICATE")).hasValue(X5Type.CERTIFICATE);

        assertThat(X5Type.find("Certificate Chain")).hasValue(X5Type.CERTIFICATE_CHAIN);
        assertThat(X5Type.find("KeyPair")).hasValue(X5Type.KEY_PAIR);
        assertThat(X5Type.find("store-entry")).hasValue(X5Type.STORE_ENTRY);

        assertThat(X5Type.find("ASN.1")).hasValue(X5Type.ASN1);
        assertThat(X5Type.find("Asn1")).hasValue(X5Type.ASN1);

        assertThat(X5Type.find("oid")).hasValue(X5Type.OID);
        assertThat(X5Type.find("OID")).hasValue(X5Type.OID);
    }

}
