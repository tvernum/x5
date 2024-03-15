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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.adjective.x5.cli.CommandLine;
import org.adjective.x5.cli.CommandRunner;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.FixedRecord;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Record;
import org.adjective.x5.types.X5Type;

public class PropertiesCommand extends AbstractSimpleCommand {

    @Override
    public String name() {
        return "properties";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception, IOException {
        requireArgumentCount(0, args);
        final X5Object object = values.pop();
        final X5Record props = new FixedRecord(object.properties(), object.getSource().withDescriptionPrefix("properties of"));
        values.push(props);
    }


}
