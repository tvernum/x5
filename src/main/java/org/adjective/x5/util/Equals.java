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

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.types.X5Object;

public final class Equals {

    public static boolean equals(X5Object[] arr1, X5Object[] arr2) throws X5Exception {
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i].isEqualTo(arr2[i]) == false) {
                return false;
            }
        }
        return true;
    }
}
