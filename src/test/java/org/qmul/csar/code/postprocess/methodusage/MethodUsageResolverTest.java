package org.qmul.csar.code.postprocess.methodusage;

import org.junit.BeforeClass;
import org.junit.Test;
import org.qmul.csar.code.parse.ProjectCodeParser;
import org.qmul.csar.code.parse.java.expression.BinaryExpression;
import org.qmul.csar.code.parse.java.expression.BinaryOperation;
import org.qmul.csar.code.parse.java.expression.MethodCallExpression;
import org.qmul.csar.code.parse.java.expression.UnitExpression;
import org.qmul.csar.code.parse.java.statement.ClassStatement;
import org.qmul.csar.code.parse.java.statement.MethodStatement;
import org.qmul.csar.code.parse.java.statement.TopLevelTypeStatement;
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
    private static Map<Path, Statement> code;

    @BeforeClass
    public static void setUp() {
        // Parse sample directory
        Iterator<Path> it = ProjectIteratorFactory.createFilteredIterator(Paths.get(SAMPLES_DIRECTORY), false);
        ProjectCodeParser parser = new ProjectCodeParser(it);
        code = parser.results();

        // Resolve method usages
        resolver.resolve(code);
    }

    @Test
    public void testSameClassInstanceMethodCall() {
        // Expected method call
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), args, 10);
        List<MethodCallExpression> calls = findMethod("A.java", "int add(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSameClassRecursiveInstanceMethodCall() {
        // Expected method call
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), new ArrayList<>(), 11);
        List<MethodCallExpression> calls = findMethod("A.java", "void add()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSameClassStaticMethodCall() {
        // Expected method call
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("staticAdd"), new ArrayList<>(),
                12);
        List<MethodCallExpression> calls = findMethod("A.java", "void staticAdd()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSuperClassInstanceMethodCall() {
        // Expected method call
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("add"), args, 6);
        List<MethodCallExpression> calls = findMethod("U.java", "int add(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSuperClassStaticMethodCall() {
        // Expected method call
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("staticAdd"), new ArrayList<>(), 7);
        List<MethodCallExpression> calls = findMethod("U.java", "void staticAdd()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testLocalVariableMethodCall() {
        // Expected method call
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add2"), args, 7);
        List<MethodCallExpression> calls = findMethod("U.java", "int add2(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testParameterVariableMethodCall() {
        // Expected method call
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add3"), args, 11);
        List<MethodCallExpression> calls = findMethod("U.java", "int add3(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testInstanceVariableMethodCall() {
        // Expected method call
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifierDotIdentifier("u", "add4"), args, 8);
        List<MethodCallExpression> calls = findMethod("U.java", "int add4(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSuperMethodCall() {
        // Expected method call
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("add5"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(call, args, 6);
        List<MethodCallExpression> calls = findMethod("U.java", "int add5(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testThisMethodCall() {
        // Expected method call
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("add6"));
        MethodCallExpression expectedMethodCall = new MethodCallExpression(call, args, 10);
        List<MethodCallExpression> calls = findMethod("U.java", "int add6(int,int)").getMethodUsages();

        // Assert
        System.err.println("TMP:"+calls);
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testMethodCallOnMethodCall() {
        // Prepare
        MethodCallExpression lhMethodCall = new MethodCallExpression(identifier("otherAdd1"), new ArrayList<>(), 10);
        List<Expression> args = Arrays.asList(literal("1"), literal("2"));
        BinaryExpression call = new BinaryExpression(lhMethodCall, BinaryOperation.DOT, identifier("add7"));

        // Prepare for the prior method call
        MethodCallExpression expectedMethodCall = new MethodCallExpression(identifier("otherAdd1"), new ArrayList<>(), 10);
        List<MethodCallExpression> calls = findMethod("Z.java", "U otherAdd1()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));

        // Prepare for the latter method call
        expectedMethodCall = new MethodCallExpression(call, args, 10);
        calls = findMethod("U.java", "int add7(int,int)").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testSuperVariableMethodCall() {
        // Expected method call
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(new BinaryExpression(superKeyword(), BinaryOperation.DOT, identifier("z")),
                        BinaryOperation.DOT, identifier("test1")), new ArrayList<>(), 14);

        List<MethodCallExpression> calls = findMethod("Z.java", "void test1()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    @Test
    public void testThisVariableMethodCall() {
        // Expected method call
        MethodCallExpression expectedMethodCall = new MethodCallExpression(
                new BinaryExpression(new BinaryExpression(thisKeyword(), BinaryOperation.DOT, identifier("z")),
                        BinaryOperation.DOT, identifier("test2")), new ArrayList<>(), 18);
        List<MethodCallExpression> calls = findMethod("Z.java", "void test2()").getMethodUsages();

        // Assert
        assertEquals(1, calls.size());
        assertEquals(expectedMethodCall, calls.get(0));
    }

    // TODO impl and test: interactions with super of super

    private static MethodStatement findMethod(String path, String methodSignature) {
        Statement code = MethodUsageResolverTest.code.get(Paths.get(SAMPLES_DIRECTORY + path));
        TopLevelTypeStatement topLevelTypeStatement = (TopLevelTypeStatement)code;
        ClassStatement clazz = (ClassStatement)topLevelTypeStatement.getTypeStatement();

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
