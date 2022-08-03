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

package org.adjective.x5.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.test.util.EmptyPasswordSupplier;
import org.adjective.x5.types.X509Certificate;
import org.adjective.x5.types.X5File;
import org.adjective.x5.types.X5Object;
import org.junit.jupiter.api.Test;

class FileParserTest {

    @Test
    public void testParseBase64Certificate() throws Exception {
        var str = "MIIDmzCCAoOgAwIBAgIVAJunSvh2cGEYsJT/HCCWaFaNjcqvMA0GCSqGSIb3DQEB\n"
            + "CwUAMFsxEzARBgoJkiaJk/IsZAEZFgNuZXQxFzAVBgoJkiaJk/IsZAEZFgdleGFt\n"
            + "cGxlMREwDwYDVQQLEwhzZWN1cml0eTEYMBYGA1UEAxMPSW50ZXJtZWRpYXRlIENB\n"
            + "MB4XDTIyMDQwNjA4MjE1MVoXDTQ3MDMxNzA4MjE1MVowPzETMBEGCgmSJomT8ixk\n"
            + "ARkWA25ldDEXMBUGCgmSJomT8ixkARkWB2V4YW1wbGUxDzANBgNVBAMTBnNlcnZl\n"
            + "cjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMdbZ/nxemGq02czxaBJ\n"
            + "rOm7B4xxSTRo65GjlavCadwB1FOkpk5rOFCpGy0t1N1vFJ+hdcN29IPh2P0lYN0L\n"
            + "lv2eSPhmTjf5by3s79fH6heECyfrtQmJdwklBAl5X9Ug80oOKBD9n9exknAmRp7B\n"
            + "Alh4HzvzkyydWGEVj+aUiOHFIQfkuagdgIKSJI6PgKphBJR1jUVIiflhA/hjYT6R\n"
            + "VfTE91v5KaNIObPMGcfVjSeLL4VKCVR5QKnNQOoZazF7cZ9bATKSgXYt+hvpBHU/\n"
            + "k/C3WHW8PitBw81Y8mg4v1sTZv6WXyyjvGqubN8bgS4Xyj2Dhbgj1Ddcv5roC1/9\n"
            + "qFUCAwEAAaNyMHAwHQYDVR0OBBYEFL/9/x81ckSHGcRMxdFL9HBLu2TVMB8GA1Ud\n"
            + "IwQYMBaAFHKRJIDMkHpHd1uVGQNd6gsxUqUQMCMGA1UdEQQcMBqHBH8AAAGCEnNl\n"
            + "cnZlci5leGFtcGxlLm5ldDAJBgNVHRMEAjAAMA0GCSqGSIb3DQEBCwUAA4IBAQCs\n"
            + "CiBGAKmMDlcaW5OrN3JCReRr7PxxRTpStI14Lg/U8utG/X57ls1v67z6dHNQiE4l\n"
            + "N1t1zYcRMX9wAX4/VvpAjC9FqfVP80BCwycio56RYFlkEKW7f4RSyXlcBCJxF7Ls\n"
            + "lLgBwOn48KCmx+ArbrBktEMf+0evlX5h3RW8u0t/5tvoiQ7OcM2St7BZyBFhkt1m\n"
            + "145XxjRtaR2E+Zhp0b/sAHkuB/u2iztr3rChiQ5K7xw1xwzdDc9FOyelCeI1EDYn\n"
            + "w+1xes+ggmkMO8aavsYL7kZXmS1LYaTBh9FnFfgRTIqMHhWCM/6ahnTh8/KiyAY3\n"
            + "pvUec4cFHzMQcjGll9eX";
        final X5File file = MockFile.any();
        final PasswordSupplier password = new EmptyPasswordSupplier();
        try (var in = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8))) {
            final X5Object object = FileParser.getInstance().read(in, file, password);
            assertThat(object).isInstanceOf(X509Certificate.class);
            assertThat(((X509Certificate) object).subject().toString()).isEqualTo("cn=server,dc=example,dc=net");
        }
    }
}
