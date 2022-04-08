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

package org.adjective.x5.types;

import java.nio.file.Path;
import java.util.Optional;

public class PathInfo implements X5StreamInfo {

    private final Optional<Path> path;
    private final String description;
    private final Optional<FileType> fileType;
    private final Optional<EncodingSyntax> syntax;

    public PathInfo(Path path, int index, FileType fileType) {
        this(path, buildDescription(path, index), fileType, Optional.empty());
    }

    public PathInfo(Path path, int index, FileType fileType, EncodingSyntax syntax) {
        this(path, buildDescription(path, index), fileType, Optional.of(syntax));
    }

    public PathInfo(Path path, int index, FileType fileType, Optional<EncodingSyntax> syntax) {
        this(path, buildDescription(path, index), fileType, syntax);
    }

    private static String buildDescription(Path path, int index) {
        return index == 0 ? path.toString() : "Object #" + index + " from " + path;
    }

    public PathInfo(Path path, String description, FileType fileType, Optional<EncodingSyntax> syntax) {
        this.path = Optional.of(path);
        this.description = description;
        this.fileType = Optional.of(fileType);
        this.syntax = syntax;
    }

    @Override
    public Optional<Path> getPath() {
        return this.path;
    }

    @Override
    public String getSourceDescription() {
        return description;
    }

    @Override
    public Optional<FileType> getFileType() {
        return this.fileType;
    }

    @Override
    public Optional<EncodingSyntax> getSyntax() {
        return this.syntax;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PathInfo{").append(description);
        path.ifPresent(p -> sb.append(",path=").append(p));
        fileType.ifPresent(t -> sb.append(",type=").append(t));
        syntax.ifPresent(f -> sb.append(",format=").append(f));
        sb.append('}');
        return sb.toString();
    }
}
