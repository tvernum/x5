package org.adjective.x5.command.util;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;

import org.adjective.x5.cli.SimpleConverter;
import org.adjective.x5.io.password.PasswordSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SimpleOptions {

    private final OptionParser parser;
    private final NonOptionArgumentSpec<String> arguments;

    public SimpleOptions() {
        this(true);
    }

    public SimpleOptions(boolean allowNonOptions) {
        parser = new OptionParser();
        arguments = allowNonOptions ? parser.nonOptions("args") : null;
    }

    public static class ParsedOptions {
        public final OptionSet optionSet;
        public final List<String> args;

        public ParsedOptions(OptionSet optionSet, List<String> args) {
            this.optionSet = optionSet;
            this.args = args;
        }

        public <T> Optional<T> maybe(OptionSpec<T> spec) {
            if (optionSet.has(spec)) {
                return Optional.of(spec.value(optionSet));
            } else {
                return Optional.empty();
            }
        }

        public <T> T get(OptionSpec<T> spec) {
            return maybe(spec).orElseThrow(() -> new IllegalStateException("No option provided for " + spec));
        }

        public <T> T get(OptionSpec<T> spec, T defaultValue) {
            return maybe(spec).orElse(defaultValue);
        }

        public boolean has(OptionSpec<?> spec) {
            return optionSet.has(spec);
        }

    }

    public ParsedOptions parse(List<String> input) {
        final OptionSet options = parser.parse(input.toArray(new String[0]));
        return new ParsedOptions(options, arguments == null ? List.of() : arguments.values(options));
    }

    public OptionSpec<PasswordSpec> declarePasswordOption(String name, String... altNames) {
        return declareOption(
            name,
            altNames,
            "Password",
            new SimpleConverter<>(PasswordSpec.class, PasswordSpec::parse),
            "password specification"
        );
    }

    public OptionSpec<String> declareStringOption(String name, String... altNames) {
        return declareOption(name, altNames, "").withRequiredArg();
    }

    public OptionSpec<Void> declareValuelessOption(String name, String... altNames) {
        return declareOption(name, altNames, "");
    }

    private ArgumentAcceptingOptionSpec<PasswordSpec> declareOption(
        String name,
        String[] altNames,
        String description,
        SimpleConverter<PasswordSpec> convertor,
        String argDescription
    ) {
        final OptionSpecBuilder builder = declareOption(name, altNames, description);
        return requireArgument(builder, convertor, argDescription);
    }

    private OptionSpecBuilder declareOption(String name, String[] altNames, String description) {
        OptionSpecBuilder builder;
        if (altNames.length == 0) {
            builder = parser.accepts(name, description);
        } else {
            List<String> names = new ArrayList<>(altNames.length + 1);
            names.add(name);
            names.addAll(Arrays.asList(altNames));
            builder = parser.acceptsAll(names, description);
        }
        return builder;
    }

    private static ArgumentAcceptingOptionSpec<PasswordSpec> requireArgument(
        OptionSpecBuilder builder,
        SimpleConverter<PasswordSpec> convertor,
        String argDescription
    ) {
        return builder.withRequiredArg().describedAs(argDescription).withValuesConvertedBy(convertor);
    }

}
