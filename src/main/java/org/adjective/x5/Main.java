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

package org.adjective.x5;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.adjective.x5.cli.CommandLine;
import org.adjective.x5.cli.CommandLineParser;
import org.adjective.x5.cli.CommandRunner;
import org.adjective.x5.command.Commands;
import org.adjective.x5.command.Context;
import org.adjective.x5.command.Environment;
import org.adjective.x5.command.ValueSet;
import org.adjective.x5.command.ValueStack;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.Debug;
import org.adjective.x5.io.password.FilePasswordSupplier;
import org.adjective.x5.io.password.InteractivePasswordSupplier;
import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.io.password.SimplePasswordSupplier;

import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.adjective.x5.types.X5Result;

public class Main {
    private final OptionParser parser = new OptionParser();

    private final OptionSpec<Void> helpOption = parser.acceptsAll(List.of("h", "help"), "Show help output");
    private final OptionSpec<Void> debugOption = parser.acceptsAll(List.of("debug"), "Show debugging output");
    private final OptionSpec<String> passwordFileOption = parser.acceptsAll(
        List.of("password-file"),
        "Read passwords from the specified file"
    ).withRequiredArg();
    private final OptionSpec<String> passwordLiteralOption = parser.acceptsAll(List.of("password"), "Use the specified password everywhere")
        .availableUnless(passwordFileOption)
        .withRequiredArg();

    private final NonOptionArgumentSpec<String> arguments = parser.nonOptions("args");

    public Main() {
        parser.posixlyCorrect(true);
    }

    public static void main(String[] args) throws IOException {
        try {
            final int rc = new Main().execute(args);
            System.exit(rc);
        } catch (X5Exception e) {
            Debug.error(e, "Command failed");

            System.err.println();
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.err.println();

            for (Throwable c = e; c != null; c = c.getCause()) {
                System.err.println("> Caused by " + c.getMessage() + " (" + c.getClass().getName() + ")");
            }
        }
    }

    private int execute(String[] args) throws X5Exception, IOException {
        final Environment environment = new Environment();
        PasswordSupplier passwordSupplier = new InteractivePasswordSupplier(environment);

        final OptionSet options = parser.parse(args);
        if (options.has(helpOption)) {
            parser.printHelpOn(System.out);
            return -1;
        }
        if (options.has(debugOption)) {
            Debug.enable();
        }
        if (options.has(passwordFileOption)) {
            passwordSupplier = new FilePasswordSupplier(environment, passwordFileOption.values(options));
        } else if (options.has(passwordLiteralOption)) {
            String password = passwordLiteralOption.value(options);
            Debug.printf("Using literal password [%s]\n", password);
            passwordSupplier = new SimplePasswordSupplier(password);
        }

        final List<String> commandArgs = arguments.values(options);
        if (commandArgs.isEmpty()) {
            System.err.println("Command line is required");
            return -1;
        }
        Debug.printf("Command line is [%s]\n", commandArgs.stream().map(s -> "'" + s + "'").collect(Collectors.joining(" ")));

        final Context context = Context.create(passwordSupplier, environment, new Properties());
        return execute(commandArgs, context);
    }

    int execute(List<String> commandArgs, Context context) throws X5Exception {
        final Commands commands = new Commands();
        final CommandLine cli = new CommandLineParser(commands).parse(commandArgs);
        final ValueSet values = new ValueStack();
        final X5Result result  = cli.execute(new CommandRunner(commands, context, values));
        if(result.isOK()) {
            return 0;
        } else {
            System.err.println(result.error());
            return 1;
        }
    }
}
