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

package org.adjective.x5.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.adjective.x5.command.Commands;
import org.adjective.x5.types.value.X5String;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandLineParserTest {

    private CommandLineParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandLineParser(new Commands());
    }

    @Test
    public void testParseSimpleCommand() throws Exception {
        final CommandLine cli = parser.parse("read /some/file");
        CommandExecution command = as(CommandExecution.class, cli);
        assertThat(command.getCommand()).isEqualTo("read");
        assertThat(command.getArgs()).containsExactly("/some/file");
    }

    @Test
    public void testParsePipedCommand() throws Exception {
        final CommandLine cli = parser.parse("read /some/file | info");
        assertThat(cli).isInstanceOf(PipedCommand.class);

        PipedCommand pipe = (PipedCommand) cli;
        assertThat(pipe.getCommands()).hasSize(2);

        CommandExecution command0 = as(CommandExecution.class, pipe.getCommands().get(0));
        assertThat(command0.getCommand()).isEqualTo("read");
        assertThat(command0.getArgs()).containsExactly("/some/file");

        CommandExecution command1 = as(CommandExecution.class, pipe.getCommands().get(1));
        assertThat(command1.getCommand()).isEqualTo("info");
        assertThat(command1.getArgs()).isEmpty();
    }

    @Test
    public void testPropertyAccess() throws Exception {
        final CommandLine cli = parser.parse("read 'my.crt' | .issuer | info");
        assertThat(cli).isInstanceOf(PipedCommand.class);

        PipedCommand pipe = (PipedCommand) cli;
        assertThat(pipe.getCommands()).hasSize(3);

        CommandExecution command0 = as(CommandExecution.class, pipe.getCommands().get(0));
        assertThat(command0.getCommand()).isEqualTo("read");
        assertThat(command0.getArgs()).containsExactly("my.crt");

        CommandExecution command1 = as(CommandExecution.class, pipe.getCommands().get(1));
        assertThat(command1.getCommand()).isEqualTo("property");
        assertThat(command1.getArgs()).containsExactly("issuer");

        CommandExecution command2 = as(CommandExecution.class, pipe.getCommands().get(2));
        assertThat(command2.getCommand()).isEqualTo("info");
        assertThat(command2.getArgs()).isEmpty();
    }

    @Test
    public void testNestedPropertyAccess() throws Exception {
        final CommandLine cli = parser.parse("read \"server.p12\" | .entry.server.public | info");
        assertThat(cli).isInstanceOf(PipedCommand.class);

        PipedCommand pipe = (PipedCommand) cli;
        assertThat(pipe.getCommands()).hasSize(3);

        CommandExecution command0 = as(CommandExecution.class, pipe.getCommands().get(0));
        assertThat(command0.getCommand()).isEqualTo("read");
        assertThat(command0.getArgs()).containsExactly("server.p12");

        CommandExecution command1 = as(CommandExecution.class, pipe.getCommands().get(1));
        assertThat(command1.getCommand()).isEqualTo("property");
        assertThat(command1.getArgs()).containsExactly("entry.server.public");

        CommandExecution command2 = as(CommandExecution.class, pipe.getCommands().get(2));
        assertThat(command2.getCommand()).isEqualTo("info");
        assertThat(command2.getArgs()).isEmpty();
    }

    @Test
    public void testParseComplexCommand() throws Exception {
        Consumer<String> test = input -> {
            final CommandLine cli = parser.parse(input);
            PipedCommand pipe = as(PipedCommand.class, cli);
            assertThat(pipe.getCommands()).hasSize(2);

            FunctionExecution f0 = as(FunctionExecution.class, pipe.getCommands().get(0));
            Assertions.assertThat(f0.getFunction().name()).isEqualTo("keystore");
            Assertions.assertThat(f0.getArguments()).hasSize(1);

            FunctionExecution f1 = as(FunctionExecution.class, f0.getArguments().get(0));
            Assertions.assertThat(f1.getFunction().name()).isEqualTo("entry");
            Assertions.assertThat(f1.getArguments()).hasSize(2);

            LiteralValue entryNameValue = as(LiteralValue.class, f1.getArguments().get(0));
            X5String entryNameString = as(X5String.class, entryNameValue.getObject());
            assertThat(entryNameString.value()).isEqualTo("server");

            FunctionExecution f2 = as(FunctionExecution.class, f1.getArguments().get(1));
            Assertions.assertThat(f2.getFunction().name()).isEqualTo("pair");
            Assertions.assertThat(f2.getArguments()).hasSize(2);

            CommandExecution c0 = as(CommandExecution.class, f2.getArguments().get(0));
            assertThat(c0.getCommand()).isEqualTo("read");
            assertThat(c0.getArgs()).containsExactly("server.crt");

            CommandExecution c1 = as(CommandExecution.class, f2.getArguments().get(1));
            assertThat(c1.getCommand()).isEqualTo("read");
            assertThat(c1.getArgs()).containsExactly("server.key");

            CommandExecution c2 = as(CommandExecution.class, pipe.getCommands().get(1));
            assertThat(c2.getCommand()).isEqualTo("write");
            assertThat(c2.getArgs()).containsExactly("/server.p12");
        };
        final String command = "keystore( entry (\"server\", pair ( read server.crt , read 'server.key' ) ) ) | write '/server.p12'";
        test.accept(command);
        for (char c : new char[] { '(', ')', ',', '|' }) {
            final String s = Character.toString(c);
            test.accept(command.replaceAll("\\s+" + Pattern.quote(s) + "\\s+", s));
            test.accept(command.replaceAll("\\s+" + Pattern.quote(s) + "\\s+", " " + s));
            test.accept(command.replaceAll("\\s+" + Pattern.quote(s) + "\\s+", s + " "));
            test.accept(command.replaceAll("\\s+" + Pattern.quote(s) + "\\s+", " " + s + " "));
        }
    }

    private <T> T as(Class<T> type, Object obj) {
        assertThat(obj).isInstanceOf(type);
        return type.cast(obj);
    }

}
