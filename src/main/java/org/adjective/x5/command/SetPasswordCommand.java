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

import java.io.IOException;
import java.util.List;

import org.adjective.x5.exception.InvalidTargetException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.encrypt.EncryptionInfo;
import org.adjective.x5.io.encrypt.EncryptionProvider;
import org.adjective.x5.io.password.PasswordSpec;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.crypto.EncryptedObject;
import org.adjective.x5.types.value.Password;

import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class SetPasswordCommand extends CommandWithOptions {

    private final OptionSpec<Void> recurseOption;

    public SetPasswordCommand() {
        recurseOption = opt.declareValuelessOption("recurse", "r");
    }

    @Override
    public String name() {
        return "set-password";
    }

    @Override
    protected void execute(Context context, ValueSet values, OptionSet options, List<String> args) throws X5Exception, IOException {
        requireArgumentCount(1, args);
        final String passwordSpec = args.get(0);
        final Password password = context.passwords().forSpec(PasswordSpec.parse(passwordSpec));
        final X5Object oldObject = values.pop();
        if (oldObject instanceof EncryptedObject) {
            final EncryptedObject oldEncrypted = (EncryptedObject) oldObject;
            final EncryptionInfo oldEncryption = oldEncrypted.encryption();
            final EncryptionInfo newEncryption;
            if (oldEncryption.isEncrypted()) {
                newEncryption = oldEncryption.withPassword(password);
            } else {
                newEncryption = EncryptionProvider.getDefaultEncryption(oldObject, password);
            }
            EncryptedObject newObject = oldEncrypted.withEncryption(newEncryption, options.has(recurseOption));
            values.push(newObject);
        } else {
            throw new InvalidTargetException(oldObject, "Cannot apply a password");
        }
    }

}
