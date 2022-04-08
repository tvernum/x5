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

import java.math.BigInteger;
import java.util.Base64;

public class Functions {

    private static final char[] HEX_CHARS = new char[16];

    static {
        for (int i = 0; i < HEX_CHARS.length; i++) {
            HEX_CHARS[i] = Character.forDigit(i, 16);
        }
    }

    public static String hex(Number number) {
        if (number instanceof BigInteger) {
            BigInteger big = (BigInteger) number;
            return big.toString(16);
        }
        return Long.toHexString(number.longValue());
    }

    public static String hex(byte[] bytes, char separator) {
        StringBuilder buf = new StringBuilder(bytes.length * (separator == 0 ? 2 : 3));
        for (byte b : bytes) {
            if (buf.length() > 0 && separator != 0) {
                buf.append(separator);
            }
            final int v1 = b >> 4 & 0XF;
            final int v2 = b & 0XF;
            buf.append(HEX_CHARS[v1]);
            buf.append(HEX_CHARS[v2]);
        }
        return buf.toString();
    }

    public static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static CharSequence insertSeparator(String str, String separator, int numberChars) {
        final StringBuilder buf = new StringBuilder(str.length() + str.length() / numberChars + 1);
        final int start = str.length() % numberChars;
        if (start != 0) {
            buf.append(str, 0, start);
        }
        for (int i = start; i < str.length(); i += numberChars) {
            if (i != 0) {
                buf.append(separator);
            }
            buf.append(str, i, i + numberChars);
        }
        return buf;
    }

    public static String wrap(String str, int width) {
        final StringBuilder buf = new StringBuilder(str.length() + str.length() / width + 1);
        int start = 0;
        while (str.length() - start > width) {
            buf.append(str.substring(start, start + width)).append('\n');
            start += width;
        }
        if (str.length() - start > 0) {
            buf.append(str.substring(start)).append('\n');
        }
        return buf.toString();
    }
}
