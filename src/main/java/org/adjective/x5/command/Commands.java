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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.adjective.x5.exception.BadCommandException;

public class Commands {

    private Map<String, SimpleCommand> standardCommands;
    private Map<String, CommandLineFunction> functions;

    public Commands() {
        this.standardCommands = new HashMap<>();
        this.functions = new HashMap<>();
        put(new InfoCommand());
        put(new PrintCommand());
        put(new PropertyCommand());
        put(new HexCommand());
        put(new CompareValueCommand(true));
        put(new CompareValueCommand(false));
        // put(new CompareExpressionCommand(true));
        // put(new CompareExpressionCommand(false));
        put(new ReadCommand());
        put(new WriteCommand());
        put(new SetPasswordCommand());
        put(new RemovePasswordCommand());
        put(new ImportCommand());
        put(new AsCommand());
        put(new SeqFunction());
        put(new FirstCommand());
        put(new LastCommand());
        put(new SortCommand());
        put(new EachCommand());
        put(new MergeCommand());
        put(new RecurseCommand());
        put(new FilterCommand());
        put(new KeystoreFunction());
        put(new EntryFunction());
        put(new PairFunction());
    }

    private void put(SimpleCommand command) {
        if (this.standardCommands.put(command.name(), command) != null) {
            throw new IllegalArgumentException("Duplicate command: " + command.name());
        }
    }

    private void put(CommandLineFunction command) {
        if (this.functions.put(command.name(), command) != null) {
            throw new IllegalArgumentException("Duplicate command: " + command.name());
        }
    }

    public SimpleCommand get(String command) throws BadCommandException {
        if (this.standardCommands.containsKey(command)) {
            return this.standardCommands.get(command);
        } else {
            throw new BadCommandException(command);
        }
    }

    public Optional<CommandLineFunction> getFunction(String commandName) {
        return Optional.ofNullable(functions.get(commandName));
    }

}
