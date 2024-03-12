package org.adjective.x5.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

class TrimReaderTest {

    @Test
    public void testReadEmptyInput() throws Exception {
        try (var reader = reader("")) {
            assertThat(reader.read()).isEqualTo(-1);
        }
    }

    @Test
    public void testReadIndentedContent() throws Exception {
        var content = """
            line 1
             line 2
            \tline 3
            line 4

              line 6
             line 7

            line 9
            """;
        try (var reader = reader(content)) {
            assertThat(read(reader, 4)).isEqualTo("line");
            assertThat(read(reader, 2)).isEqualTo(" 1");
            assertThat(read(reader, 1)).isEqualTo("\n");
            assertThat(reader.read()).isEqualTo('l');
            assertThat(read(reader, 6)).isEqualTo("ine 2\n");
            assertThat(read(reader, 7)).isEqualTo("line 3\n");
            assertThat(read(reader, 15)).isEqualTo("line 4\n\nline 6\n");
            assertThat(read(reader, 6)).isEqualTo("line 7");
            assertThat(read(reader, 2)).isEqualTo("\n\n");
            assertThat(read(reader, 7)).isEqualTo("line 9\n");
            assertThat(reader.read()).isEqualTo(-1);
        }
    }

    @Test
    public void testReadPaddedContent() throws Exception {
        var content = String.join("\n", " abc ", "def ", "ghi", " k l m n ", "        op", "", "qr st   ", "uvw    ", "      xyz    ");
        try (var reader = reader(content)) {
            assertThat(read(reader, 4)).isEqualTo("abc\n");
            assertThat(read(reader, 4)).isEqualTo("def\n");
            assertThat(read(reader, 4)).isEqualTo("ghi\n");
            assertThat(reader.read()).isEqualTo('k');
            assertThat(read(reader, 3)).isEqualTo(" l ");
            assertThat(reader.read()).isEqualTo('m');
            assertThat(read(reader, 2)).isEqualTo(" n");
            assertThat(reader.read()).isEqualTo('\n');
            assertThat(read(reader, 3)).isEqualTo("op\n");
            assertThat(reader.read()).isEqualTo('\n');
            assertThat(read(reader, 6)).isEqualTo("qr st\n");
            assertThat(read(reader, 4)).isEqualTo("uvw\n");
            assertThat(read(reader, 3)).isEqualTo("xyz");
            assertThat(reader.read()).isEqualTo(-1);
        }
    }

    private String read(Reader r, int len) throws IOException {
        char[] c = new char[len];
        assertThat(r.read(c)).isEqualTo(len);
        return new String(c);
    }

    private TrimReader reader(String content) {
        return new TrimReader(new StringReader(content));
    }

}
