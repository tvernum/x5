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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import org.adjective.x5.io.X5FileSystem;
import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.test.util.EmptyPasswordSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.value.X5String;
import org.adjective.x5.util.Values;

class HexCommandTest {

    private Context context;
    private ByteArrayOutputStream output;
    private ValueStack values;

    @BeforeEach
    public void buildContext() {
        final PasswordSupplier passwords = new EmptyPasswordSupplier();
        this.output = new ByteArrayOutputStream();
        this.context = new Context(
            new PrintStream(output),
            new ByteArrayInputStream(new byte[0]),
            new X5FileSystem(passwords),
            passwords,
            new Environment(),
            new Properties()
        );
        values = new ValueStack();
    }

    @Test
    public void testNoArgs() throws Exception {
        push(0xabcde);
        execute(new HexCommand(), List.of());
        assertThat(popString()).isEqualTo("abcde");
    }

    @Test
    public void testOneArgRequiringZeroPadding() throws Exception {
        push(0xabcde);
        execute(new HexCommand(), List.of(":"));
        assertThat(popString()).isEqualTo("0a:bc:de");
    }

    @Test
    public void testOneArgWithoutPadding() throws Exception {
        push(0x12345678);
        execute(new HexCommand(), List.of("-"));
        assertThat(popString()).isEqualTo("12-34-56-78");
    }

    @Test
    public void testTwoArgsRequiringZeroPadding() throws Exception {
        push(0xabcde);
        execute(new HexCommand(), List.of(":", "4"));
        assertThat(popString()).isEqualTo("000a:bcde");
    }

    @Test
    public void testTwoArgsWithoutPadding() throws Exception {
        push(0x123456789L);
        execute(new HexCommand(), List.of("..", "3"));
        assertThat(popString()).isEqualTo("123..456..789");
    }

    private void execute(SimpleCommand command, List<String> args) throws X5Exception, IOException {
        command.execute(context, values, args);
    }

    private String popString() throws X5Exception {
        X5Object obj = values.pop();
        assertThat(obj).isInstanceOf(X5String.class);
        return ((X5String) obj).value();
    }

    private void push(long value) {
        values.push(Values.number(value, source()));
    }

    private X5StreamInfo source() {
        return Values.source("test." + getClass().getSimpleName());
    }

}
