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

package org.adjective.x5.types.value;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.IO;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;

public class X5Date extends AbstractValueType<ZonedDateTime> {
    public X5Date(ZonedDateTime value, X5StreamInfo source) {
        super(value, source);
    }

    public X5Date(Date date, X5StreamInfo source) {
        this(date.toInstant().atZone(ZoneOffset.UTC), source);
    }

    @Override
    public X5Type getType() {
        return X5Type.DATE;
    }

    @Override
    public boolean isEqualTo(String other) {
        try {
            return value.toString().equalsIgnoreCase(other) || value.equals(ZonedDateTime.parse(other));
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        IO.writeUtf8(value.toString(), out);
    }
}
