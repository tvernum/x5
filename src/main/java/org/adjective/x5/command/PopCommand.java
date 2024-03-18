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

public class PopCommand extends AbstractSimpleCommand {

    @Override
    public String name() {
        return "pop";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        requireArgumentCount(0, args);
        values.pop();
    }

}
