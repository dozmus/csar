package org.qmul.csar.code.java.postprocess.methodusage;

import org.junit.BeforeClass;
import org.junit.Test;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.parse.expression.*;
import org.qmul.csar.code.java.parse.statement.ClassStatement;
import org.qmul.csar.code.java.parse.statement.CompilationUnitStatement;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.postprocess.methodproc.MethodProcessor;
import org.qmul.csar.code.java.postprocess.methodtypes.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.io.ProjectIteratorFactory;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptors.ClassDescriptor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class MethodUsageResolverTest {

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

        MethodProcessor processor = new MethodProcessor();
        processor.setTypeHierarchyResolver(typeHierarchyResolver);
        processor.postprocess(code);

        // Resolve method usages
        MethodUsageResolver resolver = new MethodUsageResolver();
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
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), args, path, 10);

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testSameClassRecursiveInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                identifier("testSameClassRecursiveInstanceMethodCall"), new ArrayList<>(), path, 16);

        // Assert
        assertContainsMethodCall("A.java", "void testSameClassRecursiveInstanceMethodCall()", expectedMethodCall);
    }

    @Test
    public void testSameClassStaticMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("staticAdd"), new ArrayList<>(),
                path, 20);

        // Assert
        assertContainsMethodCall("A.java", "void staticAdd()", expectedMethodCall);
    }

    @Test
    public void testSuperClassInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "T.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), args, path, 6);

        // Assert
        assertContainsMethodCall("U.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testSuperClassStaticMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "T.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("staticAdd"), new ArrayList<>(),
                path, 10);

        // Assert
        assertContainsMethodCall("U.java", "void staticAdd()", expectedMethodCall);
    }

    @Test
    public void testLocalVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "V.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add2"), args,
                path, 7);

        // Assert
        assertContainsMethodCall("U.java", "int add2(int,int)", expectedMethodCall);
    }

    @Test
    public void testParameterVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "V.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add3"), args,
                path, 11);

        // Assert
        assertContainsMethodCall("U.java", "int add3(int,int)", expectedMethodCall);
    }

    @Test
    public void testInstanceVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "W.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add4"), args,
                path, 8);

        // Assert
        assertContainsMethodCall("U.java", "int add4(int,int)", expectedMethodCall);
    }

    @Test
    public void testSuperMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("add5"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(call, args, path, 6);

        // Assert
        assertContainsMethodCall("U.java", "int add5(int,int)", expectedMethodCall);
    }

    @Test
    public void testThisMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("add6"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(call, args, path, 10);

        // Assert
        assertContainsMethodCall("U.java", "int add6(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallOnMethodCall() {
        // Prepare
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        MethodCallExpression lhMethodCall = new MethodCallExpression(identifier("otherAdd1"), new ArrayList<>(), path,
                10);
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(lhMethodCall, BinaryOperation.DOT, identifier("add7"));

        // Prepare for the prior method call
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("otherAdd1"), new ArrayList<>(),
                path, 10);

        // Assert
        assertContainsMethodCall("Z.java", "U otherAdd1()", expectedMethodCall);

        // Prepare for the latter method call
        expectedMethodCall = new MethodCallExpression(call, args, path, 10);

        // Assert
        assertContainsMethodCall("U.java", "int add7(int,int)", expectedMethodCall);
    }

    @Test
    public void testSuperVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("z")),
                        BinaryOperation.DOT, identifier("test1")), new ArrayList<>(), path, 14);

        // Assert
        assertContainsMethodCall("Z.java", "void test1()", expectedMethodCall);
    }

    @Test
    public void testThisVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("z")),
                        BinaryOperation.DOT, identifier("test2")), new ArrayList<>(), path, 18);

        // Assert
        assertContainsMethodCall("Z.java", "void test2()", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithSubtypeArgumentValue1() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A0.java");
        List<Expression> args = Arrays.asList(identifier("b"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("c"), BinaryOperation.DOT, identifier("method2")),
                args, path, 8);

        // Assert
        assertContainsMethodCall("Class1.java", "void method2(A)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithSubtypeArgumentValue2() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A0.java");
        List<Expression> args = Arrays.asList(new MethodCallExpression(identifier("getB"), path, 13));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("c"), BinaryOperation.DOT, identifier("method2")),
                args, path, 13);

        // Assert
        assertContainsMethodCall("Class1.java", "void method2(A)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithSubtypeArgumentValue3() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A0.java");
        List<Expression> args = Arrays.asList(
                new MethodCallExpression(new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("getB")),
                        path, 18)
        );
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("c"), BinaryOperation.DOT, identifier("method2")),
                args, path, 18);

        // Assert
        assertContainsMethodCall("Class1.java", "void method2(A)", expectedMethodCall);
    }

    @Test
    public void testStaticMethodCallOnClassName() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("V"), BinaryOperation.DOT, identifier("testLocalVariableMethodCall")),
                new ArrayList<>(), path, 6);

        // Assert
        assertContainsMethodCall("V.java", "void testLocalVariableMethodCall()", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithNestedParameterArguments() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        List<Expression> args = Arrays.asList(identifier("a"), identifier("b"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("adder"), BinaryOperation.DOT, identifier("add")), args, path, 18);

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
                new BinaryExpression(classDef, BinaryOperation.DOT, identifier("add")), args, path, 24);

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithLocalVariableArgument() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        List<Expression> args = Arrays.asList(identifier("a"), identifier("d"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("adder"), BinaryOperation.DOT, identifier("add")), args, path, 33);

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithLocalVariableArgumentFromForEachLoop() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
        List<Expression> args = Arrays.asList(identifier("i"), literal("5"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(identifier("adder"), BinaryOperation.DOT, identifier("add")), args, path, 42);

        // Assert
        assertContainsMethodCall("A.java", "int add(int,int)", expectedMethodCall);
    }

