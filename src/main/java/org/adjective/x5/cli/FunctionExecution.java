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

import org.adjective.x5.command.CommandLineFunction;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Result;

public class FunctionExecution extends AbstractCommandLine implements CommandLine {
    private final CommandLineFunction function;
    private final List<String> options;
    private final List<CommandLine> arguments;

    public FunctionExecution(CommandLineFunction function, List<String> options, List<CommandLine> arguments) {
        this.function = function;
        this.options = options;
        this.arguments = arguments;
    }

    @Override
    public X5Result execute(CommandRunner runner) throws X5Exception {
        function.apply(runner, options, arguments);
        return getResult(runner);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('{');
        sb.append("function=").append(function);
        sb.append(", options=").append(options);
        sb.append(", arguments=").append(arguments);
        sb.append('}');
        return sb.toString();
    }

    CommandLineFunction getFunction() {
        return function;
    }

    public List<String> getOptions() {
        return options;
    }

    List<CommandLine> getArguments() {
        return arguments;
    }
}
