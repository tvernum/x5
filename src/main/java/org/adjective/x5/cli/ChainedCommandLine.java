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

public class ChainedCommandLine implements CommandLine {
    private final List<CommandLine> commands;

    public ChainedCommandLine(List<CommandLine> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(CommandRunner runner) throws X5Exception {
        for (Iterator<CommandLine> iterator = commands.iterator(); iterator.hasNext();) {
            CommandLine command = iterator.next();
            if (iterator.hasNext()) {
                command.execute(runner.duplicate());
            } else {
                command.execute(runner);
            }
        }
    }
}
