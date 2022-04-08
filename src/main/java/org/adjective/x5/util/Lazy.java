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

import java.util.function.Supplier;

public class Lazy<T, E extends Exception> implements CheckedSupplier<T, E> {

    private final CheckedSupplier<T, E> supplier;
    private volatile boolean set;
    private T value;

    public static <T, E extends Exception> Lazy<T, E> lazy(CheckedSupplier<T, E> supplier) {
        return new Lazy<>(supplier);
    }

    public static <T> Lazy<T, RuntimeException> uncheckedLazy(Supplier<T> supplier) {
        return new Lazy<>(CheckedSupplier.of(supplier));
    }

    public Lazy(CheckedSupplier<T, E> supplier) {
        this.supplier = supplier;
        this.set = false;
    }

    public void clear() {
        synchronized (this) {
            if (set) {
                set = false;
            }
        }
    }

    @Override
    public T get() throws E {
        if (set) {
            return value;
        }
        synchronized (this) {
            if (set == false) {
                value = this.supplier.get();
                set = true;
            }
        }
        return value;
    }
}
