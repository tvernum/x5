package org.adjective.x5.types;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.IO;
import org.adjective.x5.types.value.X5Boolean;
import org.adjective.x5.types.value.X5String;
import org.adjective.x5.util.Values;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public interface X5Result extends X5Object , ToTextValue {

    String error();

    default boolean isError() {
        return error() == null;
    }

    default boolean isOK() {
        return isError() == false;
    }

    @Override
    default String description() {
        return isError() ? "Err:" + error() : "OK";
    }

    @Override
    default CharSequence toTextValue() {
        return description();
    }

    @Override
    default X5Type getType() {
        return X5Type.RESULT;
    }

    @Override
    default Map<String, ? extends X5Object> properties() {
        final Map.Entry<String, X5Boolean> ok = Map.entry("ok", asBoolean());
        if (isError()) {
            return Collections.unmodifiableMap(new TreeMap<>(Map.ofEntries(Map.entry("error", asString()), ok)));
        } else {
            return Map.ofEntries(ok);
        }
    }

    @Override
    default void writeTo(OutputStream out) throws IOException, X5Exception {
        out.write(description().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    default boolean isEqualTo(String str) {
        return Objects.equals(error(), str);
    }

    @Override
    default boolean isEqualTo(X5Object other) throws X5Exception {
        if (other instanceof X5Result) {
            return Objects.equals(this.error(), ((X5Result) other).error());
        }
        if (other instanceof X5Boolean) {
            return Objects.equals(this.isOK(), ((X5Boolean) other).value());
        }
        return false;
    }

    @Override
    default <X extends X5Object> Optional<X> as(Class<X> type) {
        if (type.isAssignableFrom(X5Boolean.class)) {
            return Optional.of(type.cast(asBoolean()));
        }
        if (type.isAssignableFrom(X5String.class)) {
            return Optional.of(type.cast(asString()));
        }
        return X5Object.super.as(type);
    }

    private X5Boolean asBoolean() {
        return Values.bool(isOK(), getSource());
    }

    private X5String asString() {
        return Values.string(error(), getSource());
    }
}
