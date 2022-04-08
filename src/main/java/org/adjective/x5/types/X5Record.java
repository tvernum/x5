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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.IO;
import org.adjective.x5.util.Values;

public interface X5Record extends X5Object {

    Map<String, X5Object> asMap();

    int size();

    Set<String> names();

    X5Object value(String name);

    @Override
    default String description() {
        return asMap().entrySet()
            .stream()
            .map(e -> e.getKey() + "=" + (e.getValue() == null ? "<null>" : e.getValue().description()))
            .collect(Collectors.joining(" , "));
    }

    @Override
    default X5Type getType() {
        return X5Type.RECORD;
    }

    @Override
    default Map<String, ? extends X5Object> properties() {
        return asMap();
    }

    @Override
    default void writeTo(OutputStream out) throws IOException, X5Exception {
        for (String name : names()) {
            IO.writeUtf8(name, out);
            out.write(':');
            Optional.ofNullable(value(name)).orElse(Values.nullValue(getSource())).writeTo(out);
        }
    }

    @Override
    default boolean isEqualTo(String str) {
        return false;
    }

    @Override
    default boolean isEqualTo(X5Object other) throws X5Exception {
        if (other instanceof X5Record) {
            return Objects.equals(this.asMap(), ((X5Record) other).asMap());
        }
        return false;
    }

}
