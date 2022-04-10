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

        KeyPair pair = trySimplePair(v1, v2);
        if (pair != null) {
            return pair;
        }
        pair = trySimplePair(v2, v1);
        if (pair != null) {
            return pair;
        }
        pair = tryConversionPair(v1, v2);
        if (pair != null) {
            return pair;
        }
        pair = tryConversionPair(v2, v1);
        if (pair != null) {
            return pair;
        }
        throw unsupportedCryptoObject(v1);
    }

    private KeyPair trySimplePair(CryptoValue v1, CryptoValue v2) throws X5Exception {
        if (v1 instanceof PublicCredential) {
            return new BasicKeyPair(getPrivateCredential(v2), (PublicCredential) v1, getSource());
        } else if (v1 instanceof PrivateCredential) {
            return new BasicKeyPair((PrivateCredential) v1, getPublicCredential(v2), getSource());
        }
        return null;
    }

    private KeyPair tryConversionPair(CryptoValue v1, CryptoValue v2) throws X5Exception {
        var pub1 = v1.as(X5Type.PUBLIC_CREDENTIAL);
        var priv1 = v1.as(X5Type.PRIVATE_CREDENTIAL);
        if (pub1.isPresent() && priv1.isEmpty()) {
            return new BasicKeyPair(getPrivateCredential(v2), pub1.get(), getSource());
        }
        if (priv1.isPresent() && pub1.isEmpty()) {
            return new BasicKeyPair(priv1.get(), getPublicCredential(v2), getSource());
        }
        return null;
    }

    private PrivateCredential getPrivateCredential(CryptoValue cryptoValue) throws BadArgumentException {
        return getCredential(cryptoValue, X5Type.PRIVATE_CREDENTIAL, X5Type.PUBLIC_CREDENTIAL);
    }

    private PublicCredential getPublicCredential(CryptoValue cryptoValue) throws BadArgumentException {
        return getCredential(cryptoValue, X5Type.PUBLIC_CREDENTIAL, X5Type.PRIVATE_CREDENTIAL);
    }

    private <C extends CryptoValue> C getCredential(
        CryptoValue cryptoValue,
        X5Type<C> requiredType,
        X5Type<? extends CryptoValue> invalidType
    ) throws BadArgumentException {
        if (requiredType.isValue(cryptoValue)) {
            return requiredType.cast(cryptoValue);
        } else if (invalidType.isValue(cryptoValue)) {
            throw twoIdenticalTypes(X5Type.PUBLIC_CREDENTIAL);
        } else {
            return cryptoValue.as(requiredType).orElseThrow(() -> unsupportedCryptoObject(cryptoValue));
        }
    }

    private BadArgumentException twoIdenticalTypes(X5Type type) {
        return new BadArgumentException("Cannot create a key-pair from two " + type.name() + " values", this);
    }

    private BadArgumentException unsupportedCryptoObject(CryptoValue cryptoValue) {
        return new BadArgumentException("Unsupported cryptographic object type " + cryptoValue.getTypeName() + " for " + name(), this);
    }

}
