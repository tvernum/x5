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

package org.adjective.x5;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.adjective.x5.command.Context;
import org.adjective.x5.command.Environment;
import org.adjective.x5.io.FileSys;
import org.adjective.x5.io.StdIO;
import org.adjective.x5.io.password.PasswordSupplier;

public class TestRunner {

    private final Main main = new Main();

    public Optional<Exception> run(
        List<String> commandLine,
        PasswordSupplier passwordSupplier,
        StdIO io,
        FileSys fileSystem,
        Environment environment
    ) {
        return run(commandLine, passwordSupplier, io, fileSystem, environment, new Properties());
    }

    private Optional<Exception> run(
        List<String> commandLine,
        PasswordSupplier passwordSupplier,
        StdIO io,
        FileSys fs,
        Environment environment,
        Properties properties
    ) {
        final Context context = new Context(io, fs, passwordSupplier, environment, properties);

        try {
            main.execute(commandLine, context);
        } catch (Exception e) {
            return Optional.of(e);
        }
        return Optional.empty();
    }
}
