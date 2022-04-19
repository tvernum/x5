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

import org.adjective.x5.exception.CommandExecutionException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.CryptoStore;
import org.adjective.x5.types.CryptoValue;
import org.adjective.x5.types.X5File;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.crypto.BasicStoreEntry;

public class EntryCommand extends AbstractSimpleCommand {

    @Override
    public String name() {
        return "entry";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        requireArgumentCount(2, args);
        importEntry(values.peek(), args.get(0), read(context, args.get(1), null));
    }

    private void importEntry(X5Object target, String alias, X5File file) throws X5Exception {
        if (target instanceof CryptoStore) {
            importEntry((CryptoStore) target, alias, file.asObject());
        } else {
            throw new CommandExecutionException("Cannot import into non-store " + target);
        }
    }

    private void importEntry(CryptoStore store, String alias, X5Object obj) throws X5Exception {
        if (obj instanceof CryptoValue) {
            store.addEntry(new BasicStoreEntry(store, alias, (CryptoValue) obj), Optional.empty());
        } else {
            throw new CommandExecutionException("Cannot import non-crypto object " + obj);
        }
    }

}
