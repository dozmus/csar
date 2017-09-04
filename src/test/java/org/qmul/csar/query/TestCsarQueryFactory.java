package org.qmul.csar.query;

import org.junit.Test;
import org.qmul.csar.query.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TestCsarQueryFactory {

    // TODO test char[] as identifiername somewhere

    private static CsarQuery parse(String query) {
        return CsarQueryFactory.parse(query);
    }

    private static void assertEquals(String query, CsarQuery expected) {
        CsarQuery actual = parse(query);
        org.junit.Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCsarQuery() {
        MethodLanguageElement method = new MethodLanguageElement.Builder(CsarQuery.Type.USE, "add").build();
        ContainsQuery containsQuery = new ContainsQuery.Builder()
                .addLogicalOperator(LogicalOperator.NOT)
                .addLanguageElement(new ClassLanguageElement.Builder(CsarQuery.Type.USE, "MyClass").build())
                .addLogicalOperator(LogicalOperator.OR)
                .addLanguageElement(new ClassLanguageElement.Builder(CsarQuery.Type.DEF, "SecondClass")
                        .inner(Optional.of(true))
                        .build())
                .build();
        CsarQuery expectedCsarQuery = new CsarQuery.Builder(method)
                .contains(containsQuery)
                .from("Helpers")
                .refactor(new RefactorElement.RenameRefactorElement("addInt"))
                .build();
        assertEquals("SELECT method:use:add CONTAINS not class:use:MyClass OR class:def:inner SecondClass "
                        + "FROM Helpers REFACTOR rename:addInt", expectedCsarQuery);
    }

    @Test
    public void testContainsQueryPart() {
        MethodLanguageElement method = new MethodLanguageElement.Builder(CsarQuery.Type.USE, "add").build();
        ContainsQuery containsQuery = new ContainsQuery.Builder()
                .addLogicalOperator(LogicalOperator.NOT)
                .addLanguageElement(new ClassLanguageElement.Builder(CsarQuery.Type.USE, "MyClass").build())
                .addLogicalOperator(LogicalOperator.OR)
                .addLanguageElement(new ClassLanguageElement.Builder(CsarQuery.Type.DEF, "SecondClass")
                        .inner(Optional.of(true))
                        .build())
                .build();
        assertEquals("SELECT method:use:add CONTAINS not class:use:MyClass OR class:def:inner SecondClass",
                new CsarQuery.Builder(method).contains(containsQuery).build());
    }

    @Test
    public void testFromQuery() {
        CsarQuery expectedCsarQuery = new CsarQuery.Builder(
                new MethodLanguageElement.Builder(CsarQuery.Type.USE, "_").build())
                .from("MyClass")
                .build();
        assertEquals("SELECT method:use:_ FROM MyClass", expectedCsarQuery);
    }

    @Test
    public void testRefactorQuery() {
        // Rename
        CsarQuery expectedCsarQuery = new CsarQuery.Builder(
                new MethodLanguageElement.Builder(CsarQuery.Type.USE, "add").build())
                .refactor(new RefactorElement.RenameRefactorElement("addInt"))
                .build();
        assertEquals("SELECT method:use:add REFACTOR rename:addInt", expectedCsarQuery);

        // Change parameters #1
        List<Identifier> parameters1 = new ArrayList<>();
        parameters1.add(new Identifier("k", "int"));
        parameters1.add(new Identifier("r", "Runnable"));
        MethodLanguageElement method1 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "SELECT")
                .staticModifier(Optional.of(true))
                .returnType("boolean")
                .parameters(new Identifier("k", "int"), new Identifier("t", "Thread"))
                .build();
        expectedCsarQuery = new CsarQuery.Builder(method1)
                .refactor(new RefactorElement.ChangeParametersRefactorElement(parameters1))
                .build();
        assertEquals("method:def:static boolean SELECT(int k,  Thread t ) REFACTOR changeparam: int k,  Runnable r",
                expectedCsarQuery);

        // Change parameters #2
        List<Identifier> parameters2 = new ArrayList<>();
        parameters2.add(new Identifier(null, "float"));
        parameters2.add(new Identifier(null, "String"));
        MethodLanguageElement method2 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "add")
                .staticModifier(Optional.of(true))
                .returnType("int")
                .parameters(new Identifier(null, "float"), new Identifier(null, "char"))
                .build();
        expectedCsarQuery = new CsarQuery.Builder(method2)
                .refactor(new RefactorElement.ChangeParametersRefactorElement(parameters2))
                .build();
        assertEquals("method:def:static int add(float, char) REFACTOR changeparam:float,String", expectedCsarQuery);
    }

    @Test
    public void testMethodQuery() {
        MethodLanguageElement method1 = new MethodLanguageElement.Builder(CsarQuery.Type.USE, "add").build();
        assertEquals("SELECT method:use:add", new CsarQuery(method1));

        MethodLanguageElement method2 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "$sqrt_0")
                .staticModifier(Optional.of(true))
                .returnType("double")
                .parameters(new Identifier("k", "int"), new Identifier("r", "Runnable"))
                .build();
        assertEquals("method:def:static double $sqrt_0(int k,  Runnable r )", new CsarQuery(method2));

        MethodLanguageElement method3 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "$")
                .thrownExceptions("IllegalArgumentException")
                .superClasses("Main")
                .build();
        assertEquals("method:def:$ throws(IllegalArgumentException) super(Main)", new CsarQuery(method3));

        MethodLanguageElement method4 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "add")
                .staticModifier(Optional.of(true))
                .returnType("byte")
                .parameterCount(Optional.of(2))
                .build();
        assertEquals("method:def:static byte add(2)", new CsarQuery(method4));

        MethodLanguageElement method5 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "add")
                .staticModifier(Optional.of(true))
                .returnType("int")
                .parameters(new Identifier(null, "float"), new Identifier(null, "String"))
                .build();
        assertEquals("method:def:static int add(float, String)", new CsarQuery(method5));
    }

    @Test
    public void testClassQuery() {
        ClassLanguageElement class1 = new ClassLanguageElement.Builder(CsarQuery.Type.DEF, "MyClass")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .staticModifier(Optional.of(true))
                .finalModifier(Optional.of(true))
                .build();
        assertEquals("class:def:public static final MyClass", new CsarQuery(class1));

        ClassLanguageElement class2 = new ClassLanguageElement.Builder(CsarQuery.Type.DEF, "class12")
                .interfaceModifier(Optional.of(true))
                .superClasses("Runnable", "Printable", "Searchable")
                .build();
        assertEquals("class:def:interface class12(Runnable,Printable,Searchable)", new CsarQuery(class2));
    }

    @Test
    public void testVariableQuery() {
        VariableLanguageElement variable1 = new VariableLanguageElement.Builder(CsarQuery.Type.DEF,
                VariableLanguageElement.VariableType.LOCAL, "x")
                .build();
        assertEquals("local:def:x", new CsarQuery(variable1));

        VariableLanguageElement variable2 = new VariableLanguageElement.Builder(CsarQuery.Type.USE,
                VariableLanguageElement.VariableType.LOCAL, "s")
                .identifierType("String")
                .build();
        assertEquals("local:use:String s", new CsarQuery(variable2));

        VariableLanguageElement variable3 = new VariableLanguageElement.Builder(CsarQuery.Type.DEF,
                VariableLanguageElement.VariableType.PARAM, "x")
                .identifierType("int")
                .finalModifier(Optional.of(true))
                .build();
        assertEquals("param:def:final int x", new CsarQuery(variable3));

        VariableLanguageElement.InstanceVariableLanguageElement variable4
                = new VariableLanguageElement.InstanceVariableLanguageElement.Builder(CsarQuery.Type.USE, "LOGGER")
                .visibilityModifier(VisibilityModifier.PRIVATE)
                .staticModifier(Optional.of(true))
                .finalModifier(Optional.of(true))
                .build();
        assertEquals("instance:use:private static final LOGGER", new CsarQuery(variable4));
    }

    @Test
    public void testCommentQuery() {
        CsarQuery expectedCsarQuery = new CsarQuery(
                new CommentLanguageElement.Builder(CommentLanguageElement.CommentType.SINGLE)
                        .content("TODO fix bug re public & private accessor")
                        .build());
        assertEquals("slc:'TODO fix bug re public & private accessor'", expectedCsarQuery);

        expectedCsarQuery = new CsarQuery(new CommentLanguageElement.Builder(CommentLanguageElement.CommentType.SINGLE)
                        .content("TODO fix bug 're' public & private accessor")
                        .build());
        assertEquals("slc:'TODO fix bug 're' public & private accessor'", expectedCsarQuery);

        expectedCsarQuery = new CsarQuery(new CommentLanguageElement.Builder(CommentLanguageElement.CommentType.MULTI)
                .content("Gets *")
                .build());
        assertEquals("mlc:'Gets *'", expectedCsarQuery);

        expectedCsarQuery = new CsarQuery(new CommentLanguageElement.Builder(CommentLanguageElement.CommentType.MULTI)
                .content("Gets the x coordinate of this Entity.")
                .javadoc(Optional.of(true))
                .build());
        assertEquals("mlc:javadoc:'Gets the x coordinate of this Entity.'", expectedCsarQuery);
    }

    @Test
    public void testControlFlowQuery() {
        // If statement
        CsarQuery expected
                = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.IF)
                .build());
        assertEquals("if", expected);

        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.IF)
                .expr("a||b")
                .build());
        assertEquals("if(a||b)", expected);

        // Switch statement
        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.SWITCH)
                        .build());
        assertEquals("switch", expected);

        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.SWITCH)
                        .identifierName("int")
                        .build());
        assertEquals("switch:int", expected);

        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.SWITCH)
                        .expr("personName")
                        .build());
        assertEquals("switch(personName)", expected);

        // While
        assertEquals("while", new CsarQuery(new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.WHILE, null)));
        assertEquals("while(a && b)", new CsarQuery(new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                ControlFlowLanguageElement.ControlFlowType.WHILE, "a && b")));
        assertEquals("while((a && b) || isActive(5, k, \"'hey'\"))", new CsarQuery(
                new ControlFlowLanguageElement.ExprControlFlowLanguageElement(
                        ControlFlowLanguageElement.ControlFlowType.WHILE, "(a && b) || isActive(5, k, \"'hey'\")")));

        // Do While
        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.DOWHILE).build());
        assertEquals("dowhile", expected);

        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.DOWHILE)
                        .expr("iterator.hasNext()")
                        .build());
        assertEquals("dowhile(iterator.hasNext())", expected);

        // For
        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.FOR)
                .build());
        assertEquals("for", expected);

        // Foreach
        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.FOREACH)
                        .build());
        assertEquals("foreach", expected);

        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.FOREACH)
                        .identifierName("String")
                        .build());
        assertEquals("foreach:String", expected);

        // Ternary
        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.TERNARY)
                        .build());
        assertEquals("ternary", expected);

        // Synchronized
        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.SYNCHRONIZED)
                        .build());
        assertEquals("synchronized", expected);

        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.SYNCHRONIZED)
                        .identifierName("Object")
                        .build());
        assertEquals("synchronized:Object", expected);

        expected = new CsarQuery(
                new ControlFlowLanguageElement.Builder(ControlFlowLanguageElement.ControlFlowType.SYNCHRONIZED)
                        .expr("getLocks().get(0)")
                        .build());
        assertEquals("synchronized(getLocks().get(0))", expected);
    }

    @Test
    public void testRegexIdentifierNames() {
        CsarQuery expectedCsarQuery = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "*").build());
        assertEquals("SELECT method:use:*", expectedCsarQuery);

        expectedCsarQuery = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "check*").build());
        assertEquals("SELECT method:use:check*", expectedCsarQuery);

        expectedCsarQuery = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "pre*_hook").build());
        assertEquals("SELECT method:use:pre*_hook", expectedCsarQuery);

        expectedCsarQuery = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "ch_ck").build());
        assertEquals("SELECT method:use:ch_ck", expectedCsarQuery);
    }

    @Test
    public void testLexerRuleOverlapIdentifierNames() {
        CsarQuery expectedCsarQuery = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "FROM")
                .build());
        assertEquals("SELECT method:use:FROM", expectedCsarQuery);

        expectedCsarQuery = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "renameIdentifier")
                .build());
        assertEquals("SELECT method:use:renameIdentifier", expectedCsarQuery);

        expectedCsarQuery = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "rename").build());
        assertEquals("SELECT method:use:rename", expectedCsarQuery);
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
