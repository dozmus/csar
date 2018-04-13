package org.qmul.csar.code.java.postprocess.methods.use;

import org.junit.BeforeClass;
import org.junit.Test;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.parse.expression.*;
import org.qmul.csar.code.java.parse.statement.ClassStatement;
import org.qmul.csar.code.java.parse.statement.CompilationUnitStatement;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.io.it.ProjectIteratorFactory;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptors.ClassDescriptor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class MethodUseResolverTest {

    private static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/postprocess/";
    private static Map<Path, Statement> code;

    @BeforeClass
    public static void setUp() throws IllegalAccessException, InstantiationException {
        // Parse sample directory
        CodeParserFactory factory = new CodeParserFactory(JavaCodeParser.class);
        Iterator<Path> it = ProjectIteratorFactory.createFilteredIterator(Paths.get(SAMPLES_DIRECTORY), false, factory);
        DefaultProjectCodeParser parser = new DefaultProjectCodeParser(factory, it);
        code = parser.results();

        // Pre-requisites
        TypeHierarchyResolver typeHierarchyResolver = new TypeHierarchyResolver();
        typeHierarchyResolver.postprocess(code);

        MethodQualifiedTypeResolver methodQualifiedTypeResolver = new MethodQualifiedTypeResolver();
        methodQualifiedTypeResolver.postprocess(code);

        MethodCallTypeInstanceResolver processor = new MethodCallTypeInstanceResolver();
        processor.setTypeHierarchyResolver(typeHierarchyResolver);
        processor.postprocess(code);

        // Resolve method usages
        MethodUseResolver resolver = new MethodUseResolver();
        resolver.setTypeHierarchyResolver(typeHierarchyResolver);
        resolver.postprocess(code);
    }

    /**
     * Attempts to find the method in the argument path with the argument signature, and asserts that the method is not
     * null and that its method called contains the argument expected.
     */
    private static void assertContainsMethodCall(String path, String methodSignature, MethodCallExpression expected) {
        MethodStatement methodStatement = findMethod(path, methodSignature);
        assertTrue(methodStatement != null);
        List<MethodCallExpression> calls = methodStatement.getMethodUsages();
        assertTrue(calls.contains(expected));
    }

    @Test
    public void testSameClassInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), args, path, 10, 11, 16,
                Arrays.asList(13));

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testSameClassRecursiveInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                identifier("testSameClassRecursiveInstanceMethodCall"), new ArrayList<>(), path, 16, 48, 49,
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("A.java", "void testSameClassRecursiveInstanceMethodCall()", expectedMethodCall);
    }

    @Test
    public void testSameClassStaticMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("staticAdd"), new ArrayList<>(),
                path, 20, 17, 18, Collections.emptyList());

        // Assert
        assertContainsMethodCall("A.java", "void staticAdd()", expectedMethodCall);
    }

    @Test
    public void testSuperClassInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "T.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), args, path, 6, 11, 16,
                Arrays.asList(13));

        // Assert
        assertContainsMethodCall("U.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testSuperClassStaticMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "T.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("staticAdd"), new ArrayList<>(),
                path, 10, 17, 18, Collections.emptyList());

        // Assert
        assertContainsMethodCall("U.java", "void staticAdd()", expectedMethodCall);
    }

    @Test
    public void testLocalVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "V.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add2"), args,
                path, 7, 14, 19, Arrays.asList(16));

        // Assert
        assertContainsMethodCall("U.java", "int add2(int,int)", expectedMethodCall);
    }

    @Test
    public void testParameterVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "V.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add3"), args,
                path, 11, 14, 19, Arrays.asList(16));

        // Assert
        assertContainsMethodCall("U.java", "int add3(int,int)", expectedMethodCall);
    }

    @Test
    public void testInstanceVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "W.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add4"), args,
                path, 8, 14, 19, Arrays.asList(16));

        // Assert
        assertContainsMethodCall("U.java", "int add4(int,int)", expectedMethodCall);
    }

    @Test
    public void testSuperMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("add5"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(call, args, path, 6, 18, 23,
                Arrays.asList(20));

        // Assert
        assertContainsMethodCall("U.java", "int add5(int,int)", expectedMethodCall);
    }

    @Test
    public void testThisMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("add6"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(call, args, path, 10, 17, 22,
                Arrays.asList(19));

        // Assert
        assertContainsMethodCall("U.java", "int add6(int,int)", expectedMethodCall);
    }

    @Test
    public void testSuperVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("z")),
                        BinaryOperation.DOT, identifier("test1")), new ArrayList<>(), path, 14, 21, 22,
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test1()", expectedMethodCall);
    }

    @Test
    public void testThisVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("z")),
                        BinaryOperation.DOT, identifier("test2")), new ArrayList<>(), path, 18, 20, 21,
                Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test2()", expectedMethodCall);
    }

    @Test
    public void testMethodCallOnMethodCall() {
        // Prepare
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        MethodCallExpression lhMethodCall = new MethodCallExpression(identifier("otherAdd1"), new ArrayList<>(), path,
                10, 17, 18, Collections.emptyList());
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(lhMethodCall, BinaryOperation.DOT, identifier("add7"));

        // Prepare for the prior method call
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("otherAdd1"), new ArrayList<>(),
                path, 10, 17, 18, Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "U otherAdd1()", expectedMethodCall);

        // Prepare for the latter method call
        expectedMethodCall = new MethodCallExpression(call, args, path, 10, 24, 29, Arrays.asList(26));

        // Assert
        assertContainsMethodCall("U.java", "int add7(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithSubtypeArgumentValue1() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A0.java");
        List<Expression> args = Arrays.asList(identifier("b"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("c"), BinaryOperation.DOT, identifier("method2")),
                args, path, 8, 17, 19, Collections.emptyList());

        // Assert
        assertContainsMethodCall("Class1.java", "void method2(A)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithSubtypeArgumentValue2() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A0.java");
        List<Expression> args = Arrays.asList(new MethodCallExpression(identifier("getB"), new ArrayList<>(), path, 13,
                22, 23, Collections.emptyList()));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("c"), BinaryOperation.DOT, identifier("method2")),
                args, path, 13, 17, 24, Collections.emptyList());

        // Assert
        assertContainsMethodCall("Class1.java", "void method2(A)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithSubtypeArgumentValue3() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A0.java");
        List<Expression> args = Arrays.asList(
                new MethodCallExpression(new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("getB")),
                        new ArrayList<>(), path, 18, 27, 28, Collections.emptyList())
        );
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("c"), BinaryOperation.DOT, identifier("method2")),
                args, path, 18, 17, 29, Collections.emptyList());

        // Assert
        assertContainsMethodCall("Class1.java", "void method2(A)", expectedMethodCall);
    }

    @Test
    public void testStaticMethodCallOnClassName() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("V"), BinaryOperation.DOT, identifier("testLocalVariableMethodCall")),
                new ArrayList<>(), path, 6, 37, 38, Collections.emptyList());

        // Assert
        assertContainsMethodCall("V.java", "void testLocalVariableMethodCall()", expectedMethodCall);
    }

    @Test
    public void testStaticMethodCallOnFullyQualifiedName() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(new BinaryExpression(identifier("base"), BinaryOperation.DOT, identifier("V")),
                        BinaryOperation.DOT, identifier("testLocalVariableMethodCall")),
                new ArrayList<>(), path, 10, 42, 43, Collections.emptyList());

        // Assert
        assertContainsMethodCall("V.java", "void testLocalVariableMethodCall()", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithNestedParameterArguments() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        List<Expression> args = Arrays.asList(identifier("a"), identifier("b"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("adder"), BinaryOperation.DOT, identifier("add")), args, path, 18, 32,
                37, Arrays.asList(34));

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
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(classDef, BinaryOperation.DOT, identifier("add")), args, path, 24, 19, 24,
                Arrays.asList(21));

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithLocalVariableArgument() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        List<Expression> args = Arrays.asList(identifier("a"), identifier("d"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("adder"), BinaryOperation.DOT, identifier("add")), args, path, 33, 32,
                37, Arrays.asList(34));

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithLocalVariableArgumentFromForEachLoop() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        List<Expression> args = Arrays.asList(identifier("i"), literal("5"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("adder"), BinaryOperation.DOT, identifier("add")), args, path, 42,
                21, 26, Arrays.asList(23));

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallOnParentInstanceInInnerClass() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("test3"), new ArrayList<>(),
                path, 34, 17, 18, Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test3()", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithArgumentInParentInstanceInInnerClass() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        List<Expression> args = Arrays.asList(identifier("number"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("test4"), args,
                path, 38, 17, 24, Collections.emptyList());

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
                path, 42, 17, 30, Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test4(int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallOnSuperKeywordInParentInstanceInInnerClass() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        List<Expression> args = Arrays.asList(literal("100"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("test4")), args, path, 46,
                23, 27, Collections.emptyList());

        // Assert
        assertContainsMethodCall("Z.java", "void test4(int)", expectedMethodCall);
    }

    private static MethodStatement findMethod(String path, String methodSignature) {
        Statement code = MethodUseResolverTest.code.get(Paths.get(SAMPLES_DIRECTORY + path));
        CompilationUnitStatement cus = (CompilationUnitStatement)code;

        if (cus.getTypeStatement() instanceof ClassStatement) {
            ClassStatement clazz = (ClassStatement) cus.getTypeStatement();

            return clazz.getBlock().getStatements().stream()
                    .filter(s -> s instanceof MethodStatement)
                    .map(s -> (MethodStatement)s)
                    .filter(m -> m.getDescriptor().signature().equals(methodSignature))
                    .findFirst().orElse(null);
        }
        return null;
    }

    private static Expression identifier(String value) {
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
