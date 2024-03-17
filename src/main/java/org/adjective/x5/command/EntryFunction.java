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
import java.util.Optional;

import org.adjective.x5.cli.CommandLine;
import org.adjective.x5.cli.CommandRunner;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.CryptoValue;
import org.adjective.x5.types.StoreEntry;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.crypto.BasicStoreEntry;

public class EntryFunction extends EvaluatedFunction<StoreEntry> implements CommandLineFunction {

    @Override
    public String name() {
        return "entry";
    }

    @Override
    protected StoreEntry evaluateFunction(CommandRunner runner, List<String> options, List<CommandLine> argumentExpressions) throws X5Exception {
        requireArgumentCount(2, argumentExpressions);
        final String name = evaluateArgument(0, X5Type.STRING, runner, argumentExpressions).value();
        final CryptoValue value = evaluateArgument(1, X5Type.ANY_CRYPTO, runner, argumentExpressions);
        return new BasicStoreEntry(Optional.empty(), name, value);
    }
}
