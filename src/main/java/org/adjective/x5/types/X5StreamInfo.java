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

public interface X5StreamInfo {

    String getSourceDescription();

    Optional<Path> getPath();

    Optional<FileType> getFileType();

    Optional<EncodingSyntax> getSyntax();

    default X5StreamInfo withDescriptionPrefix(String prefix) {
        final String prefixWithSpace = prefix.stripTrailing() + ' ';
        return new DelegateInfo(this) {
            @Override
            public String getSourceDescription() {
                return prefixWithSpace + delegate.getSourceDescription();
            }
        };
    }

    default X5StreamInfo withSyntax(EncodingSyntax encodingSyntax) {
        return new DelegateInfo(this) {
            @Override
            public Optional<EncodingSyntax> getSyntax() {
                return Optional.of(encodingSyntax);
            }
        };

    }
}
