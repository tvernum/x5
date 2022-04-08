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

package org.adjective.x5.types.crypto;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.IO;
import org.adjective.x5.types.CryptoStore;
import org.adjective.x5.types.CryptoValue;
import org.adjective.x5.types.StoreEntry;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.util.Values;

public abstract class AbstractStoreEntry implements StoreEntry {
    protected final String name;
    protected final CryptoValue value;

    public AbstractStoreEntry(String name, CryptoValue value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean isEqualTo(String str) {
        return this.name.equals(str);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public CryptoValue value() {
        return this.value;
    }

    @Override
    public String description() {
        return getTypeName() + "[" + name + "]=" + value.description();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException, X5Exception {
        IO.writeUtf8("Bag Attributes\n    friendlyName: ", out);
        IO.writeUtf8(name, out);
        out.write('\n');
        value.writeTo(out);
    }

    @Override
    public Map<String, ? extends X5Object> properties() {
        Map<String, X5Object> properties = new LinkedHashMap<>();
        properties.put("name", Values.string(name));
        properties.put("value", value);
        return properties;
    }

    protected abstract Optional<? extends CryptoStore> keyStore();

    @Override
    public boolean isEqualTo(X5Object obj) {
        if (obj instanceof StoreEntry) {
            StoreEntry other = (StoreEntry) obj;
            if (this.name.equals(other.name()) == false) {
                return false;
            }
            if (other instanceof AbstractStoreEntry && this.keyStore() == ((AbstractStoreEntry) other).keyStore()) {
                return true;
            }
            return this.value.equals(other.value());
        }
        return false;
    }
}
