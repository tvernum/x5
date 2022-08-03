/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.adjective.x5.util;

import static java.lang.Character.isWhitespace;

import org.adjective.x5.exception.DnParseException;

public class DnStringParser {

    private final String input;
    private int index;
    private Element current;

    public enum ElementType {
        CHAR_LITERAL(),
        CHAR_ESCAPE(),
        HEX_ESCAPE(),
        AVA_ASSIGN('='),
        RDN_PLUS('+'),
        DN_COMMA(','),
        END_STR('\n');

        private final char specialChar;

        ElementType(char special) {
            this.specialChar = special;
        }

        ElementType() {
            this('\0');
        }

        public boolean isSpecial() {
            return specialChar != 0;
        }

        public char getSpecialChar() {
            return specialChar;
        }
    }

    public static class Element {

        public final ElementType type;
        public final char value;

        public Element(ElementType type, char value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Element{" + type.name() + " 0x" + Integer.toString(value, 16) + '}';
        }
    }

    private static final Element AVA_ASSIGN = new Element(ElementType.AVA_ASSIGN, '=');
    private static final Element RDN_PLUS = new Element(ElementType.RDN_PLUS, '+');
    private static final Element DN_COMMA = new Element(ElementType.DN_COMMA, ',');
    private static final Element END_STR = new Element(ElementType.END_STR, '\0');

    public DnStringParser(String input) {
        this.input = input;
        this.index = 0;
        this.current = null;
    }

    public boolean hasNext() {
        return index < input.length();
    }

    public Element next() throws DnParseException {
        this.current = parseNext();
        return this.current;
    }

    public Element current() {
        return current;
    }

    public int pos() {
        return index;
    }

    public String text(int start) {
        return this.input.substring(start, index);
    }

    public String text(int start, int end) {
        return this.input.substring(start, end);
    }

    public void skipWhitespace() {
        while (hasNext() && isWhitespace(input.charAt(index))) {
            index++;
        }
    }

    private Element parseNext() throws DnParseException {
        if (index == input.length()) {
            return END_STR;
        }

        final char ch = readChar();

        switch (ch) {
            case '=':
                return AVA_ASSIGN;
            case '+':
                return RDN_PLUS;
            case ',':
                return DN_COMMA;
            case '\\': {
                char e = readChar();
                if (Character.isLetterOrDigit(e)) {
                    char f = readChar();
                    String seq = new String(new char[] { e, f });
                    if (Character.isLetterOrDigit(f)) {
                        return new Element(ElementType.HEX_ESCAPE, (char) Short.parseShort(seq, 16));
                    } else {
                        throw new DnParseException("Invalid escape sequence [" + seq + "] in DN [" + input + "]");
                    }
                }
                return new Element(ElementType.CHAR_ESCAPE, e);
            }
            default:
                return new Element(ElementType.CHAR_LITERAL, ch);
        }
    }

    private char readChar() throws DnParseException {
        if (index == input.length()) {
            throw new DnParseException("Unexpected end of string in DN [" + input + "]");
        }
        final char c = input.charAt(index);
        index++;
        return c;
    }
}
