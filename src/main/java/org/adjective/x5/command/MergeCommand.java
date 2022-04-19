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

import org.adjective.x5.exception.InvalidTargetException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.CryptoStore;
import org.adjective.x5.types.StoreEntry;
import org.adjective.x5.types.X5Object;

public class MergeCommand extends AbstractSimpleCommand {

    @Override
    public String name() {
        return "merge";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        X5Object obj = values.pop();
        if (args.size() == 1) {
            final int count = integerArgument(args, 0);
            for (int i = 0; i < count; i++) {
                obj = merge(obj, values.pop());
            }
        } else {
            while (values.hasValue()) {
                obj = merge(obj, values.pop());
            }
        }
        values.push(obj);
    }

    private X5Object merge(X5Object obj1, X5Object obj2) throws X5Exception {
        if (obj1 instanceof CryptoStore) {
            if (obj2 instanceof CryptoStore) {
                return mergeStores((CryptoStore) obj1, (CryptoStore) obj2);
            } else if (obj2 instanceof StoreEntry) {
                ((CryptoStore) obj1).addEntry((StoreEntry) obj2, Optional.empty());
                return obj1;
            } else {
                throw new InvalidTargetException(obj2, "Cannot merge " + obj2 + " into store " + obj1);
            }
        } else if (obj2 instanceof CryptoStore) {
            return merge(obj2, obj1);
        } else {
            throw new InvalidTargetException(obj2, "Cannot merge into " + obj1);
        }
    }

    private CryptoStore mergeStores(CryptoStore obj1, CryptoStore obj2) throws X5Exception {
        for (StoreEntry storeEntry : obj2.entries()) {
            obj1.addEntry(storeEntry, Optional.of(obj2.encryption()));
        }
        return obj1;
    }
}
