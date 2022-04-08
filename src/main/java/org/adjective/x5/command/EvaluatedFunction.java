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

import java.util.ArrayList;
import java.util.List;

import org.adjective.x5.cli.CommandLine;
import org.adjective.x5.cli.CommandRunner;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;

public abstract class EvaluatedFunction<X extends X5Object> extends AbstractFunction {

    protected abstract X evaluateFunction(CommandRunner runner, List<CommandLine> argumentExpressions) throws X5Exception;

    @Override
    public void apply(CommandRunner runner, List<CommandLine> arguments) throws X5Exception {
        X result = evaluateFunction(runner, arguments);
        runner.getValues().push(result);
    }

    protected List<X5Object> evaluateArguments(CommandRunner runner, List<CommandLine> arguments) throws X5Exception {
        final List<X5Object> seq = new ArrayList<>(arguments.size());
        for (CommandLine arg : arguments) {
            X5Object val = eval(arg, runner);
            seq.add(val);
        }
        return seq;
    }
}
