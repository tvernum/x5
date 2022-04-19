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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.adjective.x5.cli.SimpleConverter;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.password.PasswordSpec;

import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;

public abstract class CommandWithOptions extends AbstractSimpleCommand {

    private final OptionParser parser = new OptionParser();
    private final NonOptionArgumentSpec<String> arguments = parser.nonOptions("args");

    protected OptionSpec<PasswordSpec> declarePasswordOption(String name, String... altNames) {
        OptionSpecBuilder builder;
        if (altNames.length == 0) {
            builder = parser.accepts(name, "Password");
        } else {
            List<String> names = new ArrayList<>(altNames.length + 1);
            names.add(name);
            names.addAll(Arrays.asList(altNames));
            builder = parser.acceptsAll(names, "Password");
        }
        return builder.withRequiredArg()
            .describedAs("password specification")
            .withValuesConvertedBy(new SimpleConverter<>(PasswordSpec.class, PasswordSpec::parse));
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception, IOException {
        final OptionSet options = parser.parse(args.toArray(new String[0]));
        this.execute(context, values, options, arguments.values(options));
    }

    protected abstract void execute(Context context, ValueSet values, OptionSet options, List<String> args) throws X5Exception;
}
