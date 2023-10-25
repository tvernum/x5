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

package org.adjective.x5.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.adjective.x5.io.BaseFile;
import org.adjective.x5.io.RawFile;
import org.adjective.x5.io.StdIO;
import org.adjective.x5.io.password.PasswordSupplier;

public class ShadowedFile extends BaseFile {

    private final Path concretePath;

    public ShadowedFile(Path concretePath, Path exposedPath, PasswordSupplier passwordSupplier) {
        super(exposedPath, passwordSupplier);
        this.concretePath = concretePath;
    }

    @Override
    public long size() throws IOException {
        return Files.size(concretePath);
    }

    @Override
    protected InputStream open() throws IOException {
        return Files.newInputStream(concretePath, StandardOpenOption.READ);
    }
}
