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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;

public class IO {

    public static void writeUtf8(String s, OutputStream out) throws IOException {
        out.write(s.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeUtf8(char[] chars, OutputStream out) throws IOException {
        writeUtf8(new String(chars), out);
    }

    public static void writeProperties(Map<String, ? extends X5Object> properties, OutputStream out) throws IOException, X5Exception {
        for (String k : properties.keySet()) {
            writeUtf8(k, out);
            out.write('=');
            final X5Object v = properties.get(k);
            if (v != null) {
                v.writeTo(out);
            }
            out.write('\n');
        }
    }
}
