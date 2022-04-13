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
import java.util.Map;
import java.util.Optional;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5File;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Value;

public class InfoCommand extends AbstractCommand {

    public static final String END_LINE = "-".repeat(32) + "   " + "-".repeat(32);

    @Override
    public String name() {
        return "info";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        requireArgumentCount(0, args);
        info(context, values.peek());
    }

    private void info(Context context, X5Object obj) {
        info(context, obj.getSource(), obj);
    }

    private void info(Context context, X5StreamInfo info, X5Object obj) {
        printIf(context, "Path", info.getPath());
        print(context, "Source", info.getSourceDescription());
        printIf(context, "File Type", info.getFileType());
        printIf(context, "Syntax", info.getSyntax());
        print(context, "Object Type", obj.getTypeName());
        if (obj instanceof X5Value) {
            context.out().println();
            print(context, "Value", ((X5Value) obj).value());
        }
        Map<String, ? extends X5Object> properties = obj.properties();
        if (properties.isEmpty() == false) {
            context.out().println();
            properties.forEach((k, v) -> print(context, k, v == null ? "(null)" : v.description()));
        }
        context.out().println(END_LINE);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void printIf(Context context, String key, Optional<?> value) {
        value.ifPresent(v -> print(context, key, v));
    }

    private void print(Context context, String key, Object value) {
        context.out().printf("%32s : %s\n", key, value);
    }
}
