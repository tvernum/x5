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

package org.adjective.x5.test.util;

import java.io.IOException;
import java.nio.file.Path;

import org.adjective.x5.io.password.PasswordSpec;
import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.types.value.Password;

public class EmptyPasswordSupplier implements PasswordSupplier {
    @Override
    public Password get(Path path) {
        throw new IllegalArgumentException("No passwords available for path " + path);
    }

    @Override
    public Password forSpec(PasswordSpec spec) throws IOException {
        throw new IllegalArgumentException("No passwords available for spec " + spec);
    }
}
