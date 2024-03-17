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

import java.util.List;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.password.PasswordSpec;
import org.adjective.x5.types.X5File;

import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class ReadCommand extends CommandWithOptions {

    private final OptionSpec<PasswordSpec> passwordOption;

    public ReadCommand() {
        passwordOption = opt.declarePasswordOption("password", "p");
    }

    @Override
    public String name() {
        return "read";
    }

    @Override
    public void execute(Context context, ValueSet values, OptionSet options, List<String> args) throws X5Exception {
        requireArgumentCount(1, args);
        final PasswordSpec password;
        if (options.has(passwordOption)) {
            password = passwordOption.value(options);
        } else {
            password = null;
        }
        X5File file = read(context, args.get(0), password);
        values.push(file.asObject());
    }

}
