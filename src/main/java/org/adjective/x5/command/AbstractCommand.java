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

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

import org.adjective.x5.exception.BadArgumentException;
import org.adjective.x5.exception.FileReadException;
import org.adjective.x5.io.password.PasswordSpec;
import org.adjective.x5.types.X5File;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.Values;

public abstract class AbstractCommand implements Command {

    protected boolean isFunction() {
        return this instanceof CommandLineFunction;
    }

    protected void requireArgumentCount(int required, List<?> args) throws BadArgumentException {
        if (args.size() != required) {
            throw new BadArgumentException(
                "The '" + name() + "' " + type() + " requires exactly " + required + " argument" + (required == 1 ? "" : "s"),
                this
            );
        }
    }

    protected void requireArgumentCount(int min, int max, List<?> args) throws BadArgumentException {
        if (args.size() < min || args.size() > max) {
            throw new BadArgumentException(
                "The '" + name() + "' " + type() + " requires between " + min + " and " + max + " arguments",
                this
            );
        }
    }

    protected void requireMinimumArgumentCount(int minRequired, List<?> args) throws BadArgumentException {
        if (args.size() < minRequired) {
            throw new BadArgumentException(
                "The '" + name() + "' " + type() + " requires at least " + minRequired + " argument" + (minRequired == 1 ? "" : "s"),
                this
            );
        }
    }

    protected X5File read(Context context, String pathName, PasswordSpec password) throws FileReadException {
        Path path = context.fileSystem().resolve(pathName);
        try {
            return context.fileSystem().read(path, password);
        } catch (FileNotFoundException e) {
            throw new FileReadException(path, e);
        }
    }

    protected X5StreamInfo getSource() {
        return Values.source("command-line::" + name() + (isFunction() ? "()" : ""));
    }

    @Override
    public String toString() {
        return type() + " '" + name() + "'";
    }
}
