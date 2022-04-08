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

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.Debug;

public class CommandLineStack implements CommandLine {

    private final Deque<CommandLine> commands;

    public CommandLineStack() {
        this.commands = new LinkedList<>();
    }

    public CommandLineStack(List<CommandLine> commands) {
        this();
        this.commands.addAll(commands);
    }

    @Override
    public void execute(CommandRunner runner) throws X5Exception {
        for (CommandLine c : commands) {
            c.execute(runner);
        }
    }

    void push(CommandLine element) {
        Debug.printf("Push: %s", element);
        Objects.requireNonNull(element, "Cannot push null command line element");
        commands.add(element);
    }

    List<CommandLine> getCommands() {
        return List.copyOf(commands);
    }
}
