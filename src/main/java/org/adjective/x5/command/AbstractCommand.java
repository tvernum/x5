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

public abstract class AbstractCommand implements SimpleCommand {

    protected void requireArgumentCount(int required, List<String> args) throws BadArgumentException {
        if (args.size() != required) {
            throw new BadArgumentException(
                "The '" + name() + "' command requires exactly " + required + " argument" + (required == 1 ? "" : "s"),
                this
            );
        }
    }

    protected void requireArgumentCount(int min, int max, List<String> args) throws BadArgumentException {
        if (args.size() < min || args.size() > max) {
            throw new BadArgumentException("The '" + name() + "' command requires between " + min + " and " + max + " arguments", this);
        }
    }

    protected void requireMinimumArgumentCount(int minRequired, List<String> args) throws BadArgumentException {
        if (args.size() < minRequired) {
            throw new BadArgumentException(
                "The '" + name() + "' command requires at least " + minRequired + " argument" + (minRequired == 1 ? "" : "s"),
                this
            );
        }
    }

    protected int integerArgument(List<String> args, int n) throws BadArgumentException {
        requireArgumentCount(n + 1, args);
        final String val = args.get(n);
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            throw new BadArgumentException(
                "Argument " + (n + 1) + " to the '" + name() + "' command must be an integer (found '" + val + "')",
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
}
