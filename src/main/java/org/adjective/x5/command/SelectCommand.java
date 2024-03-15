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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.FixedRecord;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Record;
import org.adjective.x5.types.X5Type;

public class SelectCommand extends AbstractSimpleCommand {

    @Override
    public String name() {
        return "select";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception, IOException {
        requireMinimumArgumentCount(1, args);
        final X5Record record = popStack(values, X5Type.RECORD);
        final X5Record result = record.filter(buildPredicate(args));
        values.push(result);
    }

    static BiPredicate<String, X5Object> buildPredicate(List<String> args) {
        final Set<String> simpleMatch = new HashSet<>(args.size());
        final List<Predicate<String>> globMatch = new ArrayList<>(args.size());
        for (String arg : args) {
            if (arg.indexOf('*') == -1) {
                simpleMatch.add(arg);
            } else {
                globMatch.add(buildPredicate(arg));
            }
        }
        if (globMatch.isEmpty()) {
            return (key, value) -> simpleMatch.contains(key);
        } else {
            return (key, value) -> simpleMatch.contains(key) || globMatch.stream().anyMatch(predicate -> predicate.test(key));
        }
    }

    static Predicate<String> buildPredicate(String arg) {
        final int firstStar = arg.indexOf('*');
        if (firstStar == arg.length() - 1) {
            final String prefix = arg.substring(0, firstStar);
            return key -> key.startsWith(prefix);
        }
        final int lastStar = arg.lastIndexOf('*');
        if (firstStar == 0) {
            if (lastStar == 0) {
                final String suffix = arg.substring(1);
                return key -> key.endsWith(suffix);
            }
            if (lastStar == arg.length() - 1) {
                final String infix = arg.substring(1, lastStar);
                if (infix.indexOf('*') == -1) {
                    return key -> key.contains(infix);
                }
            }
        }
        if (firstStar == lastStar) {
            final String prefix = arg.substring(0, firstStar);
            final String suffix = arg.substring(lastStar + 1);
            return key -> key.startsWith(prefix) && key.endsWith(suffix);
        }

        final String prefix;
        if (firstStar > 0) {
            prefix = arg.substring(0, firstStar);
            arg = arg.substring(firstStar + 1);
        } else {
            prefix = null;
        }

        final String suffix;
        if (lastStar < arg.length() - 1) {
            suffix = arg.substring(lastStar + 1);
            arg = arg.substring(0, lastStar);
        } else {
            suffix = null;
        }

        final String[] parts = arg.split("\\*");
        return key -> {
            if (prefix != null) {
                if (key.startsWith(prefix) == false) {
                    return false;
                }
                key = key.substring(prefix.length());
            }

            for (String part : parts) {
                int idx = key.indexOf(part);
                if (idx == -1) {
                    return false;
                }
                key = key.substring(idx + part.length());
            }

            if (suffix != null && key.endsWith(suffix) == false) {
                return false;
            }

            return true;
        };
    }
}
