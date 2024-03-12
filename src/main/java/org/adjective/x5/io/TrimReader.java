package org.adjective.x5.io;

import java.io.IOException;
import java.io.Reader;

public class TrimReader extends Reader {

    private final Reader reader;
    private final RingCharBuffer buffer;
    private boolean atLineStart;
    private boolean eof;

    public TrimReader(Reader in) {
        this.reader = in;
        this.buffer = new RingCharBuffer(4096);
        this.atLineStart = true;
        this.eof = false;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (buffer.available() < len) {
            fillBuffer();
        }
        if (buffer.available() == 0 && eof) {
            return -1;
        }
        if (buffer.available() < len) {
            len = buffer.available();
        }
        buffer.get(cbuf, off, len);
        return len;
    }

    private void fillBuffer() throws IOException {
        if (eof) {
            return;
        }
        int r = -2;
        while (buffer.hasCapacity()) {
            if (r < 0) {
                r = internalRead();
            }
            if (r == -1) {
                trimWhitespace();
                return;
            }
            r = processRead((char) r);
        }
    }

    private int processRead(char ch) throws IOException {
        if (isNewline(ch)) {
            trimWhitespace();
            this.buffer.put('\n');
            atLineStart = true;
            int r = internalRead();
            if (r == -1) {
                return -1;
            }
            char next = (char) r;
            if (isNewline(next) && next != ch) {
                // Skip CR after LF (or vice versa)
                return -2;
            } else {
                return r;
            }
        }
        if (Character.isWhitespace(ch) && atLineStart) {
            return -2;
        } else {
            atLineStart = false;
            this.buffer.put(ch);
            return -2;
        }
    }

    private void trimWhitespace() {
        for (;;) {
            int w = buffer.lastWrite();
            if (w == -1) {
                return;
            }
            char c = (char) w;
            if (isNewline(c) == false && Character.isWhitespace(c) == true) {
                buffer.undoLastWrite();
            } else {
                return;
            }
        }
    }

    private boolean isNewline(char c) {
        return c == 0xa || c == 0xd;
    }

    private int internalRead() throws IOException {
        int r = reader.read();
        if (r == -1) {
            eof = true;
        }
        return r;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
