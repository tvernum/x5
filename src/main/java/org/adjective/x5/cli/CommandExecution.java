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

package org.adjective.x5.cli;

import java.util.List;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.SuccessResult;
import org.adjective.x5.types.X5Result;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.util.Values;

public class CommandExecution extends AbstractCommandLine {
    private final String command;
    private final List<String> args;

    public CommandExecution(String command, List<String> args) {
        this.command = command;
        this.args = List.copyOf(args);
    }

    public CommandExecution(List<String> values) {
        this.command = values.get(0);
        if (values.size() == 1) {
            this.args = List.of();
        } else {
            this.args = List.copyOf(values.subList(1, values.size()));
        }
    }

    @Override
    public X5Result execute(CommandRunner runner) throws X5Exception {
        runner.execute(command, args);
        return getResult(runner);
    }

    @Override
    public String toString() {
        return "command: " + command + ' ' + args;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getArgs() {
        return args;
    }
}
