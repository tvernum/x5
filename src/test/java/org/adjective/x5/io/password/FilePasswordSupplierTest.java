package org.adjective.x5.io.password;

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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.adjective.x5.command.Environment;
import org.adjective.x5.types.value.Password;
import org.junit.jupiter.api.Test;

class FilePasswordSupplierTest {

    @Test
    public void readFileSpec() throws IOException {
        final FilePasswordSupplier supplier = new FilePasswordSupplier(new Environment(), List.<String>of());
        final Path file = Files.createTempFile("test-password-", ".txt");
        Files.writeString(file, "test\n");
        try {
            final Password password = supplier.forSpec(new PasswordSpec(PasswordSpec.Type.FILE, file.toString()));
            assertThat(password.chars()).contains('t', 'e', 's', 't');
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
