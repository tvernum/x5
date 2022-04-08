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

package org.adjective.x5.command;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.adjective.x5.io.FileSys;
import org.adjective.x5.io.X5FileSystem;
import org.adjective.x5.io.password.PasswordSupplier;

public class Context {

    private final PrintStream output;
    private final InputStream input;
    private final FileSys fileSystem;
    private final Environment environment;
    private final Properties properties;
    private final PasswordSupplier passwords;

    public Context(PasswordSupplier passwordSupplier, Environment environment, Properties properties) {
        this(System.out, System.in, new X5FileSystem(passwordSupplier), passwordSupplier, environment, properties);
    }

    public Context(
        PrintStream output,
        InputStream input,
        FileSys fileSystem,
        PasswordSupplier passwordSupplier,
        Environment environment,
        Properties properties
    ) {
        this.output = output;
        this.input = input;
        this.fileSystem = fileSystem;
        this.environment = environment;
        this.properties = properties;
        this.passwords = passwordSupplier;
    }

    public PasswordSupplier passwords() {
        return passwords;
    }

    public FileSys fileSystem() {
        return fileSystem;
    }

    public PrintStream out() {
        return output;
    }
}
