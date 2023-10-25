package org.adjective.x5.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class StdIO {

    private final PrintStream output;
    private final InputStream input;

    public StdIO(PrintStream output, InputStream input) {
        this.output = output;
        this.input = input;
    }

    public StdIO(OutputStream output, InputStream input) {
        this(new PrintStream(output), input);
    }

    public PrintStream getOutput() {
        return output;
    }

    public InputStream getInput() {
        return input;
    }

}
