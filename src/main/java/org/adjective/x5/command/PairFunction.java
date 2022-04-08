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
import org.adjective.x5.types.CryptoValue;
import org.adjective.x5.types.KeyPair;
import org.adjective.x5.types.PrivateCredential;
import org.adjective.x5.types.PublicCredential;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.crypto.BasicKeyPair;

public class PairFunction extends EvaluatedFunction<KeyPair> implements CommandLineFunction {

    @Override
    public String name() {
        return "pair";
    }

    @Override
    protected KeyPair evaluateFunction(CommandRunner runner, List<CommandLine> argumentExpressions) throws X5Exception {
        requireArgumentCount(2, argumentExpressions);
        final CryptoValue v1 = evaluateArgument(0, X5Type.ANY_CRYPTO, runner, argumentExpressions);
        final CryptoValue v2 = evaluateArgument(1, X5Type.ANY_CRYPTO, runner, argumentExpressions);

        if (v1 instanceof PublicCredential) {
            if (v2 instanceof PrivateCredential) {
                return new BasicKeyPair((PrivateCredential) v2, (PublicCredential) v1, getSource());
            } else if (v2 instanceof PublicCredential) {
                throw new BadArgumentException("Cannot create a key-pair from two public credentials", this);
            } else {
                throw new BadArgumentException("Unsupported cryptographic object type " + v2.getTypeName() + " for " + name(), this);
            }
        } else if (v1 instanceof PrivateCredential) {
            if (v2 instanceof PublicCredential) {
                return new BasicKeyPair((PrivateCredential) v1, (PublicCredential) v2, getSource());
            } else if (v2 instanceof PrivateCredential) {
                throw new BadArgumentException("Cannot create a key-pair from two private credentials", this);
            } else {
                throw new BadArgumentException("Unsupported cryptographic object type " + v2.getTypeName() + " for " + name(), this);
            }
        } else {
            throw new BadArgumentException("Unsupported cryptographic object type " + v1.getTypeName() + " for " + name(), this);
        }
    }

}
