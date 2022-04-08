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

import java.util.Deque;
import java.util.LinkedList;

import org.adjective.x5.exception.ValueSetException;
import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;

public class ValueStack implements ValueSet {

    private Deque<X5Object> stack;

    public ValueStack() {
        this(new LinkedList<>());
    }

    private ValueStack(Deque<X5Object> stack) {
        this.stack = stack;
    }

    @Override
    public boolean hasValue() {
        return this.stack.size() > 0;
    }

    @Override
    public X5Object pop() throws X5Exception {
        if (stack.isEmpty()) {
            throw new ValueSetException("Attempt to use a stack value that does not exist");
        }
        return stack.pop();
    }

    @Override
    public X5Object peek() throws X5Exception {
        if (stack.isEmpty()) {
            throw new ValueSetException("Attempt to use a stack value that does not exist");
        }
        return stack.peek();
    }

    @Override
    public void push(X5Object object) {
        stack.push(object);
    }

    @Override
    public ValueSet duplicate() {
        return new ValueStack(new LinkedList<>(this.stack));
    }
}
