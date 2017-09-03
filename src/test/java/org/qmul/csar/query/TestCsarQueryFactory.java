package org.qmul.csar.query;

import org.junit.Test;
import org.qmul.csar.query.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class TestCsarQueryFactory {

    private static List<String> toList(String... s) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, s);
        return list;
    }

    private static CsarQuery parse(String query) {
        return CsarQueryFactory.parse(query);
    }

    private static void assertEquals(String query, CsarQuery expected) {
        CsarQuery actual = parse(query);
        org.junit.Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCsarQuery() {
        MethodLanguageElement method1 = new MethodLanguageElement(CsarQuery.Type.USE, null, Optional.empty(),
                Optional.empty(), "add", null, Optional.empty(), Optional.empty(), null, null, null);
        ContainsQuery containsQuery = new ContainsQuery();
        containsQuery.addLogicalOperator(LogicalOperator.NOT);
        containsQuery.addLanguageElement(new ClassLanguageElement(CsarQuery.Type.USE, null, Optional.empty(),
                Optional.empty(), "MyClass", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), null));
        containsQuery.addLogicalOperator(LogicalOperator.OR);
        containsQuery.addLanguageElement(new ClassLanguageElement(CsarQuery.Type.DEF, null, Optional.empty(),
                Optional.empty(), "SecondClass", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(true), null));
        assertEquals("SELECT method:use:add CONTAINS not class:use:MyClass OR class:def:inner SecondClass FROM Helpers",
                new CsarQuery(method1, containsQuery, toList("Helpers"), null));
        // TODO test refactor here
    }

    @Test
    public void testContainsQueryPart() {
        MethodLanguageElement method1 = new MethodLanguageElement(CsarQuery.Type.USE, null, Optional.empty(),
                Optional.empty(), "add", null, Optional.empty(), Optional.empty(), null, null, null);
        ContainsQuery containsQuery = new ContainsQuery();
        containsQuery.addLogicalOperator(LogicalOperator.NOT);
        containsQuery.addLanguageElement(new ClassLanguageElement(CsarQuery.Type.USE, null, Optional.empty(),
                Optional.empty(), "MyClass", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), null));
        containsQuery.addLogicalOperator(LogicalOperator.OR);
        containsQuery.addLanguageElement(new ClassLanguageElement(CsarQuery.Type.DEF, null, Optional.empty(),
                Optional.empty(), "SecondClass", Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(true), null));
        assertEquals("SELECT method:use:add CONTAINS not class:use:MyClass OR class:def:inner SecondClass",
                new CsarQuery(method1, containsQuery, null, null));
    }

    @Test
    public void testFromQuery() {
        assertEquals("SELECT method:use:_ FROM MyClass", new CsarQuery(new MethodLanguageElement(CsarQuery.Type.USE,
                null, Optional.empty(), Optional.empty(), "_", null, Optional.empty(), Optional.empty(), null, null,
                null), null, toList("MyClass"), null));
    }

    @Test
    public void testRefactorQuery() {
        // Rename
        assertEquals("SELECT method:use:add REFACTOR rename:addInt", new CsarQuery(
                new MethodLanguageElement(CsarQuery.Type.USE, null, Optional.empty(), Optional.empty(), "add", null,
                        Optional.empty(), Optional.empty(), null, null, null), null, null,
                new RefactorElement.RenameRefactorElement("addInt")));

        // Change parameters
        List<Identifier> parameters1 = new ArrayList<>();
        parameters1.add(new Identifier("k", "int"));
        parameters1.add(new Identifier("r", "Runnable"));
        MethodLanguageElement method1 = new MethodLanguageElement(CsarQuery.Type.DEF, null,
                Optional.of(true), Optional.empty(), "SELECT", "boolean", Optional.empty(), Optional.empty(),
                parameters1, null, null
        );
        assertEquals("method:def:static boolean SELECT(int k,  Runnable r ) REFACTOR changeparam: int k,  Runnable r",
                new CsarQuery(method1, null, null, new RefactorElement.ChangeParameters(parameters1)));

        List<Identifier> parameters2 = new ArrayList<>();
        parameters2.add(new Identifier(null, "float"));
        parameters2.add(new Identifier(null, "String"));
        MethodLanguageElement method2 = new MethodLanguageElement(CsarQuery.Type.DEF, null,
                Optional.of(true), Optional.empty(), "add", "int", Optional.empty(), Optional.empty(), parameters2,
                null, null
        );
        assertEquals("method:def:static int add(float, String) REFACTOR changeparam:float,String",
                new CsarQuery(method2, null, null, new RefactorElement.ChangeParameters(parameters2)));
    }

    @Test
    public void testMethodQuery() {
        assertEquals("SELECT method:use:add", new CsarQuery(new MethodLanguageElement(CsarQuery.Type.USE, null,
                Optional.empty(), Optional.empty(), "add", null, Optional.empty(), Optional.empty(), null, null, null)));

        MethodLanguageElement method1 = new MethodLanguageElement(CsarQuery.Type.DEF, null,
                Optional.of(true), Optional.empty(), "$sqrt_0", "double", Optional.empty(), Optional.empty(), null,
                null, null
        );
        method1.addParameter(new Identifier("k", "int"));
        method1.addParameter(new Identifier("r", "Runnable"));
        assertEquals("method:def:static double $sqrt_0(int k,  Runnable r )", new CsarQuery(method1));

        MethodLanguageElement method2 = new MethodLanguageElement(CsarQuery.Type.DEF, null, Optional.empty(),
                Optional.empty(), "$", null, Optional.empty(), Optional.empty(), null, null, null);
        method2.addThrownException("IllegalArgumentException");
        method2.addSuperClass("Main");
        assertEquals("method:def:$ throws(IllegalArgumentException) super(Main)", new CsarQuery(method2));

        MethodLanguageElement method3 = new MethodLanguageElement(CsarQuery.Type.DEF, null,
                Optional.of(true), Optional.empty(), "add", "byte", Optional.empty(), Optional.of(2), null, null, null
        );
        assertEquals("method:def:static byte add(2)", new CsarQuery(method3));

        MethodLanguageElement method4 = new MethodLanguageElement(CsarQuery.Type.DEF, null,
                Optional.of(true), Optional.empty(), "add", "int", Optional.empty(), Optional.empty(), null, null, null
        );
        method4.addParameter(new Identifier(null, "float"));
        method4.addParameter(new Identifier(null, "String"));
        assertEquals("method:def:static int add(float, String)", new CsarQuery(method4));
    }

    @Test
    public void testClassQuery() {
        assertEquals("class:def:public static final MyClass",
                new CsarQuery(new ClassLanguageElement(CsarQuery.Type.DEF, VisibilityModifier.PUBLIC,
                        Optional.of(true), Optional.of(true), "MyClass", Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(), null)));

        ClassLanguageElement class1 = new ClassLanguageElement(CsarQuery.Type.DEF, null,
                Optional.empty(), Optional.empty(), "class12", Optional.of(true), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), null
        );
        class1.addSuperClass("Runnable");
        class1.addSuperClass("Printable");
        class1.addSuperClass("Searchable");
        assertEquals("class:def:interface class12(Runnable,Printable,Searchable)", new CsarQuery(class1));
    }

    @Test
    public void testVariableQuery() {
        assertEquals("local:def:x", new CsarQuery(new VariableLanguageElement(CsarQuery.Type.DEF,
                VariableLanguageElement.VariableType.LOCAL, Optional.empty(), "x", null)));
        assertEquals("local:use:String s", new CsarQuery(new VariableLanguageElement(CsarQuery.Type.USE,
                VariableLanguageElement.VariableType.LOCAL, Optional.empty(), "s", "String")));
        assertEquals("param:def:final int x", new CsarQuery(new VariableLanguageElement(CsarQuery.Type.DEF,
                VariableLanguageElement.VariableType.PARAM, Optional.of(true), "x", "int")));
        assertEquals("instance:use:private static final LOGGER", new CsarQuery(
                new VariableLanguageElement.InstanceVariableLanguageElement(CsarQuery.Type.USE,
                        VisibilityModifier.PRIVATE, Optional.of(true), Optional.of(true), "LOGGER", null)));
    }

    @Test
    public void testCommentQuery() {
        CommentLanguageElement slc1 = new CommentLanguageElement(CommentLanguageElement.CommentType.SINGLE,
                Optional.empty(), "TODO fix bug re public & private accessor");
        assertEquals("slc:'TODO fix bug re public & private accessor'", new CsarQuery(slc1));

        CommentLanguageElement slc2 = new CommentLanguageElement(CommentLanguageElement.CommentType.SINGLE,
                Optional.empty(), "TODO fix bug 're' public & private accessor");
        assertEquals("slc:'TODO fix bug 're' public & private accessor'", new CsarQuery(slc2));

        CommentLanguageElement mlc1 = new CommentLanguageElement(CommentLanguageElement.CommentType.MULTI,
                Optional.empty(), "This is my method.");
        assertEquals("mlc:'This is my method.'", new CsarQuery(mlc1));

        CommentLanguageElement mlc2 = new CommentLanguageElement(CommentLanguageElement.CommentType.MULTI,
                Optional.of(true), "This is my method.");
        assertEquals("mlc:javadoc:'This is my method.'", new CsarQuery(mlc2));
    }

    @Test
    public void testControlFlowQuery() {
        assertEquals("if", new CsarQuery(new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.IF, null)));
        assertEquals("if(a||b)", new CsarQuery(new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.IF, "a||b")));

        assertEquals("switch", new CsarQuery(new ControlFlowLanguageElement.NamedExprControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.SWITCH, null, null)));
        assertEquals("switch:int", new CsarQuery(new ControlFlowLanguageElement.NamedExprControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.SWITCH, "int", null)));
        assertEquals("switch(personName)", new CsarQuery(
                new ControlFlowLanguageElement.NamedExprControlFlowLanguageElement(
                        ControlFlowLanguageElement.ControlFlowType.SWITCH, null, "personName")));

        assertEquals("while", new CsarQuery(new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.WHILE, null)));
        assertEquals("while(a && b)", new CsarQuery(new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.WHILE, "a && b")));
        assertEquals("while((a && b) || isActive(5, k, \"'hey'\"))", new CsarQuery(
                new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                        ControlFlowLanguageElement.ControlFlowType.WHILE, "(a && b) || isActive(5, k, \"'hey'\")")));

        assertEquals("dowhile", new CsarQuery(new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.DOWHILE, null)));
        assertEquals("dowhile(iterator.hasNext())", new CsarQuery(
                new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                        ControlFlowLanguageElement.ControlFlowType.DOWHILE, "iterator.hasNext()")));

        assertEquals("for", new CsarQuery(
                new ControlFlowLanguageElement(ControlFlowLanguageElement.ControlFlowType.FOR)));

        assertEquals("foreach", new CsarQuery(new ControlFlowLanguageElement.NamedControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.FOREACH, null)));
        assertEquals("foreach:String", new CsarQuery(
                new ControlFlowLanguageElement.NamedControlFlowLanguageElement(
                        ControlFlowLanguageElement.ControlFlowType.FOREACH, "String")));

        assertEquals("ternary", new CsarQuery(new ControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.TERNARY)));

        assertEquals("synchronized", new CsarQuery(new ControlFlowLanguageElement.NamedExprControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.SYNCHRONIZED, null, null)));
        assertEquals("synchronized:Object", new CsarQuery(
                new ControlFlowLanguageElement.NamedExprControlFlowLanguageElement(
                        ControlFlowLanguageElement.ControlFlowType.SYNCHRONIZED, "Object", null)));
        assertEquals("synchronized(getLocks().get(0))", new CsarQuery(
                new ControlFlowLanguageElement.NamedExprControlFlowLanguageElement(
                        ControlFlowLanguageElement.ControlFlowType.SYNCHRONIZED, null, "getLocks().get(0)")));
    }

    @Test
    public void testRegexIdentifierNames() {
        CsarQuery methodUseQuery = new CsarQuery(new MethodLanguageElement(CsarQuery.Type.USE, null, Optional.empty(),
                Optional.empty(), null, null, Optional.empty(), Optional.empty(), null, null, null));

        ((IdentifiableLanguageElement)methodUseQuery.getSearchTarget()).setIdentifierName("*");
        assertEquals("SELECT method:use:*", methodUseQuery);

        ((IdentifiableLanguageElement)methodUseQuery.getSearchTarget()).setIdentifierName("check*");
        assertEquals("SELECT method:use:check*", methodUseQuery);

        ((IdentifiableLanguageElement)methodUseQuery.getSearchTarget()).setIdentifierName("check*");
        assertEquals("SELECT method:use:check*", methodUseQuery);

        ((IdentifiableLanguageElement)methodUseQuery.getSearchTarget()).setIdentifierName("ch_ck");
        assertEquals("SELECT method:use:ch_ck", methodUseQuery);
    }

    @Test
    public void testLexerRuleOverlapIdentifierNames() {
        CsarQuery methodUseQuery = new CsarQuery(new MethodLanguageElement(CsarQuery.Type.USE, null, Optional.empty(),
                Optional.empty(), null, null, Optional.empty(), Optional.empty(), null, null, null));

        ((IdentifiableLanguageElement)methodUseQuery.getSearchTarget()).setIdentifierName("FROM");
        assertEquals("SELECT method:use:FROM", methodUseQuery);

        ((IdentifiableLanguageElement)methodUseQuery.getSearchTarget()).setIdentifierName("renameIdentifier");
        assertEquals("SELECT method:use:renameIdentifier", methodUseQuery);

        ((IdentifiableLanguageElement)methodUseQuery.getSearchTarget()).setIdentifierName("rename");
        assertEquals("SELECT method:use:rename", methodUseQuery);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidClassDefinitionQuery() {
        parse("class:def:");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidForEachQuery() {
        parse("foreach:");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidSwitchQuery() {
        parse("switch(test(");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyQuery() {
        parse("");
    }
}
