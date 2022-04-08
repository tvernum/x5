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

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.KeyPair;
import org.adjective.x5.types.PrivateCredential;
import org.adjective.x5.types.PublicCredential;
import org.adjective.x5.types.X5StreamInfo;

public class BasicKeyPair implements KeyPair {
    private final PrivateCredential privateCredential;
    private final PublicCredential publicCredential;
    private final X5StreamInfo source;

    public BasicKeyPair(PrivateCredential privateCredential, PublicCredential publicCredential, X5StreamInfo source) {
        this.privateCredential = privateCredential;
        this.publicCredential = publicCredential;
        this.source = source;
    }

    @Override
    public PrivateCredential privateCredential() {
        return privateCredential;
    }

    @Override
    public PublicCredential publicCredential() {
        return publicCredential;
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        privateCredential.writeTo(out);
        publicCredential.writeTo(out);
    }
}