//    @Test
//    public void testStaticMethodCallOnFullyQualifiedName() {
//        // Expected method call
//        Path path = Paths.get(SAMPLES_DIRECTORY, "A1.java");
//        MethodCallExpression expectedMethodCall = new MethodCallExpression(
//                new BinaryExpression(identifier("base"), BinaryOperation.DOT,
//                        new BinaryExpression(identifier("V"), BinaryOperation.DOT,
//                                identifier("testLocalVariableMethodCall"))),
//                new ArrayList<>(), path, 6);
//
//        // Assert
//        assertContainsMethodCall("V.java", "void testLocalVariableMethodCall()", expectedMethodCall);
//    }

    @Test
    public void testMethodCallOnParentInstanceInInnerClass() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("test3"), new ArrayList<>(),
                path, 34);

        // Assert
        assertContainsMethodCall("Z.java", "void test3()", expectedMethodCall);
    }

    @Test
    public void testMethodCallWithArgumentInParentInstanceInInnerClass() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        List<Expression> args = Arrays.asList(identifier("number"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("test4"), args,
                path, 38);

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
                path, 42);

        // Assert
        assertContainsMethodCall("Z.java", "void test4(int)", expectedMethodCall);
    }

    @Test
    public void testMethodCallOnSuperKeywordInParentInstanceInInnerClass() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
        List<Expression> args = Arrays.asList(literal("100"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("test4")), args, path, 46);

        // Assert
        assertContainsMethodCall("Z.java", "void test4(int)", expectedMethodCall);
    }

    // TODO impl and test: interactions with super of super

    private static MethodStatement findMethod(String path, String methodSignature) {
        Statement code = MethodUsageResolverTest.code.get(Paths.get(SAMPLES_DIRECTORY + path));
        CompilationUnitStatement compilationUnitStatement = (CompilationUnitStatement)code;
        ClassStatement clazz = (ClassStatement) compilationUnitStatement.getTypeStatement();

        for (Statement statement : clazz.getBlock().getStatements()) {
            if (statement instanceof MethodStatement) {
                MethodStatement method = (MethodStatement)statement;

                if (method.getDescriptor().signature().equals(methodSignature)) {
                    return method;
                }
            }
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
