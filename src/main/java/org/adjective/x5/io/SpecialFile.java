package org.adjective.x5.io;

import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.types.X5File;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Supplier;

public class SpecialFile extends BaseFile implements X5File {
    private final Supplier<InputStream> stream;

    public SpecialFile(Path path, Supplier<InputStream> inputStream, PasswordSupplier passwords) {
        super(path, passwords);
        this.stream = inputStream;
    }

    @Override
    protected InputStream open() throws IOException {
        return stream.get();
    }
}
