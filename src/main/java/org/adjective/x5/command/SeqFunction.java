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
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.ObjectSequence;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.X5Object;

public class SeqFunction extends EvaluatedFunction<Sequence> {

    @Override
    public String name() {
        return "seq";
    }

    @Override
    protected Sequence evaluateFunction(CommandRunner runner, List<String> options, List<CommandLine> argumentExpressions) throws X5Exception {
        final List<X5Object> seq = evaluateArguments(runner, argumentExpressions);
        return new ObjectSequence(seq, getSource());
    }

}
