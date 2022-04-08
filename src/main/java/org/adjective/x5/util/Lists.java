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

package org.adjective.x5.util;

import java.util.AbstractList;
import java.util.List;

public class Lists {

    public static <T> List<T> concat(List<T> firstList, List<T> secondList) {
        final List<T> first = List.copyOf(firstList);
        final List<T> second = List.copyOf(secondList);
        return new AbstractList<T>() {
            @Override
            public T get(int index) {
                if (index < first.size()) {
                    return first.get(index);
                } else {
                    int i2 = index - first.size();
                    if (i2 < second.size()) {
                        return second.get(i2);
                    } else {
                        throw new IndexOutOfBoundsException(index);
                    }
                }
            }

            @Override
            public int size() {
                return first.size() + second.size();
            }
        };
    }
}
