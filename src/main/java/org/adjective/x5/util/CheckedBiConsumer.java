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

import java.util.function.BiConsumer;

import org.adjective.x5.exception.CommandExecutionException;
import org.adjective.x5.exception.UncheckedException;

public interface CheckedBiConsumer<S, T, E extends Exception> {

    void accept(S s, T t) throws E;

    default BiConsumer<S, T> unchecked() {
        return (a, b) -> {
            try {
                CheckedBiConsumer.this.accept(a, b);
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception ex) {
                throw new UncheckedException(CheckedBiConsumer.this.getClass().getSimpleName() + " failed", ex);
            }
        };
    }

}
