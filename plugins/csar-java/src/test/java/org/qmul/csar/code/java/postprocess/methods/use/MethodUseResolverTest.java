package org.qmul.csar.code.java.postprocess.methods.use;

import org.junit.BeforeClass;
import org.junit.Test;
import org.qmul.csar.code.CodeBase;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.parse.expression.*;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.methods.MethodTestUtils;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.DefaultTypeHierarchyResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.io.it.ProjectIteratorFactory;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.descriptors.ClassDescriptor;
import org.qmul.csar.util.FilePosition;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MethodUseResolverTest {

    private static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/postprocess/";
    private static CodeBase code;

    @BeforeClass
    public static void setUp() throws IllegalAccessException, InstantiationException {
        // Parse sample directory
        CodeParserFactory factory = new CodeParserFactory(JavaCodeParser.class);
        Iterator<Path> it = ProjectIteratorFactory.createFiltered(Paths.get(SAMPLES_DIRECTORY), false, factory);
        DefaultProjectCodeParser parser = new DefaultProjectCodeParser(factory, it);
        code = parser.results();

        // Pre-requisites
        TypeHierarchyResolver typeHierarchyResolver = new DefaultTypeHierarchyResolver();
        typeHierarchyResolver.postprocess(code);

        MethodQualifiedTypeResolver methodQualifiedTypeResolver = new MethodQualifiedTypeResolver();
        methodQualifiedTypeResolver.postprocess(code);

        MethodCallTypeInstanceResolver processor = new MethodCallTypeInstanceResolver(typeHierarchyResolver);
        processor.postprocess(code);

        // Resolve method usages
        MethodUseResolver resolver = new MethodUseResolver(typeHierarchyResolver);
        resolver.postprocess(code);
    }

    /**
     * Attempts to find the method in the argument path with the argument signature, and asserts that the method is not
     * null and that its method called contains the argument expected.
     */
    private static void assertContainsMethodCall(String fileName, String signature, MethodCallExpression expected) {
        MethodStatement methodStatement = MethodTestUtils.findMethod(code, SAMPLES_DIRECTORY, fileName, signature);
        assertNotNull(methodStatement);
        List<MethodCallExpression> calls = methodStatement.getMethodUsages();
        assertTrue(calls.contains(expected));
    }

    @Test
    public void testSameClassInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), args, path,
                new FilePosition(10, 167), new FilePosition(10, 170), new FilePosition(10, 175),
                Arrays.asList(new FilePosition(10, 172)));

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testSameClassRecursiveInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                identifier("testSameClassRecursiveInstanceMethodCall"), new ArrayList<>(), path,
                new FilePosition(16, 297), new FilePosition(16, 337), new FilePosition(16, 338),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("A.java", "void testSameClassRecursiveInstanceMethodCall()", expectedMethodCall);
    }

    @Test
    public void testSameClassStaticMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("staticAdd"), new ArrayList<>(),
                path, new FilePosition(20, 411), new FilePosition(20, 420), new FilePosition(20, 421),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("A.java", "void staticAdd()", expectedMethodCall);
    }

    @Test
    public void testSuperClassInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "T.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), args, path,
                new FilePosition(6, 109), new FilePosition(6, 112), new FilePosition(6, 117),
                Arrays.asList(new FilePosition(6, 114)));

        // Assert
        assertContainsMethodCall("U.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testSuperClassStaticMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "T.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("staticAdd"), new ArrayList<>(),
                path, new FilePosition(10, 190), new FilePosition(10, 199), new FilePosition(10, 200),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("U.java", "void staticAdd()", expectedMethodCall);
    }

    @Test
    public void testLocalVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "V.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("add2", identifierDotIdentifier("u", "add2"),
                args, path, new FilePosition(7, 124), new FilePosition(7, 128), new FilePosition(7, 133),
                Arrays.asList(new FilePosition(7, 130)));

        // Assert
        assertContainsMethodCall("U.java", "int add2(int,int)", expectedMethodCall);
    }

    @Test
    public void testParameterVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "V.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("add3", identifierDotIdentifier("u", "add3"),
                args, path, new FilePosition(11, 219), new FilePosition(11, 223), new FilePosition(11, 228),
                Arrays.asList(new FilePosition(11, 225)));

        // Assert
        assertContainsMethodCall("U.java", "int add3(int,int)", expectedMethodCall);
    }

    @Test
    public void testInstanceVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "W.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("add4", identifierDotIdentifier("u", "add4"),
                args, path, new FilePosition(8, 119), new FilePosition(8, 123), new FilePosition(8, 128),
                Arrays.asList(new FilePosition(8, 125)));

        // Assert
        assertContainsMethodCall("U.java", "int add4(int,int)", expectedMethodCall);
    }

    @Test
    public void testSuperMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("add5"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("add5", call, args, path,
                new FilePosition(6, 102), new FilePosition(6, 106), new FilePosition(6, 111),
                Arrays.asList(new FilePosition(6, 108)));

        // Assert
        assertContainsMethodCall("U.java", "int add5(int,int)", expectedMethodCall);
    }

    @Test
    public void testThisMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("add6"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("add6", call, args, path,
                new FilePosition(10, 177), new FilePosition(10, 181), new FilePosition(10, 186),
                Arrays.asList(new FilePosition(10, 183)));

        // Assert
        assertContainsMethodCall("U.java", "int add6(int,int)", expectedMethodCall);
    }

    @Test
    public void testSuperVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression("test1",
                new BinaryExpression(new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("z")),
                        BinaryOperation.DOT, identifier("test1")), new ArrayList<>(), path,
                new FilePosition(14, 264), new FilePosition(14, 269), new FilePosition(14, 270),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test1()", expectedMethodCall);
    }

    @Test
    public void testThisVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression("test2",
                new BinaryExpression(new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("z")),
                        BinaryOperation.DOT, identifier("test2")), new ArrayList<>(), path,
                new FilePosition(18, 346), new FilePosition(18, 351), new FilePosition(18, 352),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test2()", expectedMethodCall);
    }

    @Test
    public void testMethodCallOnMethodCall() {
        // Prepare
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        MethodCallExpression lhMethodCall = new MethodCallExpression(identifier("otherAdd1"), new ArrayList<>(), path,
                new FilePosition(10, 152), new FilePosition(10, 161), new FilePosition(10, 162),
                Collections.emptyList());
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(lhMethodCall, BinaryOperation.DOT, identifier("add7"));

        // Prepare for the prior method call
        MethodCallExpression expectedMethodCall = lhMethodCall;

        // Assert
        assertContainsMethodCall("Z.java", "U otherAdd1()", expectedMethodCall);

        // Prepare for the latter method call
        expectedMethodCall = new MethodCallExpression("add7", call, args, path, new FilePosition(10, 164),
                new FilePosition(10, 168), new FilePosition(10, 173),
                Arrays.asList(new FilePosition(10, 170)));

        // Assert
        assertContainsMethodCall("U.java", "int add7(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithSubtypeArgumentValue1() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A0.java");
        List<Expression> args = Arrays.asList(identifier("b"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("method2",
                new BinaryExpression(identifier("c"), BinaryOperation.DOT, identifier("method2")), args, path,
                new FilePosition(8, 164), new FilePosition(8, 171), new FilePosition(8, 173),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Class1.java", "void method2(A)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithSubtypeArgumentValue2() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A0.java");
        List<Expression> args = Arrays.asList(new MethodCallExpression(identifier("getB"), new ArrayList<>(), path,
                new FilePosition(13, 299), new FilePosition(13, 303), new FilePosition(13, 304),
                Collections.emptyList()));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("method2",
                new BinaryExpression(identifier("c"), BinaryOperation.DOT, identifier("method2")), args, path,
                new FilePosition(13, 291), new FilePosition(13, 298 ), new FilePosition(13, 305),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Class1.java", "void method2(A)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithSubtypeArgumentValue3() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A0.java");
        List<Expression> args = Arrays.asList(
                new MethodCallExpression("getB", new BinaryExpression(thisKeyword(), BinaryOperation.DOT,
                        identifier("getB")), new ArrayList<>(), path, new FilePosition(18, 436),
                        new FilePosition(18, 440), new FilePosition(18, 441), Collections.emptyList())
        );
        MethodCallExpression expectedMethodCall = new MethodCallExpression("method2",
                new BinaryExpression(identifier("c"), BinaryOperation.DOT, identifier("method2")),
                args, path, new FilePosition(18, 423), new FilePosition(18, 430), new FilePosition(18, 442),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Class1.java", "void method2(A)", expectedMethodCall);
    }

    @Test
    public void testStaticMethodCallOnClassName() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression("testLocalVariableMethodCall",
                new BinaryExpression(identifier("V"), BinaryOperation.DOT, identifier("testLocalVariableMethodCall")),
                new ArrayList<>(), path, new FilePosition(6, 101), new FilePosition(6, 128),
                new FilePosition(6, 129), Collections.emptyList());

        // Assert
        assertContainsMethodCall("V.java", "void testLocalVariableMethodCall()", expectedMethodCall);
    }

    @Test
    public void testStaticMethodCallOnFullyQualifiedName() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression("testLocalVariableMethodCall",
                new BinaryExpression(new BinaryExpression(identifier("base"), BinaryOperation.DOT, identifier("V")),
                        BinaryOperation.DOT, identifier("testLocalVariableMethodCall")),
                new ArrayList<>(), path, new FilePosition(10, 219), new FilePosition(10, 246),
                new FilePosition(10, 247), Collections.emptyList());

        // Assert
        assertContainsMethodCall("V.java", "void testLocalVariableMethodCall()", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithNestedParameterArguments() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        List<Expression> args = Arrays.asList(identifier("a"), identifier("b"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("add",
                new BinaryExpression(identifier("adder"), BinaryOperation.DOT, identifier("add")), args, path,
                new FilePosition(18, 455), new FilePosition(18, 458), new FilePosition(18, 463),
                Arrays.asList(new FilePosition(18, 460)));

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithNewInstantiation() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        Expression classDef = new InstantiateClassExpression(ClassDescriptor.Builder.allFalse("A")
                .local(true)
                .build(),
                Optional.empty(), new ArrayList<>(), new ArrayList<>(), false);
        MethodCallExpression expectedMethodCall = new MethodCallExpression("add",
                new BinaryExpression(classDef, BinaryOperation.DOT, identifier("add")), args, path,
                new FilePosition(24, 574), new FilePosition(24, 577), new FilePosition(24, 582),
                Arrays.asList(new FilePosition(24, 579)));

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithLocalVariableArgument() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        List<Expression> args = Arrays.asList(identifier("a"), identifier("d"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("add",
                new BinaryExpression(identifier("adder"), BinaryOperation.DOT, identifier("add")), args, path,
                new FilePosition(33, 795), new FilePosition(33, 798), new FilePosition(33, 803),
                Arrays.asList(new FilePosition(33, 800)));

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithLocalVariableArgumentFromForEachLoop() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        List<Expression> args = Arrays.asList(identifier("i"), literal("5"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("add",
                new BinaryExpression(identifier("adder"), BinaryOperation.DOT, identifier("add")), args, path,
                new FilePosition(42, 1000), new FilePosition(42, 1003), new FilePosition(42, 1008),
                Arrays.asList(new FilePosition(42, 1005)));

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallOnParentInstanceInInnerClass() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("test3"), new ArrayList<>(),
                path, new FilePosition(34, 470), new FilePosition(34, 475), new FilePosition(34, 476),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test3()", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithArgumentInParentInstanceInInnerClass() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        List<Expression> args = Arrays.asList(identifier("number"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("test4"), args,
                path, new FilePosition(38, 585), new FilePosition(38, 590), new FilePosition(38, 597),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test4(int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithSuperKeywordArgumentInParentInstanceInInnerClass() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        List<Expression> args = Arrays.asList(new BinaryExpression(superKeyword(), BinaryOperation.DOT,
                identifier("number")));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("test4"), args,
                path, new FilePosition(42, 718), new FilePosition(42, 723), new FilePosition(42, 736),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test4(int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallOnSuperKeywordInParentInstanceInInnerClass() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        List<Expression> args = Arrays.asList(literal("100"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression("test4",
                new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("test4")), args, path,
                new FilePosition(46, 853), new FilePosition(46, 858), new FilePosition(46, 862),
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test4(int)", expectedMethodCall);
    }

    private static UnitExpression identifier(String value) {
        return new UnitExpression(UnitExpression.ValueType.IDENTIFIER, value);
    }

    private static Expression literal(String value) {
        return new UnitExpression(UnitExpression.ValueType.LITERAL, value);
    }

    private static Expression thisKeyword() {
        return new UnitExpression(UnitExpression.ValueType.THIS, "this");
    }

    private static Expression superKeyword() {
        return new UnitExpression(UnitExpression.ValueType.SUPER, "super");
    }

    private static Expression identifierDotIdentifier(String identifier1, String identifier2) {
        return new BinaryExpression(identifier(identifier1), BinaryOperation.DOT, identifier(identifier2));
    }
}
