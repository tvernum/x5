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

package org.adjective.x5.command;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

import org.adjective.x5.exception.FileWriteException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;

public class WriteCommand extends AbstractSimpleCommand {
    @Override
    public String name() {
        return "write";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        requireArgumentCount(1, args);
        X5Object object = values.peek();
        final String dest = args.get(0);
        if (dest.equals("-")) {
            try {
                write(object, context.out());
            } catch (IOException e) {
                throw new FileWriteException(object, Path.of("-"), e);
            }
        } else {
            Path path = context.fileSystem().resolve(dest);
            try (OutputStream out = context.fileSystem().writeTo(path, false)) {
                write(object, out);
            } catch (IOException e) {
                throw new FileWriteException(object, path, e);
            }
        }
    }

    private void write(X5Object object, OutputStream out) throws IOException, X5Exception {
        object.writeTo(new FilterOutputStream(out) {
            @Override
            public void close() throws IOException {
                // ignore close from object
            }
        });
        out.flush();
    }

}
