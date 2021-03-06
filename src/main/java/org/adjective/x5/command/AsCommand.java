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

import org.adjective.x5.exception.BadArgumentException;
import org.adjective.x5.exception.TypeConversionException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Type;

public class AsCommand extends AbstractSimpleCommand {

    @Override
    public String name() {
        return "as";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        requireArgumentCount(1, args);
        final X5Object value = values.pop();
        final String typeName = args.get(0);
        final X5Type type = X5Type.find(typeName).orElseThrow(() -> new BadArgumentException("No such type '" + typeName + "'", this));
        final Optional<? extends X5Object> converted = value.as(type);
        values.push(converted.orElseThrow(() -> new TypeConversionException(value, type)));
    }
}
