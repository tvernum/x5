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

import static org.adjective.x5.util.Values.nullValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.adjective.x5.exception.ValueSetException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;

public class PropertyCommand extends AbstractCommand {
    @Override
    public String name() {
        return "property";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        requireMinimumArgumentCount(1, args);
        X5Object object = values.pop();
        String scope = null;
        final List<String> names = args.stream().map(s -> s.split("\\.")).flatMap(Arrays::stream).collect(Collectors.toList());
        for (String propertyName : names) {
            boolean optional = false;
            if (propertyName.endsWith("?")) {
                propertyName = propertyName.substring(0, propertyName.length() - 1);
                optional = true;
            }
            if (scope != null) {
                propertyName = scope + "." + propertyName;
            }
            X5Object property = object.properties().get(propertyName);
            if (property == null && optional == false) {
                scope = propertyName;
            } else {
                scope = null;
                if (property == null) {
                    object = nullValue(object.getSource().withDescriptionPrefix("missing property '" + propertyName + "' of"));
                } else {
                    object = property;
                }
            }
        }
        if (scope != null) {
            throw new ValueSetException("Property " + scope + " does not exist on " + object.description());
        }
        values.push(object);
    }

}
