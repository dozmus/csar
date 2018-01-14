package org.qmul.csar.query;

import org.junit.Test;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.descriptor.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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
        MethodDescriptor desc = new MethodDescriptor.Builder("add").build();
        ContainsQuery containsQuery = new ContainsQuery.Builder()
                .addLogicalOperator(LogicalOperator.NOT)
                .addTargetDescriptor(new TargetDescriptor(SearchType.USE, new ClassDescriptor.Builder("MyClass")
                        .build()))
                .addLogicalOperator(LogicalOperator.OR)
                .addTargetDescriptor(new TargetDescriptor(SearchType.DEF, new ClassDescriptor.Builder("SecondClass")
                        .inner(true).build()))
                .build();
        CsarQuery expected = new CsarQuery.Builder(new TargetDescriptor(Optional.of(SearchType.USE), desc))
                .contains(containsQuery)
                .from("Helpers")
                .refactor(new RefactorDescriptor.Rename("addInt"))
                .build();
        assertEquals("SELECT method:use:add CONTAINS not class:use:MyClass OR class:def:inner SecondClass "
                + "FROM Helpers REFACTOR rename:addInt", expected);
    }

    @Test
    public void testContainsQueryPart1() {
        MethodDescriptor desc = new MethodDescriptor.Builder("add").build();
        ContainsQuery containsQuery = new ContainsQuery.Builder()
                .addLogicalOperator(LogicalOperator.NOT)
                .addTargetDescriptor(new TargetDescriptor(SearchType.USE, new ClassDescriptor.Builder("MyClass")
                        .build()))
                .addLogicalOperator(LogicalOperator.OR)
                .addTargetDescriptor(new TargetDescriptor(SearchType.DEF, new ClassDescriptor.Builder("SecondClass")
                        .inner(true).build()))
                .build();
        CsarQuery expected = new CsarQuery.Builder(new TargetDescriptor(Optional.of(SearchType.USE), desc))
                .contains(containsQuery)
                .build();
        assertEquals("SELECT method:use:add CONTAINS not class:use:MyClass OR class:def:inner SecondClass", expected);
    }

    @Test
    public void testContainsQueryPart2() {
        MethodDescriptor desc = new MethodDescriptor.Builder("add").build();
        ContainsQuery containsQuery = new ContainsQuery.Builder()
                .addTargetDescriptor(new TargetDescriptor(SearchType.USE, new MethodDescriptor.Builder("method")
                        .build()))
                .build();
        CsarQuery expected = new CsarQuery.Builder(new TargetDescriptor(Optional.of(SearchType.USE), desc))
                .contains(containsQuery)
                .build();
        assertEquals("SELECT method:use:add CONTAINS method:use:method", expected);
    }

    @Test
    public void testFromQuery() {
        CsarQuery expected = new CsarQuery.Builder(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder("_").build()))
                .from("MyClass")
                .build();
        assertEquals("SELECT method:use:_ FROM MyClass", expected);
    }

    @Test
    public void testRefactorQuery() {
        // Rename
        CsarQuery expected = new CsarQuery.Builder(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder("add").build()))
                .refactor(new RefactorDescriptor.Rename("addInt"))
                .build();
        assertEquals("SELECT method:use:add REFACTOR rename:addInt", expected);

        // Change parameters #1
        List<ParameterVariableDescriptor> parameters1 = new ArrayList<>();
        parameters1.add(new ParameterVariableDescriptor(Optional.of(new IdentifierName.Static("k")),
                Optional.of("int"), Optional.empty()));
        parameters1.add(new ParameterVariableDescriptor(Optional.of(new IdentifierName.Static("r")),
                Optional.of("Runnable"), Optional.empty()));

        MethodDescriptor method1 = new MethodDescriptor.Builder("SELECT")
                .staticModifier(true)
                .returnType("boolean")
                .parameters(Arrays.asList(
                        new ParameterVariableDescriptor(Optional.of(new IdentifierName.Static("k")),
                                Optional.of("int"), Optional.empty()),
                        new ParameterVariableDescriptor(Optional.of(new IdentifierName.Static("t")),
                                Optional.of("Thread"), Optional.empty()))
                )
                .hasParameters(true)
                .build();
        expected = new CsarQuery.Builder(new TargetDescriptor(Optional.of(SearchType.DEF), method1))
                .refactor(new RefactorDescriptor.ChangeParameters(parameters1))
                .build();
        assertEquals("method:def:static boolean SELECT(int k,  Thread t ) REFACTOR changeparam: int k,  Runnable r",
                expected);

        // Change parameters #2
        List<ParameterVariableDescriptor> parameters2 = new ArrayList<>();
        parameters2.add(new ParameterVariableDescriptor(Optional.empty(), Optional.of("float"), Optional.empty()));
        parameters2.add(new ParameterVariableDescriptor(Optional.empty(), Optional.of("String"), Optional.empty()));

        MethodDescriptor method2 = new MethodDescriptor.Builder("add")
                .staticModifier(true)
                .returnType("int")
                .parameters(Arrays.asList(
                        new ParameterVariableDescriptor(Optional.empty(), Optional.of("float"), Optional.empty()),
                        new ParameterVariableDescriptor(Optional.empty(), Optional.of("char"), Optional.empty()))
                )
                .hasParameters(true)
                .build();
        expected = new CsarQuery.Builder(new TargetDescriptor(Optional.of(SearchType.DEF), method2))
                .refactor(new RefactorDescriptor.ChangeParameters(parameters2))
                .build();
        assertEquals("method:def:static int add(float, char) REFACTOR changeparam:float,String", expected);
    }

    @Test
    public void testMethodQuery() {
        MethodDescriptor desc1 = new MethodDescriptor.Builder("add").build();
        assertEquals("SELECT method:use:add", new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE), desc1)));

        MethodDescriptor desc2 = new MethodDescriptor.Builder("$sqrt_0")
                .staticModifier(true)
                .returnType("double")
                .parameters(Arrays.asList(
                        new ParameterVariableDescriptor(Optional.of(new IdentifierName.Static("k")),
                                Optional.of("int"), Optional.empty()),
                        new ParameterVariableDescriptor(Optional.of(new IdentifierName.Static("r")),
                                Optional.of("Runnable"), Optional.empty()))
                )
                .hasParameters(true)
                .build();
        assertEquals("method:def:static double $sqrt_0(int k,  Runnable r )",
                new CsarQuery(new TargetDescriptor(Optional.of(SearchType.DEF), desc2)));

        MethodDescriptor desc3 = new MethodDescriptor.Builder("add")
                .staticModifier(true)
                .returnType("byte")
                .parameterCount(2)
                .hasParameters(true)
                .build();
        assertEquals("method:def:static byte add(2)",
                new CsarQuery(new TargetDescriptor(Optional.of(SearchType.DEF), desc3)));

        MethodDescriptor desc4 = new MethodDescriptor.Builder("add")
                .staticModifier(true)
                .returnType("int")
                .parameters(Arrays.asList(
                        new ParameterVariableDescriptor(Optional.empty(), Optional.of("float[]"), Optional.of(true)),
                        new ParameterVariableDescriptor(Optional.empty(), Optional.of("String"), Optional.empty()))
                )
                .hasParameters(true)
                .build();
        assertEquals("method:def:static int add(final float[], String)",
                new CsarQuery(new TargetDescriptor(Optional.of(SearchType.DEF), desc4)));
    }

    @Test
    public void testClassQuery() {
        ClassDescriptor desc1 = new ClassDescriptor.Builder("MyClass")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .staticModifier(true)
                .finalModifier(true)
                .build();
        assertEquals("class:def:public static final MyClass",
                new CsarQuery(new TargetDescriptor(Optional.of(SearchType.DEF), desc1)));

        ClassDescriptor desc2 = new ClassDescriptor.Builder("class12")
                .interfaceModifier(true)
                .implementedInterfaces(Arrays.asList("Runnable", "Printable", "Searchable"))
                .build();
        assertEquals("class:def:interface class12(Runnable,Printable,Searchable)",
                new CsarQuery(new TargetDescriptor(Optional.of(SearchType.DEF), desc2)));
    }

    @Test
    public void testVariableQuery() {
        LocalVariableDescriptor desc1 = new LocalVariableDescriptor.Builder("x").build();
        assertEquals("local:def:x", new CsarQuery(new TargetDescriptor(Optional.of(SearchType.DEF), desc1)));

        LocalVariableDescriptor desc2 = new LocalVariableDescriptor.Builder("s").identifierType("String[]").build();
        assertEquals("local:use:String[] s", new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE), desc2)));

        ParameterVariableDescriptor desc3 = new ParameterVariableDescriptor.Builder()
                .identifierName("x")
                .identifierType("int[][]")
                .finalModifier(true)
                .build();
        assertEquals("param:def:final int[][] x",
                new CsarQuery(new TargetDescriptor(Optional.of(SearchType.DEF), desc3)));

        InstanceVariableDescriptor desc4 = new InstanceVariableDescriptor.Builder("LOGGER")
                .visibilityModifier(VisibilityModifier.PRIVATE)
                .staticModifier(true)
                .finalModifier(true)
                .build();
        assertEquals("instance:use:private static final LOGGER",
                new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE), desc4)));
    }

    @Test
    public void testLineCommentQuery() {
        Descriptor desc = new LineCommentDescriptor(Optional.empty());
        assertEquals("slc", new CsarQuery(new TargetDescriptor(desc)));

        desc = new LineCommentDescriptor(Optional.of("TODO fix bug re public & private accessor"));
        assertEquals("slc:'TODO fix bug re public & private accessor'", new CsarQuery(new TargetDescriptor(desc)));

        desc = new LineCommentDescriptor(Optional.of("TODO fix bug 're' public & private accessor"));
        assertEquals("slc:'TODO fix bug 're' public & private accessor'", new CsarQuery(new TargetDescriptor(desc)));
    }

    @Test
    public void testBlockCommentQuery() {
        Descriptor desc = new BlockCommentDescriptor(Optional.empty(), Optional.empty());
        assertEquals("mlc", new CsarQuery(new TargetDescriptor(desc)));

        desc = new BlockCommentDescriptor(Optional.of("Gets *"), Optional.empty());
        assertEquals("mlc:'Gets *'", new CsarQuery(new TargetDescriptor(desc)));

        desc = new BlockCommentDescriptor(Optional.of("Gets the x coordinate of this Entity."), Optional.of(true));
        assertEquals("mlc:javadoc:'Gets the x coordinate of this Entity.'", new CsarQuery(new TargetDescriptor(desc)));

        desc = new BlockCommentDescriptor(Optional.empty(), Optional.of(true));
        assertEquals("mlc:javadoc", new CsarQuery(new TargetDescriptor(desc)));
    }

    @Test
    public void testConditionalQuery() {
        // If statement
        Descriptor desc = new ConditionalDescriptor(ConditionalDescriptor.Type.IF);
        assertEquals("if", new CsarQuery(new TargetDescriptor(desc)));

        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.IF, Optional.empty(), Optional.of("a||b"));
        assertEquals("if(a||b)", new CsarQuery(new TargetDescriptor(desc)));

        // Switch statement
        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.SWITCH);
        assertEquals("switch", new CsarQuery(new TargetDescriptor(desc)));

        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.SWITCH,
                Optional.of(new IdentifierName.Static("int")), Optional.empty());
        assertEquals("switch:int", new CsarQuery(new TargetDescriptor(desc)));

        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.SWITCH, Optional.empty(),
                Optional.of("personName"));
        assertEquals("switch(personName)", new CsarQuery(new TargetDescriptor(desc)));

        // While
        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.WHILE);
        assertEquals("while", new CsarQuery(new TargetDescriptor(desc)));

        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.WHILE, Optional.empty(), Optional.of("a && b"));
        assertEquals("while(a && b)", new CsarQuery(new TargetDescriptor(desc)));

        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.WHILE, Optional.empty(),
                Optional.of("(a && b) || isActive(5, k, \"'hey'\")"));
        assertEquals("while((a && b) || isActive(5, k, \"'hey'\"))", new CsarQuery(new TargetDescriptor(desc)));

        // Do While
        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.DO_WHILE);
        assertEquals("dowhile", new CsarQuery(new TargetDescriptor(desc)));

        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.DO_WHILE, Optional.empty(),
                Optional.of("iterator.hasNext()"));
        assertEquals("dowhile(iterator.hasNext())", new CsarQuery(new TargetDescriptor(desc)));

        // For
        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.FOR);
        assertEquals("for", new CsarQuery(new TargetDescriptor(desc)));

        // Foreach
        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.FOR_EACH);
        assertEquals("foreach", new CsarQuery(new TargetDescriptor(desc)));

        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.FOR_EACH,
                Optional.of(new IdentifierName.Static("String")), Optional.empty());
        assertEquals("foreach:String", new CsarQuery(new TargetDescriptor(desc)));

        // Ternary
        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.TERNARY);
        assertEquals("ternary", new CsarQuery(new TargetDescriptor(desc)));

        // Synchronized
        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.SYNCHRONIZED);
        assertEquals("synchronized", new CsarQuery(new TargetDescriptor(desc)));

        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.SYNCHRONIZED,
                Optional.of(new IdentifierName.Static("Object")), Optional.empty());
        assertEquals("synchronized:Object", new CsarQuery(new TargetDescriptor(desc)));

        desc = new ConditionalDescriptor(ConditionalDescriptor.Type.SYNCHRONIZED, Optional.empty(),
                Optional.of("getLocks().get(0)"));
        assertEquals("synchronized(getLocks().get(0))", new CsarQuery(new TargetDescriptor(desc)));
    }

    @Test
    public void testRegexIdentifierNames() {
        CsarQuery expected = new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder(new IdentifierName.Regex(Pattern.compile(".*"))).build()));
        assertEquals("SELECT method:use:REGEX(.*)", expected);

        expected = new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder(new IdentifierName.Regex(Pattern.compile("check.*"))).build()));
        assertEquals("SELECT method:use:REGEXP(check.*)", expected);

        expected = new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder(new IdentifierName.Regex(Pattern.compile("pre.*_hook"))).build()));
        assertEquals("SELECT method:use:regex(pre.*_hook)", expected);

        expected = new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder(new IdentifierName.Regex(Pattern.compile("ch.ck"))).build()));
        assertEquals("SELECT method:use:regexp(ch.ck)", expected);

        expected = new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder(new IdentifierName.Regex(Pattern.compile("\\\\add"))).build()));
        assertEquals("SELECT method:use:regexp(\\\\add)", expected);

        expected = new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder(new IdentifierName.Regex(Pattern.compile("[Aa]dd"))).build()));
        assertEquals("SELECT method:use:regexp([Aa]dd)", expected);
    }

    @Test
    public void testLexerRuleOverlappingIdentifierNames() {
        CsarQuery expected = new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder("FROM")
                        .build()));
        assertEquals("SELECT method:use:FROM", expected);

        expected = new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder("renameIdentifier")
                        .build()));
        assertEquals("SELECT method:use:renameIdentifier", expected);

        expected = new CsarQuery(new TargetDescriptor(Optional.of(SearchType.USE),
                new MethodDescriptor.Builder("rename").build()));
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
