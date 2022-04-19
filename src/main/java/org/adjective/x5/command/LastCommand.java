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
import java.util.Optional;

import org.adjective.x5.exception.InvalidTargetException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.X5Object;

public class LastCommand extends AbstractSimpleCommand {

    @Override
    public String name() {
        return "last";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        requireArgumentCount(0, args);
        final X5Object value = values.pop();
        final Optional<Sequence> optSeq = value.as(Sequence.class);
        final Sequence seq = optSeq.orElseThrow(() -> new InvalidTargetException(value, "Cannot get 'last' element on non-sequence"));
        final X5Object last = last(seq);
        if (last == null) {
            throw new InvalidTargetException(seq, "Cannot get last element of empty sequence");
        }
        values.push(last);
    }

    private X5Object last(Sequence seq) throws X5Exception {
        if (seq.items() instanceof List) {
            List<? extends X5Object> list = (List<? extends X5Object>) seq.items();
            if (list.isEmpty()) {
                return null;
            }
            return list.get(list.size() - 1);
        }
        var itr = seq.items().iterator();
        X5Object last = null;
        while (itr.hasNext()) {
            last = itr.next();
        }
        return last;
    }
}
