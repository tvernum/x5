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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.adjective.x5.exception.UncheckedException;
import org.adjective.x5.exception.X5Exception;

public interface Sequence extends X5Object {

    Iterable<? extends X5Object> items() throws X5Exception;

    @Override
    default Map<String, ? extends X5Object> properties() {
        try {
            final Map<String, X5Object> map = new LinkedHashMap<>();
            int i = 0;
            for (X5Object item : items()) {
                i++;
                map.put("item." + i, item);
            }
            return map;
        } catch (X5Exception e) {
            throw new UncheckedException("Failed to process items in " + this, e);
        }
    }

    @Override
    default boolean isEqualTo(X5Object other) throws X5Exception {
        if (other instanceof Sequence) {
            return areEqual(this.items(), ((Sequence) other).items());
        }
        return false;
    }

    static boolean areEqual(Iterable<? extends X5Object> seq1, Iterable<? extends X5Object> seq2) throws X5Exception {
        Iterator<? extends X5Object> itr1 = seq1.iterator();
        Iterator<? extends X5Object> itr2 = seq2.iterator();
        while (itr1.hasNext() && itr2.hasNext()) {
            X5Object obj1 = itr1.next();
            X5Object obj2 = itr2.next();
            if (obj1.isEqualTo(obj2) == false) {
                return false;
            }
        }
        if (itr1.hasNext() || itr2.hasNext()) {
            return false;
        } else {
            return true;
        }
    }

}
