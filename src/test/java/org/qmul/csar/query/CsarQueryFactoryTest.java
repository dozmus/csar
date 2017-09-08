package org.qmul.csar.query;

import org.junit.Test;
import org.qmul.csar.lang.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CsarQueryFactoryTest {

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
                        .inner(true)
                        .build())
                .build();
        CsarQuery expected = new CsarQuery.Builder(method)
                .contains(containsQuery)
                .from("Helpers")
                .refactor(new RefactorElement.RenameRefactorElement("addInt"))
                .build();
        assertEquals("SELECT method:use:add CONTAINS not class:use:MyClass OR class:def:inner SecondClass "
                        + "FROM Helpers REFACTOR rename:addInt", expected);
    }

    @Test
    public void testContainsQueryPart() {
        MethodLanguageElement method = new MethodLanguageElement.Builder(CsarQuery.Type.USE, "add").build();
        ContainsQuery containsQuery = new ContainsQuery.Builder()
                .addLogicalOperator(LogicalOperator.NOT)
                .addLanguageElement(new ClassLanguageElement.Builder(CsarQuery.Type.USE, "MyClass").build())
                .addLogicalOperator(LogicalOperator.OR)
                .addLanguageElement(new ClassLanguageElement.Builder(CsarQuery.Type.DEF, "SecondClass")
                        .inner(true)
                        .build())
                .build();
        CsarQuery expected = new CsarQuery.Builder(method).contains(containsQuery).build();
        assertEquals("SELECT method:use:add CONTAINS not class:use:MyClass OR class:def:inner SecondClass", expected);
    }

    @Test
    public void testFromQuery() {
        CsarQuery expected = new CsarQuery.Builder(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "_").build())
                .from("MyClass")
                .build();
        assertEquals("SELECT method:use:_ FROM MyClass", expected);
    }

    @Test
    public void testRefactorQuery() {
        // Rename
        CsarQuery expected = new CsarQuery.Builder(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "add").build())
                .refactor(new RefactorElement.RenameRefactorElement("addInt"))
                .build();
        assertEquals("SELECT method:use:add REFACTOR rename:addInt", expected);

        // Change parameters #1
        List<Parameter> parameters1 = new ArrayList<>();
        parameters1.add(new Parameter("int", Optional.of("k")));
        parameters1.add(new Parameter("Runnable", Optional.of("r")));
        MethodLanguageElement method1 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "SELECT")
                .staticModifier(true)
                .returnType("boolean")
                .parameters(new Parameter("int", Optional.of("k")), new Parameter("Thread", Optional.of("t")))
                .build();
        expected = new CsarQuery.Builder(method1)
                .refactor(new RefactorElement.ChangeParametersRefactorElement(parameters1))
                .build();
        assertEquals("method:def:static boolean SELECT(int k,  Thread t ) REFACTOR changeparam: int k,  Runnable r",
                expected);

        // Change parameters #2
        List<Parameter> parameters2 = new ArrayList<>();
        parameters2.add(new Parameter("float", Optional.empty()));
        parameters2.add(new Parameter("String", Optional.empty()));
        MethodLanguageElement method2 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "add")
                .staticModifier(true)
                .returnType("int")
                .parameters(new Parameter("float", Optional.empty()), new Parameter("char", Optional.empty()))
                .build();
        expected = new CsarQuery.Builder(method2)
                .refactor(new RefactorElement.ChangeParametersRefactorElement(parameters2))
                .build();
        assertEquals("method:def:static int add(float, char) REFACTOR changeparam:float,String", expected);
    }

    @Test
    public void testMethodQuery() {
        MethodLanguageElement method1 = new MethodLanguageElement.Builder(CsarQuery.Type.USE, "add").build();
        assertEquals("SELECT method:use:add", new CsarQuery(method1));

        MethodLanguageElement method2 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "$sqrt_0")
                .staticModifier(true)
                .returnType("double")
                .parameters(new Parameter("int", Optional.of("k")), new Parameter("Runnable", Optional.of("r")))
                .build();
        assertEquals("method:def:static double $sqrt_0(int k,  Runnable r )", new CsarQuery(method2));

        MethodLanguageElement method3 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "$")
                .thrownExceptions("IllegalArgumentException")
                .superClasses("Main")
                .build();
        assertEquals("method:def:$ throws(IllegalArgumentException) super(Main)", new CsarQuery(method3));

        MethodLanguageElement method4 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "add")
                .staticModifier(true)
                .returnType("byte")
                .parameterCount(2)
                .build();
        assertEquals("method:def:static byte add(2)", new CsarQuery(method4));

        MethodLanguageElement method5 = new MethodLanguageElement.Builder(CsarQuery.Type.DEF, "add")
                .staticModifier(true)
                .returnType("int")
                .parameters(new Parameter("float[]", Optional.empty(), Optional.of(true)),
                        new Parameter("String", Optional.empty()))
                .build();
        assertEquals("method:def:static int add(final float[], String)", new CsarQuery(method5));
    }

    @Test
    public void testClassQuery() {
        ClassLanguageElement class1 = new ClassLanguageElement.Builder(CsarQuery.Type.DEF, "MyClass")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .staticModifier(true)
                .finalModifier(true)
                .build();
        assertEquals("class:def:public static final MyClass", new CsarQuery(class1));

        ClassLanguageElement class2 = new ClassLanguageElement.Builder(CsarQuery.Type.DEF, "class12")
                .interfaceModifier(true)
                .superClasses("Runnable", "Printable", "Searchable")
                .build();
        assertEquals("class:def:interface class12(Runnable,Printable,Searchable)", new CsarQuery(class2));
    }

    @Test
    public void testVariableQuery() {
        VariableLanguageElement variable1 = new VariableLanguageElement.Builder(CsarQuery.Type.DEF, VariableType.LOCAL,
                "x")
                .build();
        assertEquals("local:def:x", new CsarQuery(variable1));

        VariableLanguageElement variable2 = new VariableLanguageElement.Builder(CsarQuery.Type.USE, VariableType.LOCAL,
                "s")
                .identifierType("String[]")
                .build();
        assertEquals("local:use:String[] s", new CsarQuery(variable2));

        VariableLanguageElement variable3 = new VariableLanguageElement.Builder(CsarQuery.Type.DEF, VariableType.PARAM,
                "x")
                .identifierType("int[][]")
                .finalModifier(true)
                .build();
        assertEquals("param:def:final int[][] x", new CsarQuery(variable3));

        VariableLanguageElement.InstanceVariableLanguageElement variable4
                = new VariableLanguageElement.InstanceVariableLanguageElement.Builder(CsarQuery.Type.USE, "LOGGER")
                .visibilityModifier(VisibilityModifier.PRIVATE)
                .staticModifier(true)
                .finalModifier(true)
                .build();
        assertEquals("instance:use:private static final LOGGER", new CsarQuery(variable4));
    }

    @Test
    public void testCommentQuery() {
        CsarQuery expected = new CsarQuery(new CommentLanguageElement.Builder(CommentType.SINGLE)
                .content("TODO fix bug re public & private accessor")
                .build());
        assertEquals("slc:'TODO fix bug re public & private accessor'", expected);

        expected = new CsarQuery(new CommentLanguageElement.Builder(CommentType.SINGLE)
                .content("TODO fix bug 're' public & private accessor")
                .build());
        assertEquals("slc:'TODO fix bug 're' public & private accessor'", expected);

        expected = new CsarQuery(new CommentLanguageElement.Builder(CommentType.MULTI)
                .content("Gets *")
                .build());
        assertEquals("mlc:'Gets *'", expected);

        expected = new CsarQuery(new CommentLanguageElement.Builder(CommentType.MULTI)
                .content("Gets the x coordinate of this Entity.")
                .javadoc(true)
                .build());
        assertEquals("mlc:javadoc:'Gets the x coordinate of this Entity.'", expected);

        expected = new CsarQuery(new CommentLanguageElement.Builder(CommentType.MULTI)
                .javadoc(true)
                .build());
        assertEquals("mlc:javadoc", expected);
    }

    @Test
    public void testControlFlowQuery() {
        // If statement
        CsarQuery expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.IF).build());
        assertEquals("if", expected);

        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.IF)
                .expr("a||b")
                .build());
        assertEquals("if(a||b)", expected);

        // Switch statement
        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.SWITCH).build());
        assertEquals("switch", expected);

        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.SWITCH)
                .identifierName("int")
                .build());
        assertEquals("switch:int", expected);

        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.SWITCH)
                .expr("personName")
                .build());
        assertEquals("switch(personName)", expected);

        // While
        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.WHILE).build());
        assertEquals("while", expected);

        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.WHILE)
                .expr("a && b")
                .build());
        assertEquals("while(a && b)", expected);

        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.WHILE)
                .expr("(a && b) || isActive(5, k, \"'hey'\")")
                .build());
        assertEquals("while((a && b) || isActive(5, k, \"'hey'\"))", expected);

        // Do While
        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.DO_WHILE).build());
        assertEquals("dowhile", expected);

        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.DO_WHILE)
                .expr("iterator.hasNext()")
                .build());
        assertEquals("dowhile(iterator.hasNext())", expected);

        // For
        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.FOR).build());
        assertEquals("for", expected);

        // Foreach
        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.FOREACH).build());
        assertEquals("foreach", expected);

        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.FOREACH)
                .identifierName("String")
                .build());
        assertEquals("foreach:String", expected);

        // Ternary
        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.TERNARY).build());
        assertEquals("ternary", expected);

        // Synchronized
        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.SYNCHRONIZED).build());
        assertEquals("synchronized", expected);

        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.SYNCHRONIZED)
                .identifierName("Object")
                .build());
        assertEquals("synchronized:Object", expected);

        expected = new CsarQuery(new ControlFlowLanguageElement.Builder(ControlFlowType.SYNCHRONIZED)
                .expr("getLocks().get(0)")
                .build());
        assertEquals("synchronized(getLocks().get(0))", expected);
    }

    @Test
    public void testRegexIdentifierNames() {
        CsarQuery expected = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "*").build());
        assertEquals("SELECT method:use:*", expected);

        expected = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "check*").build());
        assertEquals("SELECT method:use:check*", expected);

        expected = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "pre*_hook").build());
        assertEquals("SELECT method:use:pre*_hook", expected);

        expected = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "ch_ck").build());
        assertEquals("SELECT method:use:ch_ck", expected);
    }

    @Test
    public void testLexerRuleOverlapIdentifierNames() {
        CsarQuery expected = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "FROM").build());
        assertEquals("SELECT method:use:FROM", expected);

        expected = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "renameIdentifier").build());
        assertEquals("SELECT method:use:renameIdentifier", expected);

        expected = new CsarQuery(new MethodLanguageElement.Builder(CsarQuery.Type.USE, "rename").build());
        assertEquals("SELECT method:use:rename", expected);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidParamVariableIdentifierName() {
        parse("param:def:final x[]");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidClassDefinitionQuery() {
        parse("class:def:");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidClassNameQuery() {
        parse("class:def:Element[]");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidMethodNameQuery() {
        parse("method:def:element[]");
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
