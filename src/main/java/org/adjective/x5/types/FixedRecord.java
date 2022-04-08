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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class FixedRecord implements X5Record {

    private final Map<String, X5Object> entries;
    private final X5StreamInfo source;

    public FixedRecord(X5StreamInfo source, Map.Entry<String, ? extends X5Object>... entries) {
        this(Map.ofEntries(entries), source);
    }

    public FixedRecord(Map<String, ? extends X5Object> entries, X5StreamInfo source) {
        this.entries = Collections.unmodifiableMap(entries);
        this.source = source;
    }

    @Override
    public Map<String, X5Object> asMap() {
        return this.entries;
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Set<String> names() {
        return entries.keySet();
    }

    @Override
    public X5Object value(String name) {
        return entries.get(name);
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }
}
