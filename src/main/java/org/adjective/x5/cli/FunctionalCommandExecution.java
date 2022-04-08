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

import org.adjective.x5.command.FunctionalCommand;
import org.adjective.x5.exception.X5Exception;

public class FunctionalCommandExecution implements CommandLine {
    private final FunctionalCommand command;
    private final CommandLineStack body;

    public FunctionalCommandExecution(FunctionalCommand command, CommandLineStack body) {
        this.command = command;
        this.body = body;
    }

    @Override
    public void execute(CommandRunner runner) throws X5Exception {
        command.apply(runner, body);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('{');
        sb.append("command=").append(command);
        sb.append(", body=").append(body);
        sb.append('}');
        return sb.toString();
    }

    FunctionalCommand getCommand() {
        return command;
    }

    CommandLineStack getBody() {
        return body;
    }
}
