package org.qmul.csar.code;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.qmul.csar.code.java.expression.*;
import org.qmul.csar.code.java.statement.*;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public final class JavaCodeGeneratorTest {

    // TODO add enum and annotation tests

    /**
     * Directory of the java code files.
     */
    private static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/code/";
    private final TypeStatement expected;
    private final String sampleFileName;

    public JavaCodeGeneratorTest(TypeStatement expected, String sampleFileName) {
        this.expected = expected;
        this.sampleFileName = sampleFileName;
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample1.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample1() {
        // Constructor #1
        ConstructorStatement constructor1 = new ConstructorStatement.Builder("Sample1")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .parameterCount(0)
                .build();

        // Constructor #2
        LocalVariableStatements local1 = createLocals(new LocalVariableStatement(
                new LocalVariableDescriptor.Builder("s").identifierType("String[]").finalModifier(false).build(),
                Optional.empty(), new ArrayList<>()));

        ParameterVariableStatement param11 = createParameter("String...", "names", false);
        ConstructorStatement constructor2 = new ConstructorStatement.Builder("Sample1")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .parameterCount(1)
                .parameters(Arrays.asList(param11))
                .typeParameters(Arrays.asList("E"))
                .block(new BlockStatement(Arrays.asList(local1)))
                .build();

        // Instance #1
        InstanceVariableStatement var1 = new InstanceVariableStatement(
                new InstanceVariableDescriptor.Builder("className")
                        .visibilityModifier(VisibilityModifier.PRIVATE)
                        .staticModifier(false)
                        .finalModifier(true)
                        .identifierType("String")
                        .build(),
                new ArrayList<>(),
                Optional.of(new UnitExpression(UnitExpression.ValueType.LITERAL, "\"Sample1\"")));

        // Instance #2
        InstanceVariableStatement var2 = new InstanceVariableStatement(
                new InstanceVariableDescriptor.Builder("str")
                        .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                        .staticModifier(false)
                        .finalModifier(false)
                        .identifierType("String")
                        .build(),
                new ArrayList<>(), Optional.empty());

        // Method #1
        ParameterVariableStatement param21 = createParameter("int[]", "a", true);
        ParameterVariableStatement param22 = createParameter("int", "b", false);
        MethodStatement method1 = new MethodStatement(MethodDescriptor.Builder.allFalse("add")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .abstractModifier(true)
                .returnType("void")
                .parameters(Arrays.asList(param21.getDescriptor(), param22.getDescriptor()))
                .parameterCount(2)
                .build(), Arrays.asList(param21, param22), BlockStatement.EMPTY, new ArrayList<>());

        // Method #2
        ReturnStatement returnSt = new ReturnStatement(
                Optional.of(new UnitExpression(UnitExpression.ValueType.IDENTIFIER,
                        "result")));
        MethodStatement method2 = new MethodStatement(MethodDescriptor.Builder.allFalse("getResult")
                .visibilityModifier(VisibilityModifier.PROTECTED)
                .finalModifier(true)
                .returnType("int")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(returnSt)), new ArrayList<>());

        // Method #3
        LocalVariableStatements local = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("k")
                .finalModifier(true)
                .identifierType("int")
                .build(),
                Optional.of(new UnitExpression(UnitExpression.ValueType.LITERAL, "3")), new ArrayList<>()));
        UnitExpression thisIdentifier = new UnitExpression(UnitExpression.ValueType.THIS, "this");
        UnitExpression resultIdentifier = new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "result");
        ExpressionStatement assignmentExpr = new ExpressionStatement(new BinaryExpression(
                new BinaryExpression(thisIdentifier, BinaryOperation.DOT, resultIdentifier),
                BinaryOperation.ASSIGN,
                resultIdentifier));

        ParameterVariableStatement param31 = createParameter("int", "result", false);
        MethodStatement method3 = new MethodStatement(MethodDescriptor.Builder.allFalse("setResult")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .finalModifier(true)
                .returnType("void")
                .parameters(Arrays.asList(param31.getDescriptor()))
                .parameterCount(1)
                .typeParameters(Arrays.asList("E extends AbstractSample"))
                .build(),
                Arrays.asList(param31), new BlockStatement(Arrays.asList(local, assignmentExpr)), new ArrayList<>());

        // Top-level class
        return new ClassStatement(ClassDescriptor.Builder.allFalse("Sample1")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .strictfpModifier(true)
                .abstractModifier(true)
                .superClasses(Arrays.asList("AbstractSample"))
                .build(),
                new BlockStatement(Arrays.asList(constructor1, constructor2, var1, var2, method1, method2, method3)),
                new ArrayList<>());
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample2.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample2() {
        // Instance #1
        InstanceVariableStatement var1 = new InstanceVariableStatement(
                new InstanceVariableDescriptor.Builder("ITERATIONS")
                        .visibilityModifier(VisibilityModifier.PUBLIC)
                        .staticModifier(true)
                        .finalModifier(false)
                        .identifierType("int")
                        .build(),
                new ArrayList<>(), Optional.of(new UnitExpression(UnitExpression.ValueType.LITERAL, "1000")));

        // Method #1
        Expression methodName = new BinaryExpression(new BinaryExpression(
                new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "System"), BinaryOperation.DOT,
                new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "out")
        ), BinaryOperation.DOT, new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "println"));
        List<Expression> arguments = Arrays.asList(new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "s"));
        ExpressionStatement methodCall1 = new ExpressionStatement(new MethodCallExpression(methodName, arguments));

        ParameterVariableStatement param1 = createParameter("String", "s", false);
        MethodStatement method1 = new MethodStatement(MethodDescriptor.Builder.allFalse("print")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("void")
                .defaultModifier(true)
                .parameters(Arrays.asList(param1.getDescriptor()))
                .parameterCount(1)
                .build(), Arrays.asList(param1), new BlockStatement(Arrays.asList(methodCall1)), new ArrayList<>());

        // Method #2
        ParameterVariableStatement param21 = createParameter("E", "level", false);
        ParameterVariableStatement param22 = createParameter("String...", "s", false);
        MethodStatement method2 = new MethodStatement(MethodDescriptor.Builder.allFalse("print")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(Arrays.asList(param21.getDescriptor(), param22.getDescriptor()))
                .parameterCount(2)
                .typeParameters(Arrays.asList("E"))
                .build(), Arrays.asList(param21, param22), BlockStatement.EMPTY, new ArrayList<>());

        // Instance #1
        Expression methodName2 = new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "generateName");
        List<Expression> methodArgs
                = Arrays.asList(new UnitExpression(UnitExpression.ValueType.CLASS_REFERENCE, "Sample2.class"));
        InstanceVariableStatement var2 = new InstanceVariableStatement(new InstanceVariableDescriptor.Builder("name")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .identifierType("String[]")
                .staticModifier(false)
                .finalModifier(false)
                .build(),
                new ArrayList<>(), Optional.of(new MethodCallExpression(methodName2, methodArgs)));

        // Method #3
        ParameterVariableStatement param31 = createParameter("String[]", "$", false);
        MethodStatement method3 = new MethodStatement(MethodDescriptor.Builder.allFalse("print")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(Arrays.asList(param31.getDescriptor()))
                .parameterCount(1)
                .build(), Arrays.asList(param31), BlockStatement.EMPTY, new ArrayList<>());

        // Top-level class
        BlockStatement block = new BlockStatement(Arrays.asList(var1, method1, method2, var2, method3));
        return new ClassStatement(ClassDescriptor.Builder.allFalse("Sample2")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .interfaceModifier(true)
                .superClasses(Arrays.asList("Runnable"))
                .build(), block, new ArrayList<>());
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample3.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample3() {
        return createClass(ClassDescriptor.Builder.allFalse("Sample3")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .typeParameters(Arrays.asList("List extends Collection<String>", "T"))
                .build());
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample4.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample4() {
        // Local interface
        MethodStatement interfaceMethod = createMethod(MethodDescriptor.Builder.allFalse("run")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build());
        ClassStatement localInterface = new ClassStatement(ClassDescriptor.Builder.allFalse("Runnable")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .local(true)
                .interfaceModifier(true)
                .build(), new BlockStatement(Arrays.asList(interfaceMethod)), new ArrayList<>());

        // Local class
        LocalVariableStatement local = new LocalVariableStatement(new LocalVariableDescriptor.Builder("x")
                .finalModifier(false)
                .identifierType("int")
                .build(), Optional.of(new UnitExpression(UnitExpression.ValueType.LITERAL, "30")), new ArrayList<>());
        LocalVariableStatements locals = new LocalVariableStatements(Arrays.asList(local));

        Expression methodName = new BinaryExpression(new BinaryExpression(
                new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "System"), BinaryOperation.DOT,
                new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "out")
        ), BinaryOperation.DOT, new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "println"));
        List<Expression> arguments = Arrays.asList(new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "x"));
        ExpressionStatement methodCall1 = new ExpressionStatement(new MethodCallExpression(methodName, arguments));

        MethodStatement innerClassMethod = new MethodStatement(MethodDescriptor.Builder.allFalse("run")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("void")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(locals, methodCall1)), new ArrayList<>());
        ClassStatement localClass = new ClassStatement(ClassDescriptor.Builder.allFalse("A")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .local(true)
                .superClasses(Arrays.asList("Runnable"))
                .build(), new BlockStatement(Arrays.asList(innerClassMethod)), new ArrayList<>());

        // Instantiation
        ClassStatement calledClass = createClass(ClassDescriptor.Builder.allFalse("A").local(true).build());
        Expression newClass = new InstantiateClassExpression(calledClass, new ArrayList<>(), new ArrayList<>(), false);
        LocalVariableStatement local2 = new LocalVariableStatement(new LocalVariableDescriptor.Builder("worker")
                .finalModifier(false)
                .identifierType("A")
                .build(), Optional.of(newClass), new ArrayList<>());
        LocalVariableStatements locals2 = new LocalVariableStatements(Arrays.asList(local2));

        // Method call
        ExpressionStatement methodCall2 = new ExpressionStatement(new MethodCallExpression(new BinaryExpression(
                new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "worker"),
                BinaryOperation.DOT, new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "run")),
                new ArrayList<>()));

        // Parent class
        MethodStatement parentClassMethod = new MethodStatement(MethodDescriptor.Builder.allFalse("work")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(localInterface, localClass, locals2,
                methodCall2)), new ArrayList<>());
        return new ClassStatement(ClassDescriptor.Builder.allFalse("Sample4")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build(), new BlockStatement(Arrays.asList(parentClassMethod)), new ArrayList<>());
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample5.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample5() {
        // Inner interface
        ClassStatement innerInterface = createClass(ClassDescriptor.Builder.allFalse("A")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .interfaceModifier(true)
                .inner(true)
                .build());

        // Inner class
        ParameterVariableStatement param = createParameter("int", "threads", false);
        List<ParameterVariableStatement> params = Arrays.asList(param);
        MethodStatement method = new MethodStatement(MethodDescriptor.Builder.allFalse("work")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(params.stream().map(ParameterVariableStatement::getDescriptor).collect(Collectors.toList()))
                .parameterCount(1)
                .build(), params, BlockStatement.EMPTY, new ArrayList<>());
        ClassStatement innerClass = new ClassStatement(ClassDescriptor.Builder.allFalse("B")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .superClasses(Arrays.asList("A"))
                .inner(true)
                .build(), new BlockStatement(Arrays.asList(method)), new ArrayList<>());

        // Top-level class
        return new ClassStatement(ClassDescriptor.Builder.allFalse("Sample5")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build(), new BlockStatement(Arrays.asList(innerInterface, innerClass)), new ArrayList<>());
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample6.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample6() {
        // Inner class
        ClassStatement innerClass = createClass(ClassDescriptor.Builder.allFalse("A")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .inner(true)
                .superClasses(Arrays.asList("Sample6"))
                .typeParameters(Arrays.asList("T0"))
                .build());

        // Top-l;evel interface
        return new ClassStatement(ClassDescriptor.Builder.allFalse("Sample6")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .interfaceModifier(true)
                .build(), new BlockStatement(Arrays.asList(innerClass)), new ArrayList<>());
    }

    @Parameterized.Parameters(name="{index}: \"{1}\"")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {sample1(), "Sample1.java"},
                {sample2(), "Sample2.java"},
                {sample3(), "Sample3.java"},
                {sample4(), "Sample4.java"},
                {sample5(), "Sample5.java"},
                {sample6(), "Sample6.java"}
        });
    }

    private static MethodStatement createMethod(MethodDescriptor desc) {
        return new MethodStatement(desc, new ArrayList<>(), BlockStatement.EMPTY, new ArrayList<>());
    }

    private static ClassStatement createClass(ClassDescriptor desc) {
        return new ClassStatement(desc, BlockStatement.EMPTY, new ArrayList<>());
    }

    private static ParameterVariableStatement createParameter(String type, String identifier, boolean finalModifier) {
        return new ParameterVariableStatement(new ParameterVariableDescriptor.Builder()
                .finalModifier(finalModifier)
                .identifierName(identifier)
                .identifierType(type)
                .build(), new ArrayList<>());
    }

    private static LocalVariableStatements createLocals(LocalVariableStatement... locals) {
        return new LocalVariableStatements(Arrays.asList(locals));
    }

    @Test
    public void test() throws IOException {
        assertEquals(expected, CodeParserFactory.parse(Paths.get(SAMPLES_DIRECTORY + sampleFileName)));
    }
}
