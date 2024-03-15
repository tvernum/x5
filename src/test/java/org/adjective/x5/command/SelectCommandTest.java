package org.adjective.x5.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.adjective.x5.types.X5Object;
import org.junit.jupiter.api.Test;

class SelectCommandTest {

    @Test
    public void testPredicateSimpleStrings() throws Exception {
        final BiPredicate<String, X5Object> predicate = SelectCommand.buildPredicate(List.of("abc", "xyz"));
        assertThat(predicate.test("abc", null)).isEqualTo(true);
        assertThat(predicate.test("bcd", null)).isEqualTo(false);
        assertThat(predicate.test("abcd", null)).isEqualTo(false);
        assertThat(predicate.test("xyz", null)).isEqualTo(true);
        assertThat(predicate.test("wxyz", null)).isEqualTo(false);
        assertThat(predicate.test("abcxyz", null)).isEqualTo(false);
        assertThat(predicate.test("abcdefghijklmnopqrstuvwxyz", null)).isEqualTo(false);
    }

    @Test
    public void testPredicatePrefixGlob() throws Exception {
        final Predicate<String> predicate = SelectCommand.buildPredicate("foo*");
        assertThat(predicate.test("abc")).isEqualTo(false);
        assertThat(predicate.test("foo")).isEqualTo(true);
        assertThat(predicate.test("floor")).isEqualTo(false);
        assertThat(predicate.test("foobar")).isEqualTo(true);
        assertThat(predicate.test("barfoo")).isEqualTo(false);
        assertThat(predicate.test("barfoobar")).isEqualTo(false);
    }

    @Test
    public void testPredicateSuffixGlob() throws Exception {
        final Predicate<String> predicate = SelectCommand.buildPredicate("*foo");
        assertThat(predicate.test("abc")).isEqualTo(false);
        assertThat(predicate.test("foo")).isEqualTo(true);
        assertThat(predicate.test("floor")).isEqualTo(false);
        assertThat(predicate.test("foobar")).isEqualTo(false);
        assertThat(predicate.test("barfoo")).isEqualTo(true);
        assertThat(predicate.test("barfoobar")).isEqualTo(false);
    }

    @Test
    public void testPredicateInfixGlob() throws Exception {
        final Predicate<String> predicate = SelectCommand.buildPredicate("*foo*");
        assertThat(predicate.test("abc")).isEqualTo(false);
        assertThat(predicate.test("foo")).isEqualTo(true);
        assertThat(predicate.test("floor")).isEqualTo(false);
        assertThat(predicate.test("fnoo")).isEqualTo(false);
        assertThat(predicate.test("foobar")).isEqualTo(true);
        assertThat(predicate.test("barfoo")).isEqualTo(true);
        assertThat(predicate.test("barfoobar")).isEqualTo(true);
    }

    @Test
    public void testPredicatePrefixSuffixGlob() throws Exception {
        final Predicate<String> predicate = SelectCommand.buildPredicate("foo*bar");
        assertThat(predicate.test("abc")).isEqualTo(false);
        assertThat(predicate.test("foo")).isEqualTo(false);
        assertThat(predicate.test("floor")).isEqualTo(false);
        assertThat(predicate.test("foobar")).isEqualTo(true);
        assertThat(predicate.test("barfoo")).isEqualTo(false);
        assertThat(predicate.test("foobingbar")).isEqualTo(true);
        assertThat(predicate.test("barbingfoo")).isEqualTo(false);
        assertThat(predicate.test("dogfoocatbar")).isEqualTo(false);
        assertThat(predicate.test("foocatbardog")).isEqualTo(false);
    }

    @Test
    public void testPredicateComplexGlob1() throws Exception {
        final Predicate<String> predicate = SelectCommand.buildPredicate("foo*bar*");
        assertThat(predicate.test("abc")).isEqualTo(false);
        assertThat(predicate.test("foo")).isEqualTo(false);
        assertThat(predicate.test("floor")).isEqualTo(false);
        assertThat(predicate.test("foobar")).isEqualTo(true);
        assertThat(predicate.test("barfoo")).isEqualTo(false);
        assertThat(predicate.test("foobingbar")).isEqualTo(true);
        assertThat(predicate.test("foobingbarboom")).isEqualTo(true);
        assertThat(predicate.test("dogfoocatbar")).isEqualTo(false);
        assertThat(predicate.test("foocatbardog")).isEqualTo(true);
    }

    @Test
    public void testPredicateComplexGlob2() throws Exception {
        final Predicate<String> predicate = SelectCommand.buildPredicate("*foo*bar");
        assertThat(predicate.test("abc")).isEqualTo(false);
        assertThat(predicate.test("foo")).isEqualTo(false);
        assertThat(predicate.test("floor")).isEqualTo(false);
        assertThat(predicate.test("foobar")).isEqualTo(true);
        assertThat(predicate.test("barfoo")).isEqualTo(false);
        assertThat(predicate.test("foobingbar")).isEqualTo(true);
        assertThat(predicate.test("foobingbarboom")).isEqualTo(false);
        assertThat(predicate.test("dogfoocatbar")).isEqualTo(true);
        assertThat(predicate.test("foocatbardog")).isEqualTo(false);
    }

    @Test
    public void testPredicateComplexGlob3() throws Exception {
        final Predicate<String> predicate = SelectCommand.buildPredicate("*foo*bar*");
        assertThat(predicate.test("abc")).isEqualTo(false);
        assertThat(predicate.test("foo")).isEqualTo(false);
        assertThat(predicate.test("floor")).isEqualTo(false);
        assertThat(predicate.test("foobar")).isEqualTo(true);
        assertThat(predicate.test("barfoo")).isEqualTo(false);
        assertThat(predicate.test("foobingbar")).isEqualTo(true);
        assertThat(predicate.test("foobingbarboom")).isEqualTo(true);
        assertThat(predicate.test("dogfoocatbar")).isEqualTo(true);
        assertThat(predicate.test("foocatbardog")).isEqualTo(true);
    }

    @Test
    public void testPredicateComplexGlob4() throws Exception {
        final Predicate<String> predicate = SelectCommand.buildPredicate("*foo*bar*foo*foo*bar*bar*");
        assertThat(predicate.test("-foo-bar-foo-foo-bar-bar-")).isEqualTo(true);
        assertThat(predicate.test("foo-bar-foo-foo-bar-bar")).isEqualTo(true);
        assertThat(predicate.test("foobarfoofoobarbar")).isEqualTo(true);
        assertThat(predicate.test("foobarfoobarbar")).isEqualTo(false);
        assertThat(predicate.test("barfoofoobarbarfoobarfoo")).isEqualTo(false);
    }

    @Test
    public void testPredicateComplexGlob5() throws Exception {
        final Predicate<String> predicate = SelectCommand.buildPredicate("cat*cathy*thy*yellow*lower");
        assertThat(predicate.test("cat_cathy_thy_yellow_lower")).isEqualTo(true);
        assertThat(predicate.test("#cat_cathy_thy_yellow_lower")).isEqualTo(false);
        assertThat(predicate.test("cat_cathy_thy_yellow_lower#")).isEqualTo(false);
        assertThat(predicate.test("cathyellower")).isEqualTo(false);
    }

}
