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

import org.adjective.x5.exception.BadArgumentException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Value;

public class FilterCommand extends IterationCommand {
    @Override
    public String name() {
        return "filter";
    }

    @Override
    protected List<X5Object> evaluate(X5Object sourceObject, ValueSet childValues) throws X5Exception {
        X5Object result = childValues.pop();
        if (isTrue(result)) {
            return List.of(sourceObject);
        } else {
            return List.of();
        }
    }

    private boolean isTrue(X5Object obj) throws BadArgumentException {
        if (obj instanceof X5Value) {
            Object val = ((X5Value) obj).value();
            if (val instanceof Boolean) {
                return (((Boolean) val).booleanValue());
            }
        }
        throw new BadArgumentException(
            "Object " + obj.description() + " cannot be used in a filter because it is not a boolean value",
            this
        );
    }
}
