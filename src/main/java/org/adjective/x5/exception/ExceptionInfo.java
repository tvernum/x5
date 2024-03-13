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

package org.adjective.x5.exception;

import java.util.stream.Stream;

public class ExceptionInfo {
    private final Throwable throwable;

    public ExceptionInfo(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean hasCauseOrSuppressed(Class<? extends Throwable> cause) {
        return hasCauseOrSuppressed(cause, true);
    }

    public boolean hasCauseOrSuppressed(Class<? extends Throwable> causeType, boolean checkSuppressed) {
        if (checkSuppressed) {
            return hasCauseOrSuppressed(this.throwable, causeType);
        } else {
            for (Throwable cause = throwable; cause != null; cause = cause.getCause()) {
                if (causeType.isAssignableFrom(cause.getClass())) {
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean hasCauseOrSuppressed(Throwable throwable, Class<? extends Throwable> causeType) {
        if (throwable == null) {
            return false;
        }
        if (causeType.isAssignableFrom(throwable.getClass())) {
            return true;
        }
        if (Stream.of(throwable.getSuppressed()).anyMatch(t -> hasCauseOrSuppressed(t, causeType))) {
            return true;
        }
        return hasCauseOrSuppressed(throwable.getCause(), causeType);
    }
}
