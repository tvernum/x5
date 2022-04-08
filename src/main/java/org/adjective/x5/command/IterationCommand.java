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
import java.util.Optional;

import org.adjective.x5.cli.CommandLine;
import org.adjective.x5.cli.CommandLineStack;
import org.adjective.x5.cli.CommandRunner;
import org.adjective.x5.exception.CommandExecutionException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.ObjectSequence;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.X5Object;

abstract class IterationCommand implements CommandLineFunction {

    @Override
    public void apply(CommandRunner runner, List<CommandLine> args) throws X5Exception {
        final X5Object pop = runner.getValues().pop();
        if (pop instanceof Sequence) {
            Sequence sequence = (Sequence) pop;
            apply(runner, args, sequence);
        } else {
            final Optional<Sequence> as = pop.as(Sequence.class);
            if (as.isPresent()) {
                apply(runner, args, as.get());
            } else {
                throw new CommandExecutionException("Cannot iterator over non-sequence " + pop);
            }
        }
    }

    protected void apply(CommandRunner runner, List<CommandLine> args, Sequence sequence) throws X5Exception {
        final CommandLineStack body = new CommandLineStack(args);
        final List<X5Object> result = new ArrayList<>();
        for (X5Object object : sequence.items()) {
            CommandRunner childRunner = runner.duplicate();
            ValueSet childValues = childRunner.getValues();
            childValues.push(object);
            body.execute(childRunner);
            List<X5Object> eval = evaluate(object, childValues);
            result.addAll(eval);
        }
        runner.getValues().push(new ObjectSequence(result, sequence.getSource().withDescriptionPrefix(name() + " of")));
    }

    protected abstract List<X5Object> evaluate(X5Object sourceObject, ValueSet childValues) throws X5Exception;
}
