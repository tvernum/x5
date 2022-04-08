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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.adjective.x5.exception.InvalidTargetException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.ObjectSequence;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.util.ObjectComparator;

public class SortCommand extends AbstractCommand {

    @Override
    public String name() {
        return "sort";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        requireArgumentCount(0, args);
        final X5Object value = values.pop();
        final Optional<Sequence> optSeq = value.as(Sequence.class);
        final Sequence seq = optSeq.orElseThrow(() -> new InvalidTargetException(value, "Cannot sort non-sequence"));
        final Sequence sorted = sorted(seq);
        values.push(sorted);
    }

    private Sequence sorted(Sequence seq) throws X5Exception {
        return new ObjectSequence(sorted(seq.items()), seq.getSource().withDescriptionPrefix("sorted"));
    }

    private List<X5Object> sorted(Iterable<? extends X5Object> items) {
        if (items instanceof Collection) {
            return sort(new ArrayList<>((Collection<? extends X5Object>) items));
        }
        final List<X5Object> list = new ArrayList<>();
        for (var i : items) {
            list.add(i);
        }
        return sort(list);
    }

    private List<X5Object> sort(List<X5Object> list) {
        Collections.sort(list, ObjectComparator.INSTANCE);
        return list;
    }
}
