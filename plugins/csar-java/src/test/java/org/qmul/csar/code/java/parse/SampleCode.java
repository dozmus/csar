package org.qmul.csar.code.java.parse;

import org.qmul.csar.code.java.parse.expression.*;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptors.*;
import org.qmul.csar.util.FilePosition;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public final class SampleCode {

    /**
     * Directory of the java code files.
     */
    public static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/code/";
    
    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample1.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample1() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample1.java");

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
                .build(), Arrays.asList(param21, param22), BlockStatement.EMPTY, new ArrayList<>(),
                new FilePosition(16, 295), new FilePosition(16, 298), new FilePosition(16, 319),
                Arrays.asList(new FilePosition(16, 312)), path);

        // Method #2
        ReturnStatement returnSt = new ReturnStatement(
                new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "result"));
        MethodStatement method2 = new MethodStatement(MethodDescriptor.Builder.allFalse("getResult")
                .visibilityModifier(VisibilityModifier.PROTECTED)
                .finalModifier(true)
                .returnType("int")
                .parameterCount(0)
                .hasParameters(false)
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(returnSt)), new ArrayList<>(),
                new FilePosition(18, 349), new FilePosition(18, 358), new FilePosition(18, 359),
                Collections.emptyList(), path);

        // Method #3
        LocalVariableStatements local = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("k")
                .finalModifier(true)
                .identifierType("int")
                .build(),
                Optional.of(literalUnit("3")), new ArrayList<>()));
        UnitExpression resultIdentifier = new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "result");
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
                new FilePosition(22, 439), new FilePosition(22, 448), new FilePosition(22, 459),
                Collections.emptyList(), path);

        // Top-level class
        ClassStatement clazz = new ClassStatement(ClassDescriptor.Builder.allFalse("Sample1")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .strictfpModifier(true)
                .abstractModifier(true)
                .extendedClass("AbstractSample")
                .build(),
                new BlockStatement(Arrays.asList(constructor1, constructor2, var1, var2, method1, method2, method3)),
                new ArrayList<>());
        return new CompilationUnitStatement(Optional.of(new PackageStatement("grammars.java8", new ArrayList<>())),
                new ArrayList<>(), clazz);
    }

    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample2.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample2() {
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
        Expression methodName = systemOutPrintln();
        List<Expression> arguments = Arrays.asList(new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "s"));
        ExpressionStatement methodCall1 = new ExpressionStatement(new MethodCallExpression("println", methodName,
                arguments, path, new FilePosition(8, 183), new FilePosition(8, 190), new FilePosition(8, 192)));

        ParameterVariableStatement param1 = createParameter("String", "s", false);
        MethodStatement method1 = new MethodStatement(MethodDescriptor.Builder.allFalse("print")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("void")
                .defaultModifier(true)
                .parameters(Arrays.asList(param1.getDescriptor()))
                .parameterCount(1)
                .hasParameters(true)
                .build(), Arrays.asList(param1), new BlockStatement(Arrays.asList(methodCall1)), new ArrayList<>(),
                new FilePosition(7, 145), new FilePosition(7, 150), new FilePosition(7, 159),
                Collections.emptyList(), path);

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
                .build(), Arrays.asList(param21, param22), BlockStatement.EMPTY, new ArrayList<>(),
                new FilePosition(11, 218), new FilePosition(11, 223), new FilePosition(11, 244),
                Arrays.asList(new FilePosition(11, 231)), path);

        // Instance #1
        Expression methodName2 = new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "generateName");
        List<Expression> methodArgs = Arrays.asList(new UnitExpression(UnitExpression.ValueType.CLASS_REFERENCE,
                "Sample2.class"));
        InstanceVariableStatement var2 = new InstanceVariableStatement(new InstanceVariableDescriptor.Builder("name")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .identifierType("String[]")
                .staticModifier(false)
                .finalModifier(false)
                .build(),
                new ArrayList<>(), Optional.of(new MethodCallExpression("generateName", methodName2, methodArgs, path,
                new FilePosition(13, 270), new FilePosition(13, 282), new FilePosition(13, 296))));

        // Method #3
        ParameterVariableStatement param31 = createParameter("String[]", "$", false);
        MethodStatement method3 = new MethodStatement(MethodDescriptor.Builder.allFalse("print")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(Arrays.asList(param31.getDescriptor()))
                .parameterCount(1)
                .hasParameters(true)
                .stub(true)
                .build(), Arrays.asList(param31), BlockStatement.EMPTY, new ArrayList<>(),
                new FilePosition(15, 311), new FilePosition(15, 316), new FilePosition(15, 327),
                Collections.emptyList(), path);

        // Top-level class
        BlockStatement block = new BlockStatement(Arrays.asList(var1, method1, method2, var2, method3));
        List<ImportStatement> imports = Arrays.asList(new ImportStatement("java.lang.Runnable", false));
        return new CompilationUnitStatement(Optional.empty(), imports,
                new ClassStatement(ClassDescriptor.Builder.allFalse("Sample2")
                        .visibilityModifier(VisibilityModifier.PUBLIC)
                        .interfaceModifier(true)
                        .implementedInterfaces(Arrays.asList("Runnable"))
                        .build(), block, new ArrayList<>()));
    }

    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample3.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample3() {
        List<ImportStatement> imports = Arrays.asList(new ImportStatement("a.Enum", true));
        return new CompilationUnitStatement(Optional.empty(), imports,
                createClass(ClassDescriptor.Builder.allFalse("Sample3")
                        .visibilityModifier(VisibilityModifier.PUBLIC)
                        .typeParameters(Arrays.asList("T0 extends Collection<String>", "T1"))
                        .build()));
    }

    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample4.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample4() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample4.java");

        // Local interface
        MethodStatement interfaceMethod = createMethod(MethodDescriptor.Builder.allFalse("run")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .stub(true)
                .parameterCount(0)
                .build(), 5, Collections.emptyList(), path, 92, 95, 96);
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

        List<Expression> arguments = Arrays.asList(new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "x"));
        ExpressionStatement methodCall1 = new ExpressionStatement(new MethodCallExpression("println",
                systemOutPrintln(), arguments, path, new FilePosition(11, 241), new FilePosition(11, 248),
                new FilePosition(11, 250)));

        MethodStatement innerClassMethod = new MethodStatement(MethodDescriptor.Builder.allFalse("run")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("void")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(locals, methodCall1)), new ArrayList<>(),
                new FilePosition(9, 176), new FilePosition(9, 179), new FilePosition(9, 180),
                Collections.emptyList(), path);
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
        ExpressionStatement methodCall2 = new ExpressionStatement(new MethodCallExpression("run",
                new BinaryExpression(new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "worker"),
                        BinaryOperation.DOT, new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "run")),
                new ArrayList<>(), path, new FilePosition(15, 324), new FilePosition(15, 327),
                new FilePosition(15, 328), Collections.emptyList()));

        // Method #1
        MethodStatement parentClassMethod1 = new MethodStatement(MethodDescriptor.Builder.allFalse("work")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(localInterface, localClass, locals2,
                methodCall2)), new ArrayList<>(), new FilePosition(3, 35), new FilePosition(3, 39),
                new FilePosition(3, 40), Collections.emptyList(), path);

        // Method #2
        MethodStatement parentClassMethod2 = new MethodStatement(MethodDescriptor.Builder.allFalse("work2")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .thrownExceptions(Arrays.asList("IOException"))
                .hasThrownExceptions(true)
                .build(),
                new ArrayList<>(), BlockStatement.EMPTY, new ArrayList<>(), new FilePosition(18, 350),
                new FilePosition(18, 355), new FilePosition(18, 356), Collections.emptyList(), path);

        // Parent class
        return createTopLevelStatement(new ClassStatement(ClassDescriptor.Builder.allFalse("Sample4")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build(), new BlockStatement(Arrays.asList(parentClassMethod1, parentClassMethod2)),
                new ArrayList<>()));
    }

    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample5.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample5() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample5.java");

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
                .build(), params, BlockStatement.EMPTY, new ArrayList<>(), new FilePosition(9, 106),
                new FilePosition(9, 110), new FilePosition(9, 122), Collections.emptyList(), path);
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
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample6.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample6() {
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
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample7.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample7() {
        // Constants
        EnumConstantStatement winter = createEnumConstant("WINTER");
        EnumConstantStatement summer = createEnumConstant("SUMMER");

        // Top-level enum
        BlockStatement block = new BlockStatement(Arrays.asList(winter, summer));
        EnumDescriptor desc = EnumDescriptor.Builder.allFalse("Season")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build();
        return createTopLevelStatement(new EnumStatement(desc, block, new ArrayList<>()));
    }

    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample8.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample8() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample8.java");

        UnitExpression identA = new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "a");
        UnitExpression identB = new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "b");

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
        ReturnStatement return1 = new ReturnStatement(new BinaryExpression(identA, BinaryOperation.ADD, identB));
        MethodStatement apply1 = new MethodStatement(interfaceMethodDesc, Arrays.asList(param1, param2),
                new BlockStatement(Arrays.asList(return1)), new ArrayList<>(), new FilePosition(3, 81),
                new FilePosition(3, 86), new FilePosition(3, 107), Arrays.asList(new FilePosition(3, 96)),
                path);
        EnumConstantStatement const1 = createEnumConstant("PLUS", new BlockStatement(Arrays.asList(apply1)));

        ReturnStatement return2 = new ReturnStatement(new BinaryExpression(identA, BinaryOperation.SUB, identB));
        MethodStatement apply2 = new MethodStatement(interfaceMethodDesc, Arrays.asList(param1, param2),
                new BlockStatement(Arrays.asList(return2)), new ArrayList<>(), new FilePosition(8, 187),
                new FilePosition(8, 192), new FilePosition(8, 213), Arrays.asList(new FilePosition(8, 202)),
                path);
        EnumConstantStatement const2 = createEnumConstant("MINUS", new BlockStatement(Arrays.asList(apply2)));

        // Top-level enum
        EnumDescriptor desc = EnumDescriptor.Builder.allFalse("SimpleOperators")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .hasSuperClasses(true)
                .superClasses(Arrays.asList("Operator<Integer>"))
                .build();
        return createTopLevelStatement(new EnumStatement(desc, new BlockStatement(Arrays.asList(const1, const2)),
                new ArrayList<>()));
    }

    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample9.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample9() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample9.java");

        // Constants
        EnumConstantStatement const1 = createEnumConstant("USD", Arrays.asList(literalUnit("1.10")));
        EnumConstantStatement const2 = createEnumConstant("GBP", Arrays.asList(literalUnit("1")));

        // Body
        UnitExpression valueIdent = new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "value");

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
                .build(), new ArrayList<>(), new BlockStatement(Arrays.asList(returnSt)), new ArrayList<>(),
                new FilePosition(11, 184), new FilePosition(11, 192), new FilePosition(11, 193),
                Collections.emptyList(), path
        );

        // Top-level enum
        BlockStatement block = new BlockStatement(Arrays.asList(const1, const2, variable, constructor, method));
        EnumDescriptor desc = EnumDescriptor.Builder.allFalse("Currency")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build();
        return createTopLevelStatement(new EnumStatement(desc, block, new ArrayList<>()));
    }

    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample10.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample10() {
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
        return createTopLevelStatement(new AnnotationStatement(AnnotationDescriptor.Builder.allFalse("FileChange")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build(), new BlockStatement(Arrays.asList(author, date)), Arrays.asList(apiClassAnnotation)));
    }

    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample11.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample11() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample11.java");

        // Runnable #1
        LambdaExpression lambdaExpr1 = new LambdaExpression(new LambdaParameter.ParameterVariables(),
                printlnHelloWorld(path, 7, 140, 147, 161));
        LocalVariableStatements r1 = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("r1")
                .identifierType("Runnable")
                .finalModifier(false)
                .build(), Optional.of(lambdaExpr1), new ArrayList<>())
        );

        // Runnable #2
        LambdaExpression lambdaExpr2 = new LambdaExpression(new LambdaParameter.ParameterVariables(),
                new BlockStatement(Arrays.asList(printlnHelloWorld(path, 9, 219, 226, 240))));
        LocalVariableStatements r2 = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("r2")
                .identifierType("Runnable")
                .finalModifier(false)
                .build(), Optional.of(lambdaExpr2), new ArrayList<>())
        );

        // Binary Operation #1
        LambdaExpression lambdaExpr3 = new LambdaExpression(new LambdaParameter.Identifiers(Arrays.asList("a", "b")),
                new BlockStatement(Arrays.asList(printlnHelloWorld(path, 12, 327, 334, 348))));
        LocalVariableStatements bo1 = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("bo")
                .identifierType("BinaryOperation")
                .finalModifier(true)
                .build(), Optional.of(lambdaExpr3), new ArrayList<>())
        );

        // Binary Operation #2
        LambdaExpression lambdaExpr4 = new LambdaExpression(new LambdaParameter.ParameterVariables(Arrays.asList(
                createParameter("int", "a", false), createParameter("int", "b", false)
        )), new BlockStatement(Arrays.asList(printlnHelloWorld(path, 15, 437, 444, 458))));
        LocalVariableStatements bo2 = createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("bo")
                .identifierType("BinaryOperation")
                .finalModifier(false)
                .build(), Optional.of(lambdaExpr4), new ArrayList<>())
        );

        // Stream API call
        BinaryExpression bexp = new BinaryExpression(
                new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "variables"), BinaryOperation.DOT,
                new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "stream"));

        ExpressionStatement streamApiCall = new ExpressionStatement(new MethodCallExpression("map",
                new BinaryExpression(new MethodCallExpression("stream", bexp, new ArrayList<>(), path,
                        new FilePosition(17, 492), new FilePosition(17, 498), new FilePosition(17, 499),
                        Collections.emptyList()),
                        BinaryOperation.DOT, new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "map")),
                Arrays.asList(new MethodReferenceExpression(
                        new UnitExpression(UnitExpression.ValueType.IDENTIFIER, "SerializableCode"), "toPseudoCode",
                        Optional.empty())
                ), path, new FilePosition(17, 501), new FilePosition(17, 504), new FilePosition(17, 535),
                Collections.emptyList())
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
                new ArrayList<>(), new FilePosition(6, 78), new FilePosition(6, 82),
                new FilePosition(6, 96), Collections.emptyList(),
                path);

        // Top-level class
        List<ImportStatement> imports = Arrays.asList(new ImportStatement("p.a", false), new ImportStatement("p.b",
                false));
        return new CompilationUnitStatement(Optional.empty(), imports,
                new ClassStatement(ClassDescriptor.Builder.allFalse("Sample11")
                        .visibilityModifier(VisibilityModifier.PUBLIC)
                        .build(), new BlockStatement(Arrays.asList(mainMethod)), new ArrayList<>()));
    }

    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample12.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample12() {
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
        b1.add(new IfStatement(aEquals3, createMethodCallStatement("a", path, 14, 176, 177, 178),
                Optional.empty()));
        b1.add(new IfStatement(aEquals3, createBlock(createMethodCallStatement("b", path, 17, 219, 220, 221)),
                Optional.of(createBlock(createMethodCallStatement("c", path, 19, 255, 256, 257)))));
        b1.add(new IfStatement(aEquals3, createBlock(createMethodCallStatement("a", path, 23, 309, 310, 311)),
                Optional.of(new IfStatement(bEquals3, createBlock(createMethodCallStatement("b", path, 25, 357,
                        358, 359)), Optional.empty()))));
        b1.add(new IfStatement(aEquals3, createBlock(createMethodCallStatement("a", path, 29, 411, 412, 413)),
                Optional.of(new IfStatement(bEquals3, createBlock(createMethodCallStatement("b", path, 31, 459,
                        460, 461)),
                        Optional.of(createBlock(createMethodCallStatement("c", path, 33, 495, 496, 497)))))));

        // While
        b1.add(new WhileStatement(trueCond, printlnHelloWorld(path, 38, 577, 584, 598)));
        b1.add(new WhileStatement(aPlusBNotEqual3, createBlock(printlnHelloWorld(path, 41, 657, 664, 678))));

        // Do-While
        b1.add(new DoWhileStatement(falseCond, createBlock(createMethodCallStatement("b", path, 46, 742, 743, 744))));
        b1.add(new DoWhileStatement(aEquals3, createBlock(printlnHelloWorld(path, 50, 813, 820, 834))));

        // Synchronized
        b1.add(new SynchronizedStatement(createParenExpr(thisUnit()),
                createBlock(createMethodCallStatement("a", path, 55, 935, 936, 937))));
        b1.add(new SynchronizedStatement(createParenExpr(createMethodCall("getLock", path, 58, 976, 983, 984)),
                createBlock(printlnHelloWorld(path, 59, 1013, 1020, 1034))));

        // For-loop
        LocalVariableStatement localVarI = new LocalVariableStatement(new LocalVariableDescriptor.Builder("i")
                .identifierType("int")
                .finalModifier(false)
                .build(), Optional.of(literalUnit("0")), new ArrayList<>());
        Optional<LocalVariableStatements> localVars1 = Optional.of(createLocals(localVarI));
        b1.add(new ForStatement(localVars1, new ArrayList<>(), Optional.of(iLt10), Arrays.asList(ipp),
                createBlock(createMethodCallStatement("a", path, 64, 1124, 1125, 1126))));

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
                createBlock(new ExpressionStatement(new MethodCallExpression("println", systemOutPrintln(),
                        Arrays.asList(identifierUnit("s")), path, new FilePosition(79, 1410),
                        new FilePosition(79, 1417), new FilePosition(79, 1419))))));

        b1.add(new ForEachStatement(new LocalVariableStatement(new LocalVariableDescriptor.Builder("t")
                .identifierType("Type<K>")
                .finalModifier(true)
                .build(), Optional.empty(), new ArrayList<>()), new MethodCallExpression(identifierUnit("getTypes"),
                Arrays.asList(literalUnit("3")), path, new FilePosition(82, 1467), new FilePosition(82, 1475),
                new FilePosition(82, 1477), Collections.emptyList()),
                createBlock(createMethodCallStatement("a", path, 83, 1495, 1496, 1497))));

        // Try
        b1.add(new TryStatement(createBlock(createMethodCallStatement("a", path, 88, 1563, 1564, 1565)),
                new ArrayList<>(),
                Optional.of(createBlock(createMethodCallStatement("b", path, 90, 1602, 1603, 1604)))));

        List<CatchStatement> catches1 = Arrays.asList(new CatchStatement(createLocals(
                new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                        .identifierType("Exception")
                        .finalModifier(false)
                        .build(), Optional.empty(), new ArrayList<>())), createBlock(
                createMethodCallStatement("error", path, 96, 1699, 1704, 1705))));
        b1.add(new TryStatement(createBlock(createMethodCallStatement("a", path, 94, 1648, 1649, 1650)), catches1,
                Optional.of(createBlock(createMethodCallStatement("b", path, 98, 1742, 1743, 1744)))));

        List<CatchStatement> catches2 = Arrays.asList(new CatchStatement(createLocals(
                new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                        .identifierType("Exception")
                        .finalModifier(false)
                        .build(), Optional.empty(), new ArrayList<>()),
                new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                        .identifierType("RuntimeException")
                        .finalModifier(false)
                        .build(), Optional.empty(), new ArrayList<>())), createBlock(
                createMethodCallStatement("error", path, 104, 1858, 1863, 1864))));
        b1.add(new TryStatement(createBlock(createMethodCallStatement("a", path, 102, 1788, 1789, 1790)),
                catches2, Optional.empty()));

        // Try-with-resources
        b1.add(new TryWithResourcesStatement(createBlock(new ExpressionStatement(new MethodCallExpression("println",
                systemOutPrintln(), Arrays.asList(identifierUnit("s")), path, new FilePosition(109, 1982),
                new FilePosition(109, 1989), new FilePosition(109, 1991),
                Collections.emptyList()))), new ArrayList<>(), Optional.empty(),
                createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("s")
                        .identifierType("StringSupplier")
                        .finalModifier(false)
                        .build(), Optional.of(createMethodCall("supplier", path, 108, 1944, 1952, 1953)),
                        new ArrayList<>()))));
        b1.add(new TryWithResourcesStatement(createBlock(new ExpressionStatement(new MethodCallExpression("println",
                systemOutPrintln(), Arrays.asList(identifierUnit("s")), path, new FilePosition(113, 2078),
                new FilePosition(113, 2085), new FilePosition(113, 2087), Collections.emptyList()))),
                Arrays.asList(new CatchStatement(createLocals(new LocalVariableStatement(
                        new LocalVariableDescriptor.Builder("e")
                                .identifierType("Exception")
                                .finalModifier(false)
                                .build(), Optional.empty(), new ArrayList<>())),
                        createBlock(createMethodCallStatement("error", path, 115, 2136, 2141, 2142)))),
                Optional.empty(), createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("s")
                .identifierType("StringSupplier")
                .finalModifier(false)
                .build(), Optional.of(createMethodCall("supplier", path, 112, 2040, 2048, 2049)),
                new ArrayList<>()))));
        b1.add(new TryWithResourcesStatement(createBlock(new ExpressionStatement(new MethodCallExpression("println",
                systemOutPrintln(), Arrays.asList(identifierUnit("s")), path, new FilePosition(119, 2229),
                new FilePosition(119, 2236), new FilePosition(119, 2238)))),
                Arrays.asList(new CatchStatement(createLocals(
                        new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                                .identifierType("Exception")
                                .finalModifier(false)
                                .build(), Optional.empty(), new ArrayList<>()),
                        new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                                .identifierType("RuntimeException")
                                .finalModifier(false)
                                .build(), Optional.empty(), new ArrayList<>())),
                        createBlock(createMethodCallStatement("error", path, 121, 2306, 2311, 2312)))),
                Optional.empty(),
                createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("s")
                        .identifierType("StringSupplier")
                        .finalModifier(false)
                        .build(), Optional.of(createMethodCall("supplier", path, 118, 2191, 2199, 2200)),
                        new ArrayList<>()))));
        b1.add(new TryWithResourcesStatement(createBlock(new ExpressionStatement(new MethodCallExpression("println",
                systemOutPrintln(), Arrays.asList(identifierUnit("s")), path, new FilePosition(125, 2399),
                new FilePosition(125, 2406), new FilePosition(125, 2408)))),
                Arrays.asList(new CatchStatement(createLocals(
                        new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                                .identifierType("Exception")
                                .finalModifier(false)
                                .build(), Optional.empty(), new ArrayList<>()),
                        new LocalVariableStatement(new LocalVariableDescriptor.Builder("e")
                                .identifierType("RuntimeException")
                                .finalModifier(false)
                                .build(), Optional.empty(), new ArrayList<>())), createBlock(
                        createMethodCallStatement("error", path, 127, 2476, 2481, 2482)))),
                Optional.of(createBlock(createMethodCallStatement("b", path, 129, 2519, 2520, 2521))),
                createLocals(new LocalVariableStatement(new LocalVariableDescriptor.Builder("s")
                        .identifierType("StringSupplier")
                        .finalModifier(false)
                        .build(), Optional.of(createMethodCall("supplier", path, 124, 2361, 2369, 2370)),
                        new ArrayList<>()))));

        // Switch
        BlockStatement switchBlock1 = createBlock(
                new SwitchLabelStatement("\"a\""),
                createBlock(createMethodCallStatement("a", path, 135, 2618, 2619, 2620),
                        new BreakStatement(Optional.empty())),
                new SwitchLabelStatement("default"),
                createBlock(createMethodCallStatement("error", path, 138, 2686, 2691, 2692))
        );
        b1.add(new SwitchStatement(createParenExpr(identifierUnit("s")), switchBlock1));

        BlockStatement switchBlock2 = createBlock(
                new SwitchLabelStatement("\"a\""), new SwitchLabelStatement("default"),
                createBlock(createMethodCallStatement("a", path, 144, 2806, 2807, 2808)),
                new SwitchLabelStatement(no1Plus1), new SwitchLabelStatement("\"\""),
                createBlock(createMethodCallStatement("b", path, 147, 2875, 2876, 2877))
        );
        b1.add(new SwitchStatement(createParenExpr(new BinaryExpression(identifierUnit("x"),
                BinaryOperation.ASSIGN, new MethodCallExpression(identifierUnit("getTypes"),
                Arrays.asList(literalUnit("3")), path, new FilePosition(141, 2729),
                new FilePosition(141, 2737), new FilePosition(141, 2739), Collections.emptyList()))
        ), switchBlock2));

        b1.add(new SwitchStatement(createParenExpr(literalUnit("3")), BlockStatement.EMPTY));

        MethodStatement method1 = new MethodStatement(MethodDescriptor.Builder.allFalse("a")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(b1), new ArrayList<>(), new FilePosition(11, 121),
                new FilePosition(11, 122), new FilePosition(11, 123), Collections.emptyList(), path);

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
        ClassDescriptor st = ClassDescriptor.Builder.allFalse("RuntimeException").local(true).build();
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
                        createMethodCall("supplier", path, 185, 3477, 3485, 3486)))),
                new ArrayList<>());
        b2.add(createLocals(localArrayVar1));

        // Array definition
        LocalVariableStatement localArrayVar2 = new LocalVariableStatement(new LocalVariableDescriptor.Builder("a")
                .identifierType("int[]")
                .finalModifier(false)
                .build(),
                Optional.of(new ArrayInitializationExpression("int", Arrays.asList(
                        new SquareBracketsExpression(Optional.of(literalUnit("3")))
                ))),
                new ArrayList<>());
        b2.add(createLocals(localArrayVar2));

        LocalVariableStatement localArrayVar3 = new LocalVariableStatement(new LocalVariableDescriptor.Builder("a")
                .identifierType("int[]")
                .finalModifier(false)
                .build(),
                Optional.of(new ArrayInitializationExpression("int", Arrays.asList(
                        new SquareBracketsExpression(Optional.of(literalUnit("3"))),
                        new SquareBracketsExpression()
                ))),
                new ArrayList<>());
        b2.add(createLocals(localArrayVar3));

        MethodStatement method2 = new MethodStatement(MethodDescriptor.Builder.allFalse("b")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(), new ArrayList<>(), new BlockStatement(b2), new ArrayList<>(), new FilePosition(155, 2947),
                new FilePosition(155, 2948), new FilePosition(155, 2949), Collections.emptyList(), path);

        // Top-level class
        BlockStatement classBlock = new BlockStatement(
                Arrays.asList(new StaticBlockStatement(createBlock(printlnHelloWorld(path, 4, 53, 60, 74))),
                        emptyStaticBlock, method1, method2)
        );
        return createTopLevelStatement(new ClassStatement(ClassDescriptor.Builder.allFalse("Sample12")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .build(), classBlock, new ArrayList<>()));
    }


    /**
     * Returns a <tt>TypeStatement</tt> representing the contents of 'Sample13.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     */
    public static TypeStatement sample13() {
        Path path = Paths.get(SAMPLES_DIRECTORY, "Sample13.java");

        // Inner class instantiation's method
        MethodStatement runMethod = new MethodStatement(MethodDescriptor.Builder.allFalse("run")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(),
                new ArrayList<>(), BlockStatement.EMPTY, new ArrayList<>(), new FilePosition(5, 77),
                new FilePosition(5, 80), new FilePosition(5, 81), Collections.emptyList(), path);

        // Inner class instantiation
        ExpressionStatement innerClassInstantiation = new ExpressionStatement(new InstantiateClassExpression(
                ClassDescriptor.Builder.allFalse("Worker").local(true).build(),
                Optional.of(new BlockStatement(Arrays.asList(runMethod))), new ArrayList<>(), new ArrayList<>(), false
        ));

        // Method
        MethodStatement aMethod = new MethodStatement(MethodDescriptor.Builder.allFalse("a")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameterCount(0)
                .build(),
                new ArrayList<>(), new BlockStatement(Arrays.asList(innerClassInstantiation)), new ArrayList<>(),
                new FilePosition(3, 29), new FilePosition(3, 30), new FilePosition(3, 31),
                Collections.emptyList(), path);

        // Class
        ClassStatement clazz = new ClassStatement(ClassDescriptor.Builder.allFalse("Sample13")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE).build(),
                new BlockStatement(Arrays.asList(aMethod)), new ArrayList<>());
        return new CompilationUnitStatement(Optional.empty(), new ArrayList<>(), clazz);
    }

    private static BlockStatement createBlock(Statement... block) {
        return new BlockStatement(Arrays.asList(block));
    }

    private static UnitExpression thisUnit() {
        return new UnitExpression(UnitExpression.ValueType.THIS, "this");
    }

    private static UnitExpression identifierUnit(String identifier) {
        return new UnitExpression(UnitExpression.ValueType.IDENTIFIER, identifier);
    }

    private static UnitExpression literalUnit(String value) {
        return new UnitExpression(UnitExpression.ValueType.LITERAL, value);
    }

    private static MethodStatement createMethod(MethodDescriptor desc, int lineNumber,
            List<FilePosition> commaFilePositions, Path path, int identifierOffset, int lParenOffset,
            int rParenOffset) {
        return new MethodStatement(desc, new ArrayList<>(), BlockStatement.EMPTY, new ArrayList<>(),
                new FilePosition(lineNumber, identifierOffset),
                new FilePosition(lineNumber, lParenOffset),
                new FilePosition(lineNumber, rParenOffset), commaFilePositions, path);
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

    private static CompilationUnitStatement createTopLevelStatement(TypeStatement typeStatement) {
        return new CompilationUnitStatement(Optional.empty(), new ArrayList<>(), typeStatement);
    }

    private static ExpressionStatement printlnHelloWorld(Path path, int lineNumber, int identifierOffset,
            int lParenOffset, int rParenOffset) {
        return new ExpressionStatement(new MethodCallExpression("println", systemOutPrintln(),
                Arrays.asList(literalUnit("\"Hello World\"")), path, new FilePosition(lineNumber, identifierOffset),
                new FilePosition(lineNumber, lParenOffset), new FilePosition(lineNumber, rParenOffset)));
    }

    private static ExpressionStatement createMethodCallStatement(String identifier, Path path, int lineNumber,
            int identifierOffset, int lParenOffset, int rParenOffset) {
        return new ExpressionStatement(new MethodCallExpression(identifierUnit(identifier), Collections.emptyList(),
                path, new FilePosition(lineNumber, identifierOffset), new FilePosition(lineNumber,
                lParenOffset), new FilePosition(lineNumber, rParenOffset)));
    }

    private static Expression createMethodCall(String identifier, Path path, int lineNumber,
            int identifierOffset, int lParenOffset, int rParenOffset) {
        return new MethodCallExpression(identifierUnit(identifier), Collections.emptyList(), path,
                new FilePosition(lineNumber, identifierOffset),
                new FilePosition(lineNumber, lParenOffset),
                new FilePosition(lineNumber, rParenOffset), Collections.emptyList());
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

    private static BinaryExpression systemOutPrintln() {
        BinaryExpression systemOut = new BinaryExpression(identifierUnit("System"),
                BinaryOperation.DOT, identifierUnit("out"));
        return new BinaryExpression(systemOut, BinaryOperation.DOT, identifierUnit("println"));
    }
}
