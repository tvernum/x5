package org.adjective.x5.io;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RingCharBufferTest {

    private static final char[] VALID_CHARS;
    static {
        final var low = ' ';
        final var high = '~';
        VALID_CHARS = new char[high - low + 1];
        for (int i = 0; i < VALID_CHARS.length; i++) {
            VALID_CHARS[i] = (char) (low + i);
        }
    }

    @Test
    public void testWriteThenReadBySingleChar() throws Exception {
        final int capacity = 128;
        final RingCharBuffer buffer = new RingCharBuffer(capacity);
        for (int i = 0; i < capacity * 10 + capacity / 2; i++) {
            assertThat(buffer.capacity()).isEqualTo(capacity);
            assertThat(buffer.available()).isEqualTo(0);

            char ch = VALID_CHARS[i % VALID_CHARS.length]; // TODO randomise
            buffer.put(ch);

            assertThat(buffer.capacity()).isEqualTo(capacity - 1);
            assertThat(buffer.available()).isEqualTo(1);
            assertThat(buffer.get()).isEqualTo(ch);
        }
    }

    @Test
    public void testWriteThenReadByMultipleChars() throws Exception {
        final int capacity = 4096;
        final RingCharBuffer buffer = new RingCharBuffer(capacity);
        for (int i = 0; i < capacity * 5 + capacity / 2; i++) {
            assertThat(buffer.capacity()).isEqualTo(capacity);
            assertThat(buffer.available()).isEqualTo(0);

            final int writeSize = 2 + i % (capacity / 2);
            char[] putC = new char[writeSize];
            for (int j = 0; j < putC.length; j++) {
                putC[j] = VALID_CHARS[(i + i % (j + 1)) % VALID_CHARS.length];

            }
            buffer.put(putC, 0, putC.length);

            assertThat(buffer.capacity()).isEqualTo(capacity - putC.length);
            assertThat(buffer.available()).isEqualTo(putC.length);

            int readSize = 1 + i % (capacity / 3);
            int putOffset = 0;
            int remaining = putC.length;
            while (remaining > 0) {
                if (readSize > remaining) {
                    readSize = remaining;
                } else if (readSize > 10) {
                    readSize = readSize - 1;
                }

                assertThat(readSize).isGreaterThan(0);

                char[] getC = new char[readSize];
                buffer.get(getC, 0, readSize);

                char[] expected = new char[readSize];
                System.arraycopy(putC, putOffset, expected, 0, readSize);
                assertThat(getC).isEqualTo(expected);

                putOffset += readSize;
                remaining -= readSize;
            }
        }
    }

}
