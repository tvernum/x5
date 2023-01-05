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

package org.adjective.x5.io.password;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.adjective.x5.command.Command;
import org.adjective.x5.command.Environment;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.FileType;
import org.adjective.x5.types.PathInfo;
import org.adjective.x5.types.value.Password;
import org.adjective.x5.util.Values;

public abstract class BasePasswordSupplier implements PasswordSupplier {

    private final Environment environment;

    public BasePasswordSupplier(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Password get(Path path) throws IOException {
        return input(path.toString());
    }

    @Override
    public Password forCommand(Command command) throws IOException {
        return input(command.toString());
    }

    protected abstract Password input(String text) throws IOException;

    @Override
    public Password forSpec(PasswordSpec spec) throws IOException {
        switch (spec.type()) {
            case ENV:
                String env = environment.get(spec.text());
                if (env == null) {
                    throw new IllegalArgumentException("Environment variable " + env + " does not exist");
                }
                return new Password(env, Values.source("environment." + spec.text()));
            case LITERAL:
                return new Password(spec.text(), Values.source("password literal"));
            case FILE: {
                final Path path = Paths.get(spec.text());
                String value = Files.readString(path);
                if (value.endsWith(System.lineSeparator())) {
                    value = value.substring(0, value.length() - System.lineSeparator().length());
                }
                return new Password(value, new PathInfo(path, 0, FileType.TEXT));
            }
            case INPUT:
                return this.input(spec.text());
            default:
                throw new IllegalArgumentException("Unsupported password spec type: " + spec.type());
        }
    }

}
