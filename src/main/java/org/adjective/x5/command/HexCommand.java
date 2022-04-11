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
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.value.X5Number;
import org.adjective.x5.types.value.X5String;
import org.adjective.x5.util.Functions;
import org.adjective.x5.util.Values;

public class HexCommand extends AbstractCommand {

    @Override
    public String name() {
        return "hex";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        requireArgumentCount(0, 2, args);

        final String separator;
        final int numChars;
        if (args.size() >= 1) {
            separator = args.get(0);
        } else {
            separator = null;
        }
        if (args.size() >= 2) {
            numChars = Integer.parseInt(args.get(1));
        } else {
            numChars = 2;
        }

        final X5Object val = values.pop();
        final Optional<X5Number> optNum = val.as(X5Type.NUMBER);
        if (optNum.isPresent()) {
            final X5Number<?> num = optNum.get();
            String hexStr = Functions.hex(num.value());
            if (separator != null) {
                final int mod = hexStr.length() % numChars;
                if (mod > 0) {
                    hexStr = "0".repeat(numChars - mod) + hexStr;
                }
                if(separator.length() > 0) {
                    hexStr = Functions.insertSeparator(hexStr, separator, numChars).toString();
                }
            }
            final X5String str = Values.string(hexStr, num.getSource().withDescriptionPrefix("hex format"));
            values.push(str);
        } else {
            throw new InvalidTargetException(val, "Cannot call hex function on non-number");
        }
    }

}
