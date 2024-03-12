package org.adjective.x5.io;

class RingCharBuffer {

    private final char[] buffer;
    private int writePosition;
    private int readPosition;

    public RingCharBuffer(int capacity) {
        this.buffer = new char[capacity + 1];
        this.writePosition = 0;
        this.readPosition = 0;
    }

    public void put(char c) {
        int nextPosition = writePosition + 1;
        if (nextPosition == buffer.length) {
            nextPosition = 0;
        }
        if (nextPosition == readPosition) {
            throw new IllegalStateException("buffer is full");
        }
        buffer[writePosition] = c;
        writePosition = nextPosition;
    }

    public void put(char[] cbuf, int off, int len) {
        // TODO: Optimize
        for (int i = 0; i < len; i++) {
            put(cbuf[off + i]);
        }
    }

    public char get() {
        if (readPosition == writePosition) {
            throw new IllegalStateException("buffer is empty");
        }
        final char ch = buffer[readPosition];
        readPosition++;
        if (readPosition == buffer.length) {
            readPosition = 0;
        }
        return ch;
    }

    public void get(char[] cbuf, int off, int len) {
        final int available = available();
        if (available < len) {
            throw new IllegalStateException("Buffer has " + available + " chars available to read, but " + len + " were requested");
        }
        if (readPosition + len > buffer.length) {
            final int avail = buffer.length - readPosition;
            System.arraycopy(buffer, readPosition, cbuf, off, avail);
            readPosition = 0;
            off += avail;
            len -= avail;
        }
        System.arraycopy(buffer, readPosition, cbuf, off, len);
        readPosition += len;
        if (readPosition == buffer.length) {
            readPosition = 0;
        }
    }

    public int available() {
        if (readPosition <= writePosition) {
            return writePosition - readPosition;
        } else {
            return buffer.length - readPosition + writePosition;
        }
    }

    public boolean hasCapacity() {
        return capacity() > 0;
    }

    public int capacity() {
        if (writePosition < readPosition) {
            return readPosition - writePosition - 1;
        } else {
            return buffer.length - writePosition + readPosition - 1;
        }
    }

    public int lastWrite() {
        if (available() == 0) {
            return -1;
        }
        if (writePosition == 0) {
            return buffer[buffer.length - 1];
        } else {
            return buffer[writePosition - 1];
        }
    }

    public boolean undoLastWrite() {
        if (available() == 0) {
            return false;
        }
        if (writePosition == 0) {
            writePosition = buffer.length - 1;
        } else {
            writePosition -= 1;
        }
        return true;
    }
}
