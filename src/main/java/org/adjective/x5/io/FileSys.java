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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import org.adjective.x5.io.password.PasswordSpec;
import org.adjective.x5.types.X5File;

public interface FileSys {
    Path resolve(String path);

    X5File read(Path path, PasswordSpec password) throws FileNotFoundException;

    default X5File read(Path path) throws FileNotFoundException {
        return read(path, null);
    }

    OutputStream writeTo(Path path, boolean overwrite) throws IOException;
}
