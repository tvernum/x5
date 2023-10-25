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

import org.adjective.x5.util.Tuple;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class SpecialFiles {

    public static Optional<Tuple<Path, InputStream>> resolveInput(Path path, StdIO stdio) {
        if (path.toString().equals("-")) {
            return Optional.of(new Tuple<>(Path.of("/dev/stdin"), stdio.getInput()));
        }

        var name = path.toAbsolutePath().toString();
        if (name.equals("/dev/stdin")) {
            return Optional.of(new Tuple<>(path, stdio.getInput()));
        }

        return Optional.empty();
    }

}
