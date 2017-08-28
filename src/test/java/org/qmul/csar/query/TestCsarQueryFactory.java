package org.qmul.csar.query;

import org.junit.Test;
import org.qmul.csar.query.domain.ClassLanguageElement;
import org.qmul.csar.query.domain.MethodLanguageElement;
import org.qmul.csar.query.domain.VisibilityModifier;

import java.util.Optional;

public final class TestCsarQueryFactory {

    // TODO fix output which says: line 1:0 no viable alternative at input '<EOF>'

    private static CsarQuery parse(String query) {
        return CsarQueryFactory.parse(query);
    }

    private static void assertEquals(String query, CsarQuery expected) {
        CsarQuery actual = parse(query);
        org.junit.Assert.assertEquals(expected, actual);
    }

    @Test
    public void test() {
        assertEquals("SELECT method:use:add", new CsarQuery(new MethodLanguageElement(CsarQuery.Type.USAGE, null,
                Optional.empty(), Optional.empty(), "add", null, Optional.empty(), Optional.empty(), null, null, null)));
        assertEquals("class:def:public static final MyClass",
                new CsarQuery(new ClassLanguageElement(CsarQuery.Type.DEFINITION, VisibilityModifier.PUBLIC,
                        Optional.of(true), Optional.of(true), "MyClass", Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(), null)));
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
