package org.adjective.x5.types;

import java.util.List;
import java.util.stream.Collectors;

public class ValueSequence extends ObjectSequence {
    private List<? extends X5Value> objects;

    public ValueSequence(List<? extends X5Value> objects, X5StreamInfo source) {
        super(objects, source);
        this.objects = objects;
    }

    @Override
    public Iterable<? extends X5Value> items() {
        return objects;
    }

    @Override
    public String description() {
        return objects.stream().map(X5Value::toTextValue).collect(Collectors.joining(", ", "[", "]"));
    }
}
