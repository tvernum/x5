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

import java.util.Iterator;
import java.util.List;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.SuccessResult;
import org.adjective.x5.types.X5Result;
import org.adjective.x5.util.Values;

public class ChainedCommandLine implements CommandLine {
    private final List<CommandLine> commands;

    public ChainedCommandLine(List<CommandLine> commands) {
        this.commands = commands;
    }

    @Override
    public X5Result execute(CommandRunner runner) throws X5Exception {
        X5Result result = new SuccessResult(Values.source("command execution"));
        for (Iterator<CommandLine> iterator = commands.iterator(); iterator.hasNext();) {
            CommandLine command = iterator.next();
            if (iterator.hasNext()) {
                result = command.execute(runner.duplicate());
            } else {
                result = command.execute(runner);
            }
        }
        return result;
    }
}
