package org.qmul.csar.query;

import org.junit.Test;
import org.qmul.csar.query.domain.*;

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
    public void testCsarQuery() {
        MethodLanguageElement method1 = new MethodLanguageElement(CsarQuery.Type.USAGE, null, Optional.empty(),
                Optional.empty(), "add", null, Optional.empty(), Optional.empty(), null, null, null);
        DomainQuery domainQuery = new DomainQuery();
        domainQuery.addLogicalOperator(LogicalOperator.NOT);
        domainQuery.addLanguageElement(new ClassLanguageElement(CsarQuery.Type.USAGE, null, Optional.empty(),
                Optional.empty(), "MyClass", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), null));
        domainQuery.addLogicalOperator(LogicalOperator.OR);
        domainQuery.addLanguageElement(new ClassLanguageElement(CsarQuery.Type.DEFINITION, null, Optional.empty(),
                Optional.empty(), "SecondClass", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(true), null));
        assertEquals("SELECT method:use:add CONTAINS not class:use:MyClass OR class:def:inner SecondClass FROM Helpers",
                new CsarQuery(method1, domainQuery, "Helpers"));
    }

    @Test
    public void testDomainQueryPart() {
        MethodLanguageElement method1 = new MethodLanguageElement(CsarQuery.Type.USAGE, null, Optional.empty(),
                Optional.empty(), "add", null, Optional.empty(), Optional.empty(), null, null, null);
        DomainQuery domainQuery = new DomainQuery();
        domainQuery.addLogicalOperator(LogicalOperator.NOT);
        domainQuery.addLanguageElement(new ClassLanguageElement(CsarQuery.Type.USAGE, null, Optional.empty(),
                Optional.empty(), "MyClass", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), null));
        domainQuery.addLogicalOperator(LogicalOperator.OR);
        domainQuery.addLanguageElement(new ClassLanguageElement(CsarQuery.Type.DEFINITION, null, Optional.empty(),
                Optional.empty(), "SecondClass", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(true), null));
        assertEquals("SELECT method:use:add CONTAINS not class:use:MyClass OR class:def:inner SecondClass",
                new CsarQuery(method1, domainQuery, null));
    }

    @Test
    public void testFromLanguageElement() {
        assertEquals("SELECT method:use:_ FROM MyClass", new CsarQuery(new MethodLanguageElement(CsarQuery.Type.USAGE,
                null, Optional.empty(), Optional.empty(), "_", null, Optional.empty(), Optional.empty(), null, null,
                null), null, "MyClass"));
    }

    @Test
    public void testMethodQuery() {
        assertEquals("SELECT method:use:add", new CsarQuery(new MethodLanguageElement(CsarQuery.Type.USAGE, null,
                Optional.empty(), Optional.empty(), "add", null, Optional.empty(), Optional.empty(), null, null, null)));

        MethodLanguageElement method1 = new MethodLanguageElement(CsarQuery.Type.DEFINITION, null,
                Optional.of(true), Optional.empty(), "$sqrt_0", "double", Optional.empty(), Optional.empty(), null,
                null, null
        );
        method1.addParameter(new Identifier("k", "int"));
        method1.addParameter(new Identifier("r", "Runnable"));
        assertEquals("method:def:static double $sqrt_0(int k,  Runnable r )", new CsarQuery(method1));

        MethodLanguageElement method2 = new MethodLanguageElement(CsarQuery.Type.DEFINITION, null,
                Optional.empty(), Optional.empty(), "$", null, Optional.empty(), Optional.empty(), null,
                null, null
        );
        method2.addThrownException("IllegalArgumentException");
        method2.addSuperClass("Main");
        assertEquals("method:def:$ throws(IllegalArgumentException) super(Main)", new CsarQuery(method2));
    }

    @Test
    public void testClassQuery() {
        assertEquals("class:def:public static final MyClass",
                new CsarQuery(new ClassLanguageElement(CsarQuery.Type.DEFINITION, VisibilityModifier.PUBLIC,
                        Optional.of(true), Optional.of(true), "MyClass", Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(), null)));

        ClassLanguageElement class1 = new ClassLanguageElement(CsarQuery.Type.DEFINITION, null,
                Optional.empty(), Optional.empty(), "class12", Optional.of(true), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), null
        );
        class1.addSuperClass("Runnable");
        class1.addSuperClass("Printable");
        class1.addSuperClass("Searchable");
        assertEquals("class:def:interface class12(Runnable,Printable,Searchable)", new CsarQuery(class1));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidClassDefinitionQuery() {
        parse("class:def:");
    }

    @Test(expected = RuntimeException.class)
    public void testEmptyQuery() {
        parse("");
    }
}
