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

import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.util.Properties;

import org.adjective.x5.io.FileSys;
import org.adjective.x5.io.StdIO;
import org.adjective.x5.io.X5FileSystem;
import org.adjective.x5.io.password.PasswordSupplier;

public class Context {

    private final StdIO stdio;
    private final FileSys fileSystem;
    private final Environment environment;
    private final Properties properties;
    private final PasswordSupplier passwords;

    public static Context create(PasswordSupplier passwordSupplier, Environment environment, Properties properties) {
        final StdIO stdio = new StdIO(System.out, System.in);
        return new Context(stdio, new X5FileSystem(passwordSupplier, stdio), passwordSupplier, environment, properties);
    }

    public Context(
        StdIO stdio,
        FileSys fileSystem,
        PasswordSupplier passwordSupplier,
        Environment environment,
        Properties properties
    ) {
        this.stdio = stdio;
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
        return stdio.getOutput();
    }
}
