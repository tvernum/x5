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

import org.adjective.x5.exception.UncheckedException;

public interface CheckedSupplier<T, E extends Exception> {

    T get() throws E;

    default Supplier<T> unchecked() {
        return () -> {
            try {
                return CheckedSupplier.this.get();
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception ex) {
                throw new UncheckedException(CheckedSupplier.this.getClass().getSimpleName() + " failed", ex);
            }
        };
    }

    static <T> CheckedSupplier<T, RuntimeException> of(Supplier<T> supplier) {
        return supplier::get;
    }
}
