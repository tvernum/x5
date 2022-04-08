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

import java.io.IOException;
import java.util.List;

import org.adjective.x5.command.Commands;
import org.adjective.x5.command.Context;
import org.adjective.x5.command.SimpleCommand;
import org.adjective.x5.command.ValueSet;
import org.adjective.x5.exception.CommandExecutionException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.Debug;

public class CommandRunner {

    private final Commands commands;
    private final Context context;
    private final ValueSet values;

    public CommandRunner(Commands commands, Context context, ValueSet values) {
        this.commands = commands;
        this.context = context;
        this.values = values;
    }

    public void execute(String commandName, List<String> args) throws X5Exception {
        try {
            SimpleCommand cmd = commands.get(commandName);
            Debug.printf("execute: %s %s", commandName, args);
            cmd.execute(context, values, args);
        } catch (IOException e) {
            throw new CommandExecutionException("Failed to execute command: " + commandName, e);
        }
    }

    public ValueSet getValues() {
        return values;
    }

    public CommandRunner duplicate() {
        return new CommandRunner(commands, context, values.duplicate());
    }
}
