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

import org.adjective.x5.cli.CommandLineStack;
import org.adjective.x5.cli.CommandRunner;
import org.adjective.x5.exception.X5Exception;

public interface FunctionalCommand extends Command {
    String name();

    void apply(CommandRunner runner, CommandLineStack body) throws X5Exception;
}
