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

import java.util.List;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.ToTextValue;
import org.adjective.x5.types.X5Object;

public class PrintCommand extends AbstractCommand {

    @Override
    public String name() {
        return "print";
    }

    @Override
    public void execute(Context context, ValueSet values, List<String> args) throws X5Exception {
        print(context, values.peek());
    }

    private void print(Context context, X5Object obj) throws X5Exception {
        CharSequence value = valueOf(obj);
        context.out().println(value);
    }

    private CharSequence valueOf(X5Object obj) throws X5Exception {
        if (obj instanceof ToTextValue) {
            return ((ToTextValue) obj).toTextValue();
        } else if (obj instanceof Sequence) {
            Sequence seq = (Sequence) obj;
            StringBuilder builder = new StringBuilder("(");
            for (var item : seq.items()) {
                builder.append(valueOf(item));
                builder.append(",");
            }
            if (builder.length() == 1) {
                builder.append(" )");
            } else {
                builder.setCharAt(builder.length() - 1, ')');
            }
            return builder;
        } else {
            return obj.description();
        }
    }

}
