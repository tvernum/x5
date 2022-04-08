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
import java.util.List;
import java.util.Optional;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.Debug;
import org.adjective.x5.types.ObjectSequence;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.X5Object;

public class RecurseCommand extends AbstractCommand {
    @Override
    public String name() {
        return "recurse";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception, IOException {
        final X5Object root = values.pop();
        final List<X5Object> sequence = new ArrayList<>();
        execute(root, sequence);
        values.push(new ObjectSequence(sequence, root.getSource().withDescriptionPrefix("recursion of")));
    }

    public void execute(X5Object object, List<X5Object> result) throws X5Exception {
        Debug.printf("Recursing into %s", object.description());
        if (result.contains(object)) {
            Debug.printf("Cycle detected, ignoring %s", object.description());
            return;
        }
        result.add(object);
        if (object instanceof Sequence) {
            Sequence sequence = (Sequence) object;
            recurse(sequence, result);
        } else {
            final Optional<Sequence> as = object.as(Sequence.class);
            if (as.isPresent()) {
                recurse(as.get(), result);
            }
        }
    }

    private void recurse(Sequence sequence, List<X5Object> result) throws X5Exception {
        for (X5Object i : sequence.items()) {
            execute(i, result);
        }
    }

}
