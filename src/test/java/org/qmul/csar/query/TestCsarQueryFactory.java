package org.qmul.csar.query;

import org.junit.Test;
import org.qmul.csar.query.domain.LanguageElement;
import org.qmul.csar.query.domain.VisibilityModifier;

import java.util.Optional;

public class TestCsarQueryFactory {

    private static CsarQuery parse(String query) {
        return CsarQueryFactory.parse(query);
    }

    private static void assertEquals(String query, CsarQuery csarQuery) {
        org.junit.Assert.assertEquals(parse(query), csarQuery);
    }

    @Test
    public void test() {
        assertEquals("SELECT method:use:add", new CsarQuery(new LanguageElement(LanguageElement.Type.METHOD,
                CsarQuery.Type.USAGE, null, Optional.empty(), Optional.empty(), "add")));
        assertEquals("class:def:public static final MyClass",
                new CsarQuery(new LanguageElement(LanguageElement.Type.CLASS,
                        CsarQuery.Type.DEFINITION, VisibilityModifier.PUBLIC,
                        Optional.of(true), Optional.of(true), "MyClass")));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidQuery1() {
        parse("class:def:");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidQuery2() {
        parse("");
    }
}
