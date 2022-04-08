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

public class MultipleCommandLine implements CommandLine {
    private final List<CommandLine> commands;

    public MultipleCommandLine(List<CommandLine> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(CommandRunner runner) throws X5Exception {
        for (var command : commands) {
            command.execute(runner.duplicate());
        }
    }
}
