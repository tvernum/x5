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

package org.adjective.x5.types.crypto;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PublicKey;
import java.util.Map;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5PublicKey;
import org.adjective.x5.types.X5StreamInfo;

public class JavaPublicKey implements X5PublicKey {
    private final PublicKey key;
    private final X5StreamInfo source;

    public JavaPublicKey(PublicKey key, X5StreamInfo source) {
        this.key = key;
        this.source = source;
    }

    @Override
    public String getKeyType() {
        return key.getAlgorithm();
    }

    @Override
    public byte[] encodedValue() {
        return key.getEncoded();
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public Map<String, ? extends X5Object> properties() {
        // TODO
        return Map.of();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        // TODO PEM?
        out.write(key.getEncoded());
    }
}
