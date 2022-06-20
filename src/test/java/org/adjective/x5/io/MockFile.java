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

package org.adjective.x5.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.FileType;
import org.adjective.x5.types.PathInfo;
import org.adjective.x5.types.X5File;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.Values;

public class MockFile implements X5File {

    private final Path path;
    private final PathInfo info;

    public MockFile(Path path) {
        this.path = path;
        this.info = new PathInfo(path, "mock file", FileType.UNSPECIFIED, Optional.empty());
    }

    public static MockFile any() {
        // TODO : Randomised testing
        return new MockFile(Paths.get("/mock.file"));
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public X5StreamInfo info() throws X5Exception {
        return info;
    }

    @Override
    public X5Object asObject() throws X5Exception {
        return Values.nullValue(info);
    }

    @Override
    public long size() throws IOException {
        return Files.size(path);
    }
}
