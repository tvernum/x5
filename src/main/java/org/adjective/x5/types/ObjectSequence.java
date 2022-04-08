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
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.util.Lazy;

public class ObjectSequence implements Sequence {
    private final List<? extends X5Object> objects;
    private final X5StreamInfo source;
    private final Supplier<String> type;

    public ObjectSequence(List<? extends X5Object> objects, X5StreamInfo source) {
        this.objects = List.copyOf(objects);
        this.source = source;
        if (this.objects.isEmpty()) {
            this.type = () -> "Sequence{}";
        } else {
            this.type = Lazy.uncheckedLazy(
                () -> "Sequence{" + objects.stream().map(X5Object::getTypeName).distinct().sorted().collect(Collectors.joining("|")) + "}"
            ).unchecked();
        }
    }

    @Override
    public <X extends X5Object> Optional<X> as(Class<X> type) {
        return Sequence.super.as(type).or(() -> {
            List<? extends X5Object> matches = objects.stream().filter(type::isInstance).collect(Collectors.toList());
            if (matches.size() == 1) {
                return Optional.of(type.cast(matches.get(0)));
            } else {
                matches = objects.stream().map(o -> o.as(type)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
                if (matches.size() == 1) {
                    return Optional.of(type.cast(matches.get(0)));
                } else {
                    return Optional.empty();
                }
            }
        });
    }

    @Override
    public boolean isEqualTo(String str) {
        return false;
    }

    @Override
    public Iterable<? extends X5Object> items() {
        return objects;
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public String getTypeName() {
        return type.get();
    }

    @Override
    public X5Type getType() {
        return X5Type.SEQUENCE;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        for (X5Object o : objects) {
            o.writeTo(out);
        }
    }
}
