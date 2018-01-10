package org.qmul.csar.code.parse.java;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.java.expression.*;
import org.qmul.csar.code.parse.java.statement.*;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.qmul.csar.code.parse.java.expression.UnitExpression.ValueType.*;

@RunWith(value = Parameterized.class)
public final class JavaCodeGeneratorTest {

    private static final BinaryExpression SYSTEM_OUT = new BinaryExpression(identifierUnit("System"),
            BinaryOperation.DOT, identifierUnit("out"));
    private static final BinaryExpression SOUT_PRINTLN = new BinaryExpression(SYSTEM_OUT, BinaryOperation.DOT,
            identifierUnit("println"));

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
                Optional.of(literalUnit("\"Sample1\"")));

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
                .hasParameters(true)
                .stub(true)
                .build(), Arrays.asList(param21, param22), BlockStatement.EMPTY, new ArrayList<>(), 16);

        // Method #2
        ReturnStatement returnSt = new ReturnStatement(new UnitExpression(IDENTIFIER, "result"));
        MethodStatement method2 = new MethodStatement(MethodDescriptor.Builder.allFalse("getResult")
                .visibilityModifier(VisibilityModifier.PROTECTED)
                .finalModifier(true)
                .returnType("int")
                .parameterCount(0)
                .hasParameters(false)
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(returnSt)), new ArrayList<>(), 18);

        // Method #3
        LocalVariableStatements local = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("k")
                .finalModifier(true)
                .identifierType("int")
                .build(),
                Optional.of(literalUnit("3")), new ArrayList<>()));
        UnitExpression resultIdentifier = new UnitExpression(IDENTIFIER, "result");
        ExpressionStatement assignmentExpr = new ExpressionStatement(new BinaryExpression(
                new BinaryExpression(thisUnit(), BinaryOperation.DOT, resultIdentifier), BinaryOperation.ASSIGN,
                resultIdentifier));

        ParameterVariableStatement param31 = createParameter("int", "result", false);
        MethodStatement method3 = new MethodStatement(MethodDescriptor.Builder.allFalse("setResult")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .finalModifier(true)
                .returnType("void")
                .parameters(Arrays.asList(param31.getDescriptor()))
                .parameterCount(1)
                .hasParameters(true)
                .typeParameters(Arrays.asList("E extends AbstractSample"))
                .hasTypeArguments(true)
                .build(),
                Arrays.asList(param31), new BlockStatement(Arrays.asList(local, assignmentExpr)), new ArrayList<>(),
                22);

        // Top-level class
        ClassStatement clazz = new ClassStatement(ClassDescriptor.Builder.allFalse("Sample1")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .strictfpModifier(true)
                .abstractModifier(true)
                .extendedClass("AbstractSample")
                .build(),
                new BlockStatement(Arrays.asList(constructor1, constructor2, var1, var2, method1, method2, method3)),
                new ArrayList<>());
        return new TopLevelTypeStatement(Optional.of(new PackageStatement("grammars.java8pt", new ArrayList<>())),
                new ArrayList<>(), clazz);
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample2.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample2() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample2.java");

        // Instance #1
        InstanceVariableStatement var1 = new InstanceVariableStatement(
                new InstanceVariableDescriptor.Builder("ITERATIONS")
                        .visibilityModifier(VisibilityModifier.PUBLIC)
                        .staticModifier(true)
                        .finalModifier(false)
                        .identifierType("int")
                        .build(),
                new ArrayList<>(), Optional.of(literalUnit("1000")));

        // Method #1
        Expression methodName = SOUT_PRINTLN;
        List<Expression> arguments = Arrays.asList(new UnitExpression(IDENTIFIER, "s"));
        ExpressionStatement methodCall1 = new ExpressionStatement(new MethodCallExpression(methodName, arguments, path,
                8));

        ParameterVariableStatement param1 = createParameter("String", "s", false);
        MethodStatement method1 = new MethodStatement(MethodDescriptor.Builder.allFalse("print")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("void")
                .defaultModifier(true)
                .parameters(Arrays.asList(param1.getDescriptor()))
                .parameterCount(1)
                .hasParameters(true)
                .build(), Arrays.asList(param1), new BlockStatement(Arrays.asList(methodCall1)), new ArrayList<>(), 7);

        // Method #2
        ParameterVariableStatement param21 = createParameter("E", "level", false);
        ParameterVariableStatement param22 = createParameter("String...", "s", false);
        MethodStatement method2 = new MethodStatement(MethodDescriptor.Builder.allFalse("print")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(Arrays.asList(param21.getDescriptor(), param22.getDescriptor()))
                .parameterCount(2)
                .hasParameters(true)
                .typeParameters(Arrays.asList("E"))
                .hasTypeArguments(true)
                .stub(true)
                .build(), Arrays.asList(param21, param22), BlockStatement.EMPTY, new ArrayList<>(), 11);

        // Instance #1
        Expression methodName2 = new UnitExpression(IDENTIFIER, "generateName");
        List<Expression> methodArgs = Arrays.asList(new UnitExpression(CLASS_REFERENCE, "Sample2.class"));
        InstanceVariableStatement var2 = new InstanceVariableStatement(new InstanceVariableDescriptor.Builder("name")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .identifierType("String[]")
                .staticModifier(false)
                .finalModifier(false)
                .build(),
                new ArrayList<>(), Optional.of(new MethodCallExpression(methodName2, methodArgs, path, 13)));

        // Method #3
        ParameterVariableStatement param31 = createParameter("String[]", "$", false);
        MethodStatement method3 = new MethodStatement(MethodDescriptor.Builder.allFalse("print")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(Arrays.asList(param31.getDescriptor()))
                .parameterCount(1)
                .hasParameters(true)
                .stub(true)
                .build(), Arrays.asList(param31), BlockStatement.EMPTY, new ArrayList<>(), 15);

        // Top-level class
        BlockStatement block = new BlockStatement(Arrays.asList(var1, method1, method2, var2, method3));
        List<ImportStatement> imports = Arrays.asList(new ImportStatement("java.lang.Runnable", false));
        return new TopLevelTypeStatement(Optional.empty(), imports,
                new ClassStatement(ClassDescriptor.Builder.allFalse("Sample2")
                        .visibilityModifier(VisibilityModifier.PUBLIC)
                        .interfaceModifier(true)
                        .implementedInterfaces(Arrays.asList("Runnable"))
                        .build(), block, new ArrayList<>()));
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample3.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample3() {
        List<ImportStatement> imports = Arrays.asList(new ImportStatement("a.Enum", true));
        return new TopLevelTypeStatement(Optional.empty(), imports,
                createClass(ClassDescriptor.Builder.allFalse("Sample3")
                        .visibilityModifier(VisibilityModifier.PUBLIC)
                        .typeParameters(Arrays.asList("T0 extends Collection<String>", "T1"))
                        .build()));
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample4.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample4() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample4.java");

        // Local interface
        MethodStatement interfaceMethod = createMethod(MethodDescriptor.Builder.allFalse("run")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .stub(true)
                .parameterCount(0)
                .build(), 5);
        ClassStatement localInterface = new ClassStatement(ClassDescriptor.Builder.allFalse("Runnable")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .local(true)
                .interfaceModifier(true)
                .build(), new BlockStatement(Arrays.asList(interfaceMethod)), new ArrayList<>());

        // Local class
        LocalVariableStatement local = new LocalVariableStatement(new LocalVariableDescriptor.Builder("x")
                .finalModifier(false)
                .identifierType("int")
                .build(), Optional.of(literalUnit("30")), new ArrayList<>());
        LocalVariableStatements locals = createLocals(local);

        List<Expression> arguments = Arrays.asList(new UnitExpression(IDENTIFIER, "x"));
        ExpressionStatement methodCall1 = new ExpressionStatement(new MethodCallExpression(SOUT_PRINTLN, arguments,
                path, 11));

        MethodStatement innerClassMethod = new MethodStatement(MethodDescriptor.Builder.allFalse("run")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("void")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(locals, methodCall1)), new ArrayList<>(),
                9);
        ClassStatement localClass = new ClassStatement(ClassDescriptor.Builder.allFalse("A")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .local(true)
                .implementedInterfaces(Arrays.asList("Runnable"))
                .build(), new BlockStatement(Arrays.asList(innerClassMethod)), new ArrayList<>());

        // Instantiation
        ClassStatement calledClass = createClass(ClassDescriptor.Builder.allFalse("A").local(true).build());
        Expression newClass = new InstantiateClassExpression(calledClass.getDescriptor(), Optional.empty(),
                new ArrayList<>(), new ArrayList<>(), false);
        LocalVariableStatement local2 = new LocalVariableStatement(new LocalVariableDescriptor.Builder("worker")
                .finalModifier(false)
                .identifierType("A")
                .build(), Optional.of(newClass), new ArrayList<>());
        LocalVariableStatements locals2 = createLocals(local2);

        // Method call
        ExpressionStatement methodCall2 = new ExpressionStatement(new MethodCallExpression(new BinaryExpression(
                new UnitExpression(IDENTIFIER, "worker"), BinaryOperation.DOT, new UnitExpression(IDENTIFIER, "run")),
                new ArrayList<>(), path, 15));

        // Method #1
        MethodStatement parentClassMethod1 = new MethodStatement(MethodDescriptor.Builder.allFalse("work")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(localInterface, localClass, locals2,
                methodCall2)), new ArrayList<>(), 3);

        // Method #2
        MethodStatement parentClassMethod2 = new MethodStatement(MethodDescriptor.Builder.allFalse("work2")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .thrownExceptions(Arrays.asList("IOException"))
                .hasThrownExceptions(true)
                .build(),
                new ArrayList<>(), BlockStatement.EMPTY, new ArrayList<>(), 18);

        // Parent class
        return createTopLevelStatement(new ClassStatement(ClassDescriptor.Builder.allFalse("Sample4")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build(), new BlockStatement(Arrays.asList(parentClassMethod1, parentClassMethod2)),
                new ArrayList<>()));
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
                .hasParameters(true)
                .build(), params, BlockStatement.EMPTY, new ArrayList<>(), 9);
        ClassStatement innerClass = new ClassStatement(ClassDescriptor.Builder.allFalse("B")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .implementedInterfaces(Arrays.asList("A"))
                .inner(true)
                .build(), new BlockStatement(Arrays.asList(method)), new ArrayList<>());

        // Top-level class
        return createTopLevelStatement(new ClassStatement(ClassDescriptor.Builder.allFalse("Sample5")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build(), new BlockStatement(Arrays.asList(innerInterface, innerClass)), new ArrayList<>()));
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
                .implementedInterfaces(Arrays.asList("Sample6"))
                .typeParameters(Arrays.asList("T0"))
                .build());

        // Top-level interface
        return createTopLevelStatement(new ClassStatement(ClassDescriptor.Builder.allFalse("Sample6")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .interfaceModifier(true)
                .build(), new BlockStatement(Arrays.asList(innerClass)), new ArrayList<>()));
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample7.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample7() {
        // Constants
        EnumConstantStatement const1 = createEnumConstant("WINTER");
        EnumConstantStatement const2 = createEnumConstant("SUMMER");

        // Top-level enum
        BlockStatement block = new BlockStatement(Arrays.asList(const1, const2));
        EnumDescriptor desc = EnumDescriptor.Builder.allFalse("Season")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build();
        return createTopLevelStatement(new EnumStatement(desc, block, new ArrayList<>()));
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample8.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample8() {
        UnitExpression identifier1 = new UnitExpression(IDENTIFIER, "a");
        UnitExpression identifier2 = new UnitExpression(IDENTIFIER, "b");

        // Inherited method descriptor
        ParameterVariableStatement param1 = createParameter("Integer", "a", false);
        ParameterVariableStatement param2 = createParameter("Integer", "b", false);
        MethodDescriptor interfaceMethodDesc = MethodDescriptor.Builder.allFalse("apply")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("Integer")
                .parameters(Arrays.asList(param1.getDescriptor(), param2.getDescriptor()))
                .parameterCount(2)
                .hasParameters(true)
                .build();

        // Constants
        ReturnStatement return1 = new ReturnStatement(
                new BinaryExpression(identifier1, BinaryOperation.ADD, identifier2));
        MethodStatement apply1 = new MethodStatement(interfaceMethodDesc, Arrays.asList(param1, param2),
                new BlockStatement(Arrays.asList(return1)), new ArrayList<>(), 3);
        EnumConstantStatement const1 = createEnumConstant("PLUS", new BlockStatement(Arrays.asList(apply1)));

        ReturnStatement return2 = new ReturnStatement(
                new BinaryExpression(identifier1, BinaryOperation.SUB, identifier2));
        MethodStatement apply2 = new MethodStatement(interfaceMethodDesc, Arrays.asList(param1, param2),
                new BlockStatement(Arrays.asList(return2)), new ArrayList<>(), 8);
        EnumConstantStatement const2 = createEnumConstant("MINUS", new BlockStatement(Arrays.asList(apply2)));

        // Top-level enum
        EnumDescriptor desc = EnumDescriptor.Builder.allFalse("SimpleOperators")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .superClasses(Arrays.asList("Operator<Integer>"))
                .build();
        return createTopLevelStatement(new EnumStatement(desc, new BlockStatement(Arrays.asList(const1, const2)),
                new ArrayList<>()));
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample9.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample9() {
        // Constants
        EnumConstantStatement const1 = createEnumConstant("USD", Arrays.asList(literalUnit("1.10")));
        EnumConstantStatement const2 = createEnumConstant("GBP", Arrays.asList(literalUnit("1")));

        // Body
        UnitExpression valueIdent = new UnitExpression(IDENTIFIER, "value");

        InstanceVariableStatement variable = new InstanceVariableStatement(
                InstanceVariableDescriptor.Builder.allFalse("value")
                        .finalModifier(true)
                        .visibilityModifier(VisibilityModifier.PRIVATE)
                        .identifierType("double")
                        .build(), new ArrayList<>(), Optional.empty());

        BlockStatement constructorBlock = new BlockStatement(Arrays.asList(new ExpressionStatement(
                new BinaryExpression(new BinaryExpression(
                        thisUnit(), BinaryOperation.DOT, valueIdent), BinaryOperation.ASSIGN, valueIdent))
        ));
        ConstructorStatement constructor = new ConstructorStatement.Builder("Currency")
                .visibilityModifier(VisibilityModifier.PRIVATE)
                .parameterCount(1)
                .block(constructorBlock)
                .parameters(Arrays.asList(createParameter("double", "value", false)))
                .build();

        ReturnStatement returnSt = new ReturnStatement(valueIdent);
        MethodStatement method = new MethodStatement(MethodDescriptor.Builder.allFalse("getValue")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("double")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(returnSt)), new ArrayList<>(), 11);

        // Top-level enum
        BlockStatement block = new BlockStatement(Arrays.asList(const1, const2, variable, constructor, method));
        EnumDescriptor desc = EnumDescriptor.Builder.allFalse("Currency")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build();
        return createTopLevelStatement(new EnumStatement(desc, block, new ArrayList<>()));
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample10.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample10() {
        Annotation.Value value1 = new Annotation.Values("ApiClass",
                Arrays.asList(createAnonExprValue("author", "\"Deniz Ozmus\"")));
        Annotation apiClassAnnotation = new Annotation("ApiClass", Optional.of(value1));

        Annotation.Value value2 = new Annotation.Values("Metadata",
                Arrays.asList(createAnonExprValue("author", "\"DO\""), createAnonExprValue("since", "1.0")));
        Annotation authorMethodAnnotation = new Annotation("Metadata", Optional.of(value2));

        AnnotationStatement.AnnotationMethod author = new AnnotationStatement.AnnotationMethod(
                VisibilityModifier.PACKAGE_PRIVATE, false, "author", Optional.empty(),
                Arrays.asList(authorMethodAnnotation));

        AnnotationStatement.AnnotationMethod date = new AnnotationStatement.AnnotationMethod(
                VisibilityModifier.PACKAGE_PRIVATE, false, "date",
                Optional.of(createAnonExprValue("date", "\"N/A\"")),
                Arrays.asList(new Annotation("Deprecated", Optional.empty())));

        // Top-level annotation type
        return createTopLevelStatement(new AnnotationStatement(new AnnotationDescriptor.Builder("FileChange")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .inner(false)
                .abstractModifier(false)
                .strictfpModifier(false)
                .build(), new BlockStatement(Arrays.asList(author, date)), Arrays.asList(apiClassAnnotation)));
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample11.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample11() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample11.java");

        // Runnable #1
        LambdaExpression lambdaExpr1 = new LambdaExpression(new LambdaParameter.ParameterVariables(),
                soutprintlnHelloWorld(path, 7));
        LocalVariableStatements r1 = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("r1")
                .identifierType("Runnable")
                .finalModifier(false)
                .build(), Optional.of(lambdaExpr1), new ArrayList<>())
        );

        // Runnable #2
        LambdaExpression lambdaExpr2 = new LambdaExpression(new LambdaParameter.ParameterVariables(),
                new BlockStatement(Arrays.asList(soutprintlnHelloWorld(path, 9))));
        LocalVariableStatements r2 = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("r2")
                .identifierType("Runnable")
                .finalModifier(false)
                .build(), Optional.of(lambdaExpr2), new ArrayList<>())
        );

        // Binary Operation #1
        LambdaExpression lambdaExpr3 = new LambdaExpression(new LambdaParameter.Identifiers(Arrays.asList("a", "b")),
                new BlockStatement(Arrays.asList(soutprintlnHelloWorld(path, 12))));
        LocalVariableStatements bo1 = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("bo")
                .identifierType("BinaryOperation")
                .finalModifier(true)
                .build(), Optional.of(lambdaExpr3), new ArrayList<>())
        );

        // Binary Operation #2
        LambdaExpression lambdaExpr4 = new LambdaExpression(new LambdaParameter.ParameterVariables(Arrays.asList(
                createParameter("int", "a", false), createParameter("int", "b", false)
        )), new BlockStatement(Arrays.asList(soutprintlnHelloWorld(path, 15))));
        LocalVariableStatements bo2 = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("bo")
                .identifierType("BinaryOperation")
                .finalModifier(false)
                .build(), Optional.of(lambdaExpr4), new ArrayList<>())
        );

        // Stream API call
        ExpressionStatement streamApiCall = new ExpressionStatement(new MethodCallExpression(
                new BinaryExpression(new MethodCallExpression(
                        new BinaryExpression(new UnitExpression(IDENTIFIER, "variables"),
                                BinaryOperation.DOT, new UnitExpression(IDENTIFIER, "stream")), new ArrayList<>(), path,
                        17),
                        BinaryOperation.DOT, new UnitExpression(IDENTIFIER, "map")),
                        Arrays.asList(new UnitExpression(METHOD_REFERENCE, "SerializableCode::toPseudoCode")), path, 17)
        );

        // Main method
        ParameterVariableStatement param = createParameter("String[]", "args", false);
        MethodStatement mainMethod = new MethodStatement(MethodDescriptor.Builder.allFalse("main")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .staticModifier(true)
                .returnType("void")
                .parameters(Arrays.asList(param.getDescriptor()))
                .parameterCount(1)
                .hasParameters(true)
                .build(), Arrays.asList(param), new BlockStatement(Arrays.asList(r1, r2, bo1, bo2, streamApiCall)),
                new ArrayList<>(), 6);

        // Top-level class
        List<ImportStatement> imports = Arrays.asList(new ImportStatement("p.a", false), new ImportStatement("p.b",
                false));
        return new TopLevelTypeStatement(Optional.empty(), imports,
                new ClassStatement(ClassDescriptor.Builder.allFalse("Sample11")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build(), new BlockStatement(Arrays.asList(mainMethod)), new ArrayList<>()));
    }

    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample12.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample12() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample12.java");

        // Frequently used elements
        final ParenthesisExpression aEquals3 = createParenExpr(new BinaryExpression(identifierUnit("a"),
                BinaryOperation.EQUALS, literalUnit("3")));
        final ParenthesisExpression aNotEquals3 = createParenExpr(new BinaryExpression(identifierUnit("a"),
                BinaryOperation.NOTEQUALS, literalUnit("3")));
        final ParenthesisExpression bEquals3 = createParenExpr(new BinaryExpression(identifierUnit("b"),
                BinaryOperation.EQUALS, literalUnit("3")));
        final ParenthesisExpression aPlusBNotEqual3 = createParenExpr(new BinaryExpression(
                new BinaryExpression(identifierUnit("a"), BinaryOperation.ADD, identifierUnit("b")),
                BinaryOperation.NOTEQUALS, literalUnit("3")));
        final BinaryExpression no1Plus1 = new BinaryExpression(literalUnit("1"), BinaryOperation.ADD, literalUnit("1"));
        final BinaryExpression iLt10 = new BinaryExpression(identifierUnit("i"), BinaryOperation.LT, literalUnit("10"));
        final BinaryExpression iLtK = new BinaryExpression(identifierUnit("i"), BinaryOperation.LT,
                identifierUnit("k"));
        final BinaryExpression ipck = new BinaryExpression(identifierUnit("i"), BinaryOperation.ADD_ASSIGN,
                identifierUnit("k"));
        final ParenthesisExpression trueCond = createParenExpr(literalUnit("true"));
        final ParenthesisExpression falseCond = createParenExpr(literalUnit("false"));
        final PostfixedExpression ipp = new PostfixedExpression(identifierUnit("i"), Postfix.INC);

        // Static block
        StaticBlockStatement emptyStaticBlock = new StaticBlockStatement(BlockStatement.EMPTY);

        // Method #1
        List<Statement> b1 = new ArrayList<>();

        // If
        b1.add(new IfStatement(aEquals3, createMethodCallStatement("a", path, 14), Optional.empty()));
        b1.add(new IfStatement(aEquals3, createBlock(createMethodCallStatement("b", path, 17)),
                Optional.of(createBlock(createMethodCallStatement("c", path, 19)))));
        b1.add(new IfStatement(aEquals3, createBlock(createMethodCallStatement("a", path, 23)),
                Optional.of(new IfStatement(bEquals3, createBlock(createMethodCallStatement("b", path, 25)),
                        Optional.empty()))));
        b1.add(new IfStatement(aEquals3, createBlock(createMethodCallStatement("a", path, 29)),
                Optional.of(new IfStatement(bEquals3, createBlock(createMethodCallStatement("b", path, 31)),
                        Optional.of(createBlock(createMethodCallStatement("c", path, 33)))))));

        // While
        b1.add(new WhileStatement(trueCond, soutprintlnHelloWorld(path, 38)));
        b1.add(new WhileStatement(aPlusBNotEqual3, createBlock(soutprintlnHelloWorld(path, 41))));

        // Do-While
        b1.add(new DoWhileStatement(falseCond, createBlock(createMethodCallStatement("b", path, 46))));
        b1.add(new DoWhileStatement(aEquals3, createBlock(soutprintlnHelloWorld(path, 50))));

        // Synchronized
        b1.add(new SynchronizedStatement(createParenExpr(thisUnit()), createBlock(
                createMethodCallStatement("a", path, 55))));
        b1.add(new SynchronizedStatement(createParenExpr(createMethodCall("getLock", path, 58)),
                createBlock(soutprintlnHelloWorld(path, 59))));

        // For-loop
        LocalVariableStatement localVarI = new LocalVariableStatement(new LocalVariableDescriptor.Builder("i")
                .identifierType("int")
                .finalModifier(false)
                .build(), Optional.of(literalUnit("0")), new ArrayList<>());
        Optional<LocalVariableStatements> localVars1 = Optional.of(createLocals(localVarI));
        b1.add(new ForStatement(localVars1, new ArrayList<>(), Optional.of(iLt10), Arrays.asList(ipp),
                createBlock(createMethodCallStatement("a", path, 64))));

        LocalVariableStatement localVarK = new LocalVariableStatement(new LocalVariableDescriptor.Builder("k")
                .identifierType("int")
                .finalModifier(false)
                .build(), Optional.of(literalUnit("3")), new ArrayList<>());
        Optional<LocalVariableStatements> localVars2 = Optional.of(createLocals(localVarI, localVarK));
        b1.add(new ForStatement(localVars2, new ArrayList<>(), Optional.of(iLtK), Arrays.asList(ipck),
                BlockStatement.EMPTY));

        // For-loop with label
        b1.add(new LabelStatement("for1", new ForStatement(localVars1, new ArrayList<>(), Optional.of(iLt10),
                Arrays.asList(ipp), createBlock(new BreakStatement(Optional.of("for1"))))));

        // For-each
        b1.add(new ForEachStatement(new LocalVariableStatement(new LocalVariableDescriptor.Builder("s")
                .identifierType("String")
                .finalModifier(false)
                .build(), Optional.empty(), new ArrayList<>()), identifierUnit("list"),
                createBlock(new ExpressionStatement(new MethodCallExpression(SOUT_PRINTLN,
                        Arrays.asList(identifierUnit("s")), path, 79)))));

        b1.add(new ForEachStatement(new LocalVariableStatement(new LocalVariableDescriptor.Builder("t")
                .identifierType("Type<K>")
                .finalModifier(true)
                .build(), Optional.empty(), new ArrayList<>()), new MethodCallExpression(identifierUnit("getTypes"),
                Arrays.asList(literalUnit("3")), path, 82), createBlock(createMethodCallStatement("a", path, 83))));

        // Try
        b1.add(new TryStatement(createBlock(createMethodCallStatement("a", path, 88)), new ArrayList<>(),
                Optional.of(createBlock(createMethodCallStatement("b", path, 90)))));

        List<CatchStatement> catches1 = Arrays.asList(new CatchStatement(createLocals(
                        new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                                .identifierType("Exception")
                                .finalModifier(false)
                                .build(), Optional.empty(), new ArrayList<>())), createBlock(
                createMethodCallStatement("error", path, 96))));
        b1.add(new TryStatement(createBlock(createMethodCallStatement("a", path, 94)), catches1,
                Optional.of(createBlock(createMethodCallStatement("b", path, 98)))));

        List<CatchStatement> catches2 = Arrays.asList(new CatchStatement(createLocals(
                new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                        .identifierType("Exception")
                        .finalModifier(false)
                        .build(), Optional.empty(), new ArrayList<>()),
                new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                        .identifierType("RuntimeException")
                        .finalModifier(false)
                        .build(), Optional.empty(), new ArrayList<>())), createBlock(
                createMethodCallStatement("error", path, 104))));
        b1.add(new TryStatement(createBlock(createMethodCallStatement("a", path, 102)), catches2, Optional.empty()));

        // Try-with-resources
        b1.add(new TryWithResourcesStatement(createBlock(new ExpressionStatement(new MethodCallExpression(SOUT_PRINTLN,
                Arrays.asList(identifierUnit("s")), path, 109))), new ArrayList<>(), Optional.empty(),
                createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("s")
                        .identifierType("StringSupplier")
                        .finalModifier(false)
                        .build(), Optional.of(createMethodCall("supplier", path, 108)), new ArrayList<>()))));
        b1.add(new TryWithResourcesStatement(createBlock(new ExpressionStatement(new MethodCallExpression(SOUT_PRINTLN,
                Arrays.asList(identifierUnit("s")), path, 113))), Arrays.asList(new CatchStatement(createLocals(
                new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                        .identifierType("Exception")
                        .finalModifier(false)
                        .build(), Optional.empty(), new ArrayList<>())), createBlock(
                createMethodCallStatement("error", path, 115)))), Optional.empty(),
                createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("s")
                        .identifierType("StringSupplier")
                        .finalModifier(false)
                        .build(), Optional.of(createMethodCall("supplier", path, 112)), new ArrayList<>()))));
        b1.add(new TryWithResourcesStatement(createBlock(new ExpressionStatement(new MethodCallExpression(SOUT_PRINTLN,
                Arrays.asList(identifierUnit("s")), path, 119))), Arrays.asList(new CatchStatement(createLocals(
                new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                        .identifierType("Exception")
                        .finalModifier(false)
                        .build(), Optional.empty(), new ArrayList<>()),
                new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                        .identifierType("RuntimeException")
                        .finalModifier(false)
                        .build(), Optional.empty(), new ArrayList<>())),
                createBlock(createMethodCallStatement("error", path, 121)))), Optional.empty(),
                createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("s")
                        .identifierType("StringSupplier")
                        .finalModifier(false)
                        .build(), Optional.of(createMethodCall("supplier", path, 118)), new ArrayList<>()))));
        b1.add(new TryWithResourcesStatement(createBlock(new ExpressionStatement(new MethodCallExpression(SOUT_PRINTLN,
                Arrays.asList(identifierUnit("s")), path, 125))), Arrays.asList(new CatchStatement(createLocals(
                new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                        .identifierType("Exception")
                        .finalModifier(false)
                        .build(), Optional.empty(), new ArrayList<>()),
                new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                        .identifierType("RuntimeException")
                        .finalModifier(false)
                        .build(), Optional.empty(), new ArrayList<>())), createBlock(
                createMethodCallStatement("error", path, 127)))), Optional.of(createBlock(
                createMethodCallStatement("b", path, 129))),
                createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("s")
                        .identifierType("StringSupplier")
                        .finalModifier(false)
                        .build(), Optional.of(createMethodCall("supplier", path, 124)), new ArrayList<>()))));

        // Switch
        BlockStatement switchBlock1 = createBlock(
                new SwitchLabelStatement("\"a\""), createBlock(createMethodCallStatement("a", path, 135),
                        new BreakStatement(Optional.empty())),
                new SwitchLabelStatement("default"), createBlock(createMethodCallStatement("error", path, 138))
        );
        b1.add(new SwitchStatement(createParenExpr(identifierUnit("s")), switchBlock1));

        BlockStatement switchBlock2 = createBlock(
                new SwitchLabelStatement("\"a\""), new SwitchLabelStatement("default"), createBlock(
                        createMethodCallStatement("a", path, 144)),
                new SwitchLabelStatement(no1Plus1), new SwitchLabelStatement("\"\""), createBlock(
                        createMethodCallStatement("b", path, 147))
        );
        b1.add(new SwitchStatement(createParenExpr(new BinaryExpression(identifierUnit("x"),
                BinaryOperation.ASSIGN, new MethodCallExpression(identifierUnit("getTypes"), 
                Arrays.asList(literalUnit("3")), path, 141))
        ), switchBlock2));

        b1.add(new SwitchStatement(createParenExpr(literalUnit("3")), BlockStatement.EMPTY));

        MethodStatement method1 = new MethodStatement(MethodDescriptor.Builder.allFalse("a")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(b1), new ArrayList<>(), 11);

        // Method #2
        List<Statement> b2 = new ArrayList<>();

        // Assert
        b2.add(new AssertStatement(aNotEquals3, Optional.empty()));
        b2.add(new AssertStatement(new BinaryExpression(new ArrayAccessExpression(identifierUnit("a"),
                literalUnit("3")), BinaryOperation.NOTEQUALS, literalUnit("3")),
                Optional.of(literalUnit("\"a is 3\""))));

        // Break
        b2.add(new BreakStatement(Optional.empty()));
        b2.add(new BreakStatement(Optional.of("ident")));

        // Return
        b2.add(new ReturnStatement(Optional.empty()));
        b2.add(new ReturnStatement(Optional.of(new TernaryExpression(identifierUnit("a"),
                        new BinaryExpression(createParenExpr(literalUnit("1_000")), BinaryOperation.MUL,
                                literalUnit("1d")), literalUnit("300"))
        )));

        // Throw
        ClassDescriptor st = ClassDescriptor.Builder.allFalse("RuntimeException")
                .local(true)
                .build();
        b2.add(new ThrowStatement(new InstantiateClassExpression(st, Optional.empty(),
                Arrays.asList(literalUnit("\"rtex\"")), new ArrayList<>(), false)));

        // Post-fixed
        b2.add(createPostfixedStatement("i", Postfix.INC));
        b2.add(createPostfixedStatement("i", Postfix.DEC));

        // Pre-fixed
        b2.add(createPrefixedStatement("i", Prefix.ADD));
        b2.add(createPrefixedStatement("i", Prefix.SUB));
        b2.add(createPrefixedStatement("i", Prefix.INC));
        b2.add(createPrefixedStatement("i", Prefix.DEC));
        b2.add(createPrefixedStatement("i", Prefix.TILDE));
        b2.add(createPrefixedStatement("i", Prefix.BANG));

        // Array expression
        LocalVariableStatement localArrayVar1 = new LocalVariableStatement(new LocalVariableDescriptor.Builder("a")
                .identifierType("int[]")
                .finalModifier(true)
                .build(),
                Optional.of(new ArrayExpression(Arrays.asList(literalUnit("1"), literalUnit("\"a\""),
                        createMethodCall("supplier", path, 185)))),
                new ArrayList<>());
        b2.add(createLocals(localArrayVar1));

        // Array definition
        LocalVariableStatement localArrayVar2 = new LocalVariableStatement(new LocalVariableDescriptor.Builder("a")
                .identifierType("int[]")
                .finalModifier(false)
                .build(),
                Optional.of(new ArrayInitializationExpression(Arrays.asList(
                        new SquareBracketsExpression(Optional.of(literalUnit("3")))
                ))),
                new ArrayList<>());
        b2.add(createLocals(localArrayVar2));

        LocalVariableStatement localArrayVar3 = new LocalVariableStatement(new LocalVariableDescriptor.Builder("a")
                .identifierType("int[]")
                .finalModifier(false)
                .build(),
                Optional.of(new ArrayInitializationExpression(Arrays.asList(
                        new SquareBracketsExpression(Optional.of(literalUnit("3"))),
                        new SquareBracketsExpression()
                ))),
                new ArrayList<>());
        b2.add(createLocals(localArrayVar3));

        MethodStatement method2 = new MethodStatement(MethodDescriptor.Builder.allFalse("b")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(b2), new ArrayList<>(), 155);

        // Top-level class
        BlockStatement classBlock = new BlockStatement(Arrays.asList(
                new StaticBlockStatement(createBlock(soutprintlnHelloWorld(path, 4))), emptyStaticBlock, method1,
                method2));
        return createTopLevelStatement(new ClassStatement(ClassDescriptor.Builder.allFalse("Sample12")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .build(), classBlock, new ArrayList<>()));
    }


    /**
     * A <tt>TypeStatement</tt> representing the contents of 'Sample13.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static TypeStatement sample13() {
        // Inner class instantiation's method
        MethodStatement runMethod = new MethodStatement(MethodDescriptor.Builder.allFalse("run")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(),
                new ArrayList<>(), BlockStatement.EMPTY, new ArrayList<>(), 5);

        // Inner class instantiation
        ExpressionStatement innerClassInstantiation = new ExpressionStatement(new InstantiateClassExpression(
                new ClassDescriptor(
                        new IdentifierName.Static("Worker"), Optional.empty(), Optional.of(false), Optional.of(false),
                        Optional.of(false), Optional.of(false), Optional.of(false), Optional.of(false),
                        Optional.of(true), Optional.of(false), new ArrayList<>(), new ArrayList<>(), Optional.empty()
                ),
                Optional.of(new BlockStatement(Arrays.asList(runMethod))), new ArrayList<>(), new ArrayList<>(), false
        ));

        // Method
        MethodStatement aMethod = new MethodStatement(MethodDescriptor.Builder.allFalse("a")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(),
                new ArrayList<>(), new BlockStatement(Arrays.asList(innerClassInstantiation)), new ArrayList<>(), 3);

        // Class
        ClassStatement clazz = new ClassStatement(ClassDescriptor.Builder.allFalse("Sample13")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE).build(),
                new BlockStatement(Arrays.asList(aMethod)), new ArrayList<>());
        return new TopLevelTypeStatement(Optional.empty(), new ArrayList<>(), clazz);
    }

    @Parameterized.Parameters(name = "{index}: \"{1}\"")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {sample1(), "Sample1.java"},
                {sample2(), "Sample2.java"},
                {sample3(), "Sample3.java"},
                {sample4(), "Sample4.java"},
                {sample5(), "Sample5.java"},
                {sample6(), "Sample6.java"},
                {sample7(), "Sample7.java"},
                {sample8(), "Sample8.java"},
                {sample9(), "Sample9.java"},
                {sample10(), "Sample10.java"},
                {sample11(), "Sample11.java"},
                {sample12(), "Sample12.java"},
                {sample13(), "Sample13.java"}
        });
    }

    private static BlockStatement createBlock(Statement... block) {
        return new BlockStatement(Arrays.asList(block));
    }

    private static UnitExpression thisUnit() {
        return new UnitExpression(THIS, "this");
    }

    private static UnitExpression identifierUnit(String identifier) {
        return new UnitExpression(IDENTIFIER, identifier);
    }

    private static UnitExpression literalUnit(String value) {
        return new UnitExpression(LITERAL, value);
    }

    private static MethodStatement createMethod(MethodDescriptor desc, int lineNumber) {
        return new MethodStatement(desc, new ArrayList<>(), BlockStatement.EMPTY, new ArrayList<>(), lineNumber);
    }

    private static ClassStatement createClass(ClassDescriptor desc) {
        return new ClassStatement(desc, BlockStatement.EMPTY, new ArrayList<>());
    }

    private static ParameterVariableStatement createParameter(String type, String identifier, boolean finalModifier) {
        return new ParameterVariableStatement(new ParameterVariableDescriptor(new IdentifierName.Static(identifier),
                type, finalModifier), new ArrayList<>());
    }

    private static LocalVariableStatements createLocals(LocalVariableStatement... locals) {
        return new LocalVariableStatements(Arrays.asList(locals));
    }

    private static EnumConstantStatement createEnumConstant(String identifierName) {
        return new EnumConstantStatement(identifierName, new ArrayList<>(), Optional.empty(), new ArrayList<>());
    }

    private static EnumConstantStatement createEnumConstant(String identifierName, List<Expression> expressions) {
        return new EnumConstantStatement(identifierName, expressions, Optional.empty(), new ArrayList<>());
    }

    private static EnumConstantStatement createEnumConstant(String identifierName, BlockStatement block) {
        return new EnumConstantStatement(identifierName, new ArrayList<>(), Optional.of(block), new ArrayList<>());
    }

    private static TopLevelTypeStatement createTopLevelStatement(TypeStatement typeStatement) {
        return new TopLevelTypeStatement(Optional.empty(), new ArrayList<>(), typeStatement);
    }

    private static ExpressionStatement soutprintlnHelloWorld(Path path, int lineNumber) {
        return new ExpressionStatement(new MethodCallExpression(SOUT_PRINTLN,
                Arrays.asList(literalUnit("\"Hello World\"")), path, lineNumber));
    }

    private static ExpressionStatement createMethodCallStatement(String identifier, Path path, int lineNumber) {
        return new ExpressionStatement(new MethodCallExpression(identifierUnit(identifier), path, lineNumber));
    }

    private static Expression createMethodCall(String identifier, Path path, int lineNumber) {
        return new MethodCallExpression(identifierUnit(identifier), path, lineNumber);
    }
    
    private static ParenthesisExpression createParenExpr(Expression e) {
        return new ParenthesisExpression(e);
    }

    private static Annotation.ExpressionValue createAnonExprValue(String identifierName, String literalValue) {
        return new Annotation.ExpressionValue(identifierName, literalUnit(literalValue));
    }

    private static ExpressionStatement createPrefixedStatement(String identifierName, Prefix prefix) {
        return new ExpressionStatement(new PrefixedExpression(identifierUnit(identifierName), prefix));
    }

    private static ExpressionStatement createPostfixedStatement(String identifierName, Postfix postfix) {
        return new ExpressionStatement(new PostfixedExpression(identifierUnit(identifierName), postfix));
    }

    @Test
    public void testValidJavaCode() throws IOException {
        Path sampleFile = Paths.get(SAMPLES_DIRECTORY, sampleFileName);
        assertEquals(expected, CodeParserFactory.create(sampleFile).parse(sampleFile));
    }
}
