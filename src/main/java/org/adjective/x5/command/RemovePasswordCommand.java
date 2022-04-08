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
import org.adjective.x5.io.encrypt.Unencrypted;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.crypto.EncryptedObject;

public class RemovePasswordCommand extends AbstractCommand {

    @Override
    public String name() {
        return "remove-password";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception, IOException {
        X5Object oldObject = values.pop();
        if (oldObject instanceof EncryptedObject) {
            EncryptedObject oldEncrypted = (EncryptedObject) oldObject;
            EncryptedObject newObject;
            if (oldEncrypted.encryption().isEncrypted()) {
                newObject = oldEncrypted.withEncryption(Unencrypted.INSTANCE);
            } else {
                newObject = oldEncrypted;
            }
            values.push(newObject);
        } else {
            throw new InvalidTargetException(oldObject, "Cannot remove password from");
        }
    }

}
