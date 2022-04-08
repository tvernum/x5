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

package org.adjective.x5.io.password;

import java.util.Locale;

public class PasswordSpec {

    enum Type {
        ENV,
        FILE,
        LITERAL,
        INPUT,
    }

    private final Type type;
    private final String text;

    public PasswordSpec(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    public Type type() {
        return type;
    }

    public String text() {
        return text;
    }

    public static PasswordSpec parse(String spec) {
        if (spec.startsWith("$") && spec.length() > 1) {
            if (spec.charAt(1) == '{' && spec.endsWith("}")) {
                return new PasswordSpec(Type.ENV, spec.substring(2, spec.length() - 1));
            } else {
                return new PasswordSpec(Type.ENV, spec.substring(1));
            }
        }
        if (spec.startsWith("@") && spec.length() > 1) {
            return new PasswordSpec(Type.FILE, spec.substring(1));
        }
        if (spec.startsWith("=") && spec.length() > 1) {
            return new PasswordSpec(Type.LITERAL, spec.substring(1));
        }
        if (spec.startsWith("+") && spec.length() > 1) {
            return new PasswordSpec(Type.INPUT, spec.substring(1));
        }
        int colon = spec.indexOf(':');
        if (colon > 0 && colon < spec.length() - 1) {
            String typeName = spec.substring(0, colon - 1);
            String value = spec.substring(colon + 1);
            switch (typeName.toLowerCase(Locale.ROOT)) {
                case "env":
                case "environment":
                    return new PasswordSpec(Type.ENV, value);
                case "file":
                    return new PasswordSpec(Type.FILE, value);
                case "literal":
                case "str":
                case "string":
                    return new PasswordSpec(Type.LITERAL, value);
                case "input":
                case "stdin":
                case "ask":
                    return new PasswordSpec(Type.INPUT, value);
                default:
                    throw new IllegalArgumentException("Unrecognised password specification type: " + typeName);
            }
        }
        throw new IllegalArgumentException("Unrecognised password specification: " + spec);
    }
}
