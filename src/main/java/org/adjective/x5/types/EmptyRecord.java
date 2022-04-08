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

import java.util.Map;
import java.util.Set;

public class EmptyRecord implements X5Record {

    private final X5StreamInfo source;

    public EmptyRecord(X5StreamInfo source) {
        this.source = source;
    }

    @Override
    public Map<String, X5Object> asMap() {
        return Map.of();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<String> names() {
        return Set.of();
    }

    @Override
    public X5Object value(String name) {
        return null;
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }
}
