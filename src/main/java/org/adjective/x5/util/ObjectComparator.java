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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.adjective.x5.exception.X5Exception;
import org.adjective.x5.io.Debug;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.X5Value;

public class ObjectComparator implements Comparator<X5Object> {
    public static final ObjectComparator INSTANCE = new ObjectComparator();

    static final List<X5Type<?>> TYPE_ORDER = Arrays.asList(
        X5Type.BOOLEAN,
        X5Type.RESULT,
        X5Type.NUMBER,
        X5Type.STRING,
        X5Type.IP_ADDRESS,
        X5Type.PASSWORD,
        X5Type.DISTINGUISHED_NAME,
        X5Type.RELATIVE_DISTINGUISHED_NAME,
        X5Type.ATTRIBUTE_VALUE_ASSERTION,
        X5Type.ALGORITHM,
        X5Type.OID,
        X5Type.ASN1,
        X5Type.DATE,
        X5Type.NULL,
        X5Type.ANY_VALUE,
        X5Type.CERTIFICATE_CHAIN,
        X5Type.CERTIFICATE,
        X5Type.PUBLIC_KEY,
        X5Type.PUBLIC_CREDENTIAL,
        X5Type.PRIVATE_KEY,
        X5Type.SECRET_KEY,
        X5Type.PRIVATE_CREDENTIAL,
        X5Type.ANY_CRYPTO,
        X5Type.KEY_PAIR,
        X5Type.STORE_ENTRY,
        X5Type.STORE,
        X5Type.ENCRYPTION,
        X5Type.SEQUENCE,
        X5Type.RECORD
    );

    @Override
    public int compare(X5Object o1, X5Object o2) {
        int cmp = compareTypes(o1.getType(), o2.getType());
        if (cmp != 0) {
            return cmp;
        }
        if (o1 instanceof X5Value && o2 instanceof X5Value) {
            return compareValues((X5Value) o1, (X5Value) o2);
        }
        if (o1 instanceof Sequence && o2 instanceof Sequence) {
            return compareSequences((Sequence) o1, (Sequence) o2);
        }
        return compareObjects(o1, o2);
    }

    private int compareTypes(X5Type t1, X5Type t2) {
        int o1 = TYPE_ORDER.indexOf(t1);
        int o2 = TYPE_ORDER.indexOf(t2);
        if (o1 == o2) {
            return 0;
        }
        if (o1 == -1) {
            return +1;
        }
        if (o2 == -1) {
            return -1;
        }
        return Integer.compare(o1, o2);
    }

    private int compareValues(X5Value o1, X5Value o2) {
        final Object c1 = o1.value();
        final Object c2 = o2.value();

        final Class<?> class1 = c1.getClass();
        final Class<?> class2 = c2.getClass();

        if (class1 == class2 && o1 instanceof Comparable) {
            return ((Comparable) o1).compareTo(o2);
        }

        return compareObjects(o1, o2);
    }

    private int compareSequences(Sequence seq1, Sequence seq2) {
        try {
            var items1 = seq1.items();
            var items2 = seq2.items();
            var size1 = Iterables.size(items1);
            var size2 = Iterables.size(items2);
            if (size1 != size2) {
                return Integer.compare(size1, size2);
            }
            if (size1 == 0) {
                return 0;
            }
            var itr1 = items1.iterator();
            var itr2 = items2.iterator();
            while (itr1.hasNext()) {
                var o1 = itr1.next();
                var o2 = itr2.next();
                int cmp = compare(o1, o2);
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        } catch (X5Exception e) {
            Debug.error(e, "Cannot get items of sequence");
            return 0;
        }
    }

    private int compareObjects(X5Object o1, X5Object o2) {
        return o1.description().compareTo(o2.description());
    }

}
