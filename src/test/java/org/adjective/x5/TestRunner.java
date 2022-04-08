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
import org.adjective.x5.io.password.PasswordSupplier;

public class TestRunner {

    private final Main main = new Main();

    public Result run(List<String> commandLine, PasswordSupplier passwordSupplier, FileSys fileSystem, Environment environment) {
        return run(commandLine, passwordSupplier, new byte[0], fileSystem, environment, new Properties());
    }

    private Result run(
        List<String> commandLine,
        PasswordSupplier passwordSupplier,
        byte[] input,
        FileSys fs,
        Environment environment,
        Properties properties
    ) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final Context context = new Context(
            new PrintStream(output),
            new ByteArrayInputStream(input),
            fs,
            passwordSupplier,
            environment,
            properties
        );

        Optional<Exception> exception = Optional.empty();
        try {
            main.execute(commandLine, context);
        } catch (Exception e) {
            exception = Optional.of(e);
        }
        return new Result(output.toByteArray(), exception);
    }

    public class Result {
        private final byte[] output;
        private final Optional<Exception> exception;

        private Result(byte[] output, Optional<Exception> exception) {
            this.output = output;
            this.exception = exception;
        }

        public String getOutput() {
            return getOutput(StandardCharsets.UTF_8);
        }

        public String getOutput(Charset charset) {
            return charset.decode(ByteBuffer.wrap(output)).toString();
        }

        public byte[] output() {
            return output;
        }

        public Optional<Exception> exception() {
            return exception;
        }

        public List<String> getOutputLines() {
            return List.of(getOutput().split("\n"));
        }
    }
}
