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

package org.adjective.x5.exception;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public class FileReadException extends FileException {

    public FileReadException(Path path, Throwable cause) {
        super(buildMessage(path, cause), path, cause);
    }

    private static String buildMessage(Path path, Throwable cause) {
        if (cause instanceof FileNotFoundException) {
            return "File " + path + " does not exist";
        } else {
            return "Failed to read file " + path;
        }
    }
}
