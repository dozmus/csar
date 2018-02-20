package org.qmul.csar.code.java.postprocess.methodusage;

import org.junit.BeforeClass;
import org.junit.Test;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.parse.expression.BinaryExpression;
import org.qmul.csar.code.java.parse.expression.BinaryOperation;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.expression.UnitExpression;
import org.qmul.csar.code.java.parse.statement.ClassStatement;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.parse.statement.CompilationUnitStatement;
import org.qmul.csar.io.ProjectIteratorFactory;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class MethodUsageResolverTest {

    private static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/postprocess/";
    private static final MethodUsageResolver resolver = new MethodUsageResolver();
    private static final TypeHierarchyResolver typeHierarchyResolver = new TypeHierarchyResolver();
    private static Map<Path, Statement> code;

    @BeforeClass
    public static void setUp() throws IllegalAccessException, InstantiationException {
        // Parse sample directory
        CodeParserFactory factory = new CodeParserFactory(JavaCodeParser.class);
        Iterator<Path> it = ProjectIteratorFactory.createFilteredIterator(Paths.get(SAMPLES_DIRECTORY), false, factory);
        DefaultProjectCodeParser parser = new DefaultProjectCodeParser(factory, it);
        code = parser.results();

        // Resolve method usages
        typeHierarchyResolver.postprocess(code);
        resolver.setTypeHierarchyResolver(typeHierarchyResolver);
        resolver.postprocess(code);
    }

    @Test
    public void testSameClassInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), args, path, 10);
        List<MethodCallExpression> calls = findMethod("A.java", "int add(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSameClassRecursiveInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), new ArrayList<>(), path,
                11);
        List<MethodCallExpression> calls = findMethod("A.java", "void add()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSameClassStaticMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "A.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("staticAdd"), new ArrayList<>(),
                path, 12);
        List<MethodCallExpression> calls = findMethod("A.java", "void staticAdd()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSuperClassInstanceMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "T.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), args, path, 6);
        List<MethodCallExpression> calls = findMethod("U.java", "int add(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSuperClassStaticMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "T.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("staticAdd"), new ArrayList<>(),
                path, 7);
        List<MethodCallExpression> calls = findMethod("U.java", "void staticAdd()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testLocalVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "V.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add2"), args,
                path, 7);
        List<MethodCallExpression> calls = findMethod("U.java", "int add2(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testParameterVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "V.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add3"), args,
                path, 11);
        List<MethodCallExpression> calls = findMethod("U.java", "int add3(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testInstanceVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "W.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add4"), args,
                path, 8);
        List<MethodCallExpression> calls = findMethod("U.java", "int add4(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSuperMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("add5"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(call, args, path, 6);
        List<MethodCallExpression> calls = findMethod("U.java", "int add5(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testThisMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("add6"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(call, args, path, 10);
        List<MethodCallExpression> calls = findMethod("U.java", "int add6(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
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
        List<MethodCallExpression> calls = findMethod("Z.java", "U otherAdd1()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));

        // Prepare for the latter method call
        expectedMethodCall = new MethodCallExpression(call, args, path, 10);
        calls = findMethod("U.java", "int add7(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSuperVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("z")),
                        BinaryOperation.DOT, identifier("test1")), new ArrayList<>(), path, 14);

        List<MethodCallExpression> calls = findMethod("Z.java", "void test1()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testThisVariableMethodCall() {
        // Expected method call
        Path path = Paths.get(SAMPLES_DIRECTORY, "Y.java");
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("z")),
                        BinaryOperation.DOT, identifier("test2")), new ArrayList<>(), path, 18);
        List<MethodCallExpression> calls = findMethod("Z.java", "void test2()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

//    @Test
//    public void testMethodCallOnParentInstanceInStaticInnerClass() {
//        // Expected method call
//        Path path = Paths.get(SAMPLES_DIRECTORY, "Z.java");
//        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("test3"), new ArrayList<>(),
//                path, 28);
//        List<MethodCallExpression> calls = findMethod("Z.java", "void test3()").getMethodUsages();
//
//        // Assert
//        assertEquals(1, calls.size());
//        assertEquals(expectedMethodCall, calls.get(0));
//    }

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
