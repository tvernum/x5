package org.adjective.x5.io;

import org.adjective.x5.exception.FileReadException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.password.PasswordSupplier;
import org.adjective.x5.types.X5File;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.util.CheckedSupplier;
import org.adjective.x5.util.Lazy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BaseFile implements X5File {
    private final Path path;
    private final CheckedSupplier<X5Object, X5Exception> object;

    public BaseFile(Path path, PasswordSupplier passwordSupplier) {
        this.path = path;
        this.object = Lazy.lazy(() -> {
            try (InputStream in = open()) {
                return FileParser.getInstance().read(in, this, passwordSupplier);
            } catch (IOException e) {
                throw new FileReadException(path, e);
            }
        });
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public long size() throws IOException {
        return Files.size(path);
    }

    @Override
    public X5StreamInfo info() throws X5Exception {
        return object.get().getSource();
    }

    @Override
    public X5Object asObject() throws X5Exception {
        return object.get();
    }

    protected abstract InputStream open() throws IOException;
}
