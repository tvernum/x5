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

import org.adjective.x5.cli.CommandLine;
import org.adjective.x5.cli.CommandRunner;
import org.adjective.x5.exception.BadArgumentException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.util.Values;

public abstract class AbstractFunction implements CommandLineFunction {

    protected void requireArgumentCount(int required, List<?> args) throws BadArgumentException {
        if (args.size() != required) {
            throw new BadArgumentException(
                "The '" + name() + "' function requires exactly " + required + " argument" + (required == 1 ? "" : "s"),
                this
            );
        }
    }

    protected void requireMinimumArgumentCount(int minRequired, List<?> args) throws BadArgumentException {
        if (args.size() < minRequired) {
            throw new BadArgumentException(
                "The '" + name() + "' function requires at least " + minRequired + " argument" + (minRequired == 1 ? "" : "s"),
                this
            );
        }
    }

    protected <X extends X5Object> X evaluateArgument(int index, X5Type<X> type, CommandRunner parentRunner, List<CommandLine> arguments)
        throws X5Exception {
        if (index < 0) {
            throw new IllegalArgumentException("Cannot access argument [" + index + "]");
        }
        requireMinimumArgumentCount(index + 1, arguments);
        final X5Object result = eval(arguments.get(index), parentRunner);
        return result.as(type)
            .orElseThrow(
                () -> new BadArgumentException(
                    "Argument " + (index + 1) + " to the '" + name() + "' function must be of type " + type.name(),
                    this
                )
            );
    }

    protected X5Object eval(CommandLine arg, CommandRunner parentRunner) throws X5Exception {
        var childRunner = parentRunner.duplicate();
        arg.execute(childRunner);
        return childRunner.getValues().peek();
    }

    protected X5StreamInfo getSource() {
        return Values.source("command-line::" + name() + "()");
    }
}
