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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.adjective.x5.io.password.PasswordSpec;
import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.io.password.SpecifiedPassword;

public abstract class BaseFileSystem implements FileSys {
    protected final PasswordSupplier passwordSupplier;

    public BaseFileSystem(PasswordSupplier passwordSupplier) {
        this.passwordSupplier = passwordSupplier;
    }

    protected PasswordSupplier resolvePasswordSupplier(PasswordSpec password) {
        final PasswordSupplier passwords = password == null ? this.passwordSupplier : new SpecifiedPassword(password, passwordSupplier);
        return passwords;
    }

    protected void checkReadable(Path path) throws FileNotFoundException {
        if (Files.isReadable(path) == false) {
            throw new FileNotFoundException("Cannot read " + path);
        }
    }

    @Override
    public OutputStream writeTo(Path path, boolean overwrite) throws IOException {
        if (Files.exists(path) && Files.isWritable(path) == false) {
            throw new FileNotFoundException("Cannot write to " + path);
        }
        return Files.newOutputStream(path, overwrite ? StandardOpenOption.CREATE : StandardOpenOption.CREATE_NEW);
    }
}
