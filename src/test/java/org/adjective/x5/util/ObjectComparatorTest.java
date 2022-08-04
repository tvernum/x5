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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.adjective.x5.types.ObjectSequence;
import org.adjective.x5.types.Sequence;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Type;
import org.adjective.x5.types.value.X5String;
import org.junit.jupiter.api.Test;

class ObjectComparatorTest {

    @Test
    public void testAllTypesHaveAnOrder() {
        for (X5Type t : X5Type.values()) {
            assertThat(ObjectComparator.TYPE_ORDER).contains(t);
        }
    }

    @Test
    public void testCompareSequences() {
        final X5StreamInfo source = Values.source("test");
        final X5String a = Values.string("a");
        final X5String b = Values.string("b");

        Sequence seq0 = new ObjectSequence(List.of(), source);

        Sequence seq1a = new ObjectSequence(List.of(a), source);
        Sequence seq1b = new ObjectSequence(List.of(b), source);

        Sequence seq2aa = new ObjectSequence(List.of(a, a), source);
        Sequence seq2ab = new ObjectSequence(List.of(a, b), source);
        Sequence seq2bb = new ObjectSequence(List.of(b, b), source);

        Sequence seq3aaa = new ObjectSequence(List.of(a, a, a), source);

        assertThat(ObjectComparator.INSTANCE.compare(seq0, seq0)).isEqualTo(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq0, seq1a)).isLessThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq0, seq2ab)).isLessThan(0);

        assertThat(ObjectComparator.INSTANCE.compare(seq1a, seq1a)).isEqualTo(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq1a, seq0)).isGreaterThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq1a, seq1b)).isLessThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq1b, seq1a)).isGreaterThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq1b, seq2aa)).isLessThan(0);

        assertThat(ObjectComparator.INSTANCE.compare(seq2aa, seq2aa)).isEqualTo(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2aa, seq0)).isGreaterThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2aa, seq1a)).isGreaterThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2aa, seq2ab)).isLessThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2aa, seq2bb)).isLessThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2aa, seq3aaa)).isLessThan(0);

        assertThat(ObjectComparator.INSTANCE.compare(seq2ab, seq2ab)).isEqualTo(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2ab, seq2aa)).isGreaterThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2ab, seq1b)).isGreaterThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2ab, seq2bb)).isLessThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2ab, seq3aaa)).isLessThan(0);

        assertThat(ObjectComparator.INSTANCE.compare(seq2bb, seq2bb)).isEqualTo(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2bb, seq2aa)).isGreaterThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2bb, seq2ab)).isGreaterThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2bb, seq1b)).isGreaterThan(0);
        assertThat(ObjectComparator.INSTANCE.compare(seq2bb, seq3aaa)).isLessThan(0);
    }

}
