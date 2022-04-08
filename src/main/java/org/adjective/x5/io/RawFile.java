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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.adjective.x5.exception.FileReadException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.types.X5File;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.CheckedSupplier;
import org.adjective.x5.util.Lazy;

public class RawFile implements X5File {
    private final Path path;
    private final CheckedSupplier<X5Object, X5Exception> object;

    public RawFile(Path path, PasswordSupplier passwordSupplier) {
        this.path = path;
        this.object = Lazy.lazy(() -> {
            try (InputStream in = open()) {
                return FileParser.getInstance().read(in, this, passwordSupplier);
            } catch (IOException e) {
                throw new FileReadException(path, e);
            }
        });
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public long size() throws IOException {
        return Files.size(path);
    }

    protected InputStream open() throws IOException {
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    @Override
    public X5StreamInfo info() throws X5Exception {
        return object.get().getSource();
    }

    @Override
    public X5Object asObject() throws X5Exception {
        return object.get();
    }

}
