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

import java.util.List;

import org.adjective.x5.exception.BadArgumentException;

public abstract class AbstractSimpleCommand extends AbstractCommand implements SimpleCommand {

    protected int integerArgument(List<String> args, int n) throws BadArgumentException {
        requireArgumentCount(n + 1, args);
        final String val = args.get(n);
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            throw new BadArgumentException(
                "Argument " + (n + 1) + " to the '" + name() + "' command must be an integer (found '" + val + "')",
                this
            );
        }
    }

}
