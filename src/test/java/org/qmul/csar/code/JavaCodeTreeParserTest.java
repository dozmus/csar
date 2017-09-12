package org.qmul.csar.code;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.qmul.csar.lang.*;
import org.qmul.csar.query.CsarQuery;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public final class JavaCodeTreeParserTest {

    /**
     * Directory of the java code files.
     */
    private static final String SAMPLES_DIRECTORY = "src/test/resources/org/qmul/csar/code/";
    private final Node sampleNode;
    private final String sampleFileName;

    public JavaCodeTreeParserTest(Node sampleNode, String sampleFileName) {
        this.sampleNode = sampleNode;
        this.sampleFileName = sampleFileName;
    }

    /**
     * A node representing the contents of 'Sample1.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static Node sample1() {
        // Construct language elements
        ClassLanguageElement clazz = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "Sample1")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .strictfpModifier(true)
                .abstractModifier(true)
                .superClasses("AbstractSample")
                .build();
        ConstructorLanguageElement constructor1 = new ConstructorLanguageElement
                .Builder(CsarQuery.Type.DEF, "Sample1")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .parameterCount(0)
                .build();
        ConstructorLanguageElement constructor2 = new ConstructorLanguageElement
                .Builder(CsarQuery.Type.DEF, "Sample1")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .parameterCount(1)
                .parameters(new Parameter("String...", Optional.of("names"), Optional.of(false)))
                .typeParameters("E")
                .build();
        Node constructor2Node = new Node(constructor2);
        constructor2Node.addNode(new Node(
                new VariableLanguageElement.Builder(CsarQuery.Type.DEF, VariableType.LOCAL, "s")
                        .finalModifier(false)
                        .identifierType("String[]")
                        .build())
        );
        VariableLanguageElement variable1 = InstanceVariableLanguageElement.Builder
                .allFalse(CsarQuery.Type.DEF, "className")
                .visibilityModifier(VisibilityModifier.PRIVATE)
                .finalModifier(true)
                .identifierType("String")
                .valueExpression("\"Sample1\"")
                .build();
        VariableLanguageElement variable2 = InstanceVariableLanguageElement.Builder
                .allFalse(CsarQuery.Type.DEF, "str")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .identifierType("String")
                .build();
        MethodLanguageElement method1 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "add")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .abstractModifier(true)
                .returnType("void")
                .parameters(new Parameter("int[]", Optional.of("a"), Optional.of(true)),
                        new Parameter("int", Optional.of("b"), Optional.of(false)))
                .parameterCount(2)
                .build();
        MethodLanguageElement method2 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "getResult")
                .visibilityModifier(VisibilityModifier.PROTECTED)
                .finalModifier(true)
                .returnType("int")
                .parameterCount(0)
                .build();
        MethodLanguageElement method3 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "setResult")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .finalModifier(true)
                .returnType("void")
                .parameters(new Parameter("int", Optional.of("result"), Optional.of(false)))
                .parameterCount(1)
                .typeParameters("E extends AbstractSample")
                .build();
        Node method3Node = new Node(method3);
        method3Node.addNode(new Node(
                new VariableLanguageElement.Builder(CsarQuery.Type.DEF, VariableType.LOCAL, "k")
                        .finalModifier(true)
                        .identifierType("int")
                        .valueExpression("3")
                        .build())
        );

        // Build node tree
        Node root = new Node(clazz);
        root.addNode(new Node(constructor1));
        root.addNode(constructor2Node);
        root.addNode(new Node(variable1));
        root.addNode(new Node(variable2));
        root.addNode(new Node(method1));
        root.addNode(new Node(method2));
        root.addNode(method3Node);
        return root;
    }

    /**
     * A node representing the contents of 'Sample2.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static Node sample2() {
        // Construct language elements
        ClassLanguageElement clazz = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "Sample2")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .interfaceModifier(true)
                .superClasses("Runnable")
                .build();
        VariableLanguageElement const1 = InstanceVariableLanguageElement.Builder
                .allFalse(CsarQuery.Type.DEF, "ITERATIONS")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .staticModifier(true)
                .identifierType("int")
                .valueExpression("1000")
                .build();
        MethodLanguageElement method1 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "print")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("void")
                .defaultModifier(true)
                .parameters(new Parameter("String", Optional.of("s"), Optional.of(false)))
                .parameterCount(1)
                .build();
        MethodLanguageElement method2 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "print")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(new Parameter("E", Optional.of("level"), Optional.of(false)),
                        new Parameter("String...", Optional.of("s"), Optional.of(false)))
                .parameterCount(2)
                .typeParameters("E")
                .build();
        VariableLanguageElement const2 = InstanceVariableLanguageElement.Builder
                .allFalse(CsarQuery.Type.DEF, "name")
                .identifierType("String[]")
                .valueExpression("generateName(Sample2.class)")
                .build();
        MethodLanguageElement method3 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "print")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(new Parameter("String[]", Optional.of("$"), Optional.of(false)))
                .parameterCount(1)
                .build();

        // Build node tree
        Node root = new Node(clazz);
        root.addNode(new Node(const1));
        root.addNode(new Node(method1));
        root.addNode(new Node(method2));
        root.addNode(new Node(const2));
        root.addNode(new Node(method3));
        return root;
    }

    /**
     * A node representing the contents of 'Sample3.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static Node sample3() {
        // Construct language elements
        ClassLanguageElement clazz = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "Sample3")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .typeParameters("List extends Collection<String>", "T")
                .build();

        // Build node tree
        return new Node(clazz);
    }

    /**
     * A node representing the contents of 'Sample4.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static Node sample4() {
        // Construct language elements
        ClassLanguageElement parentClass = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "Sample4")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build();

        MethodLanguageElement parentClassMethod = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "work")
                .returnType("void")
                .parameterCount(0)
                .build();

        ClassLanguageElement localInterface = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "Runnable")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .local(true)
                .interfaceModifier(true)
                .build();
        MethodLanguageElement method2 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "run")
                .returnType("void")
                .parameterCount(0)
                .build();
        Node interfaceNode = new Node(localInterface);
        interfaceNode.addNode(new Node(method2));

        ClassLanguageElement localClass = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "A")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .local(true)
                .superClasses("Runnable")
                .build();
        MethodLanguageElement method3 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "run")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .returnType("void")
                .parameterCount(0)
                .build();
        VariableLanguageElement local1 = new VariableLanguageElement.Builder(CsarQuery.Type.DEF, VariableType.LOCAL,
                "x")
                .finalModifier(false)
                .identifierType("int")
                .valueExpression("30")
                .build();
        Node localClassMethodNode = new Node(method3);
        localClassMethodNode.addNode(new Node(local1));
        Node classNode = new Node(localClass);
        classNode.addNode(localClassMethodNode);

        VariableLanguageElement local2 = new VariableLanguageElement.Builder(CsarQuery.Type.DEF, VariableType.LOCAL,
                "worker")
                .finalModifier(false)
                .identifierType("A")
                .valueExpression("newA()")
                .build();

        // Build node tree
        Node methodNode = new Node(parentClassMethod);
        methodNode.addNode(interfaceNode);
        methodNode.addNode(classNode);
        methodNode.addNode(new Node(local2));

        Node root = new Node(parentClass);
        root.addNode(methodNode);
        return root;
    }

    /**
     * A node representing the contents of 'Sample5.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static Node sample5() {
        // Construct language elements
        ClassLanguageElement clazz = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "Sample5")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .build();
        ClassLanguageElement innerInterface = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "A")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .interfaceModifier(true)
                .inner(true)
                .build();

        ClassLanguageElement innerClass = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "B")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .superClasses("A")
                .inner(true)
                .build();
        MethodLanguageElement method = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "work")
                .returnType("void")
                .parameters(new Parameter("int", Optional.of("threads"), Optional.of(false)))
                .parameterCount(1)
                .build();

        // Build node tree
        Node innerClassNode = new Node(innerClass);
        innerClassNode.addNode(new Node(method));

        Node root = new Node(clazz);
        root.addNode(new Node(innerInterface));
        root.addNode(innerClassNode);
        return root;
    }

    /**
     * A node representing the contents of 'Sample6.java' inside <tt>SAMPLES_DIRECTORY</tt>.
     *
     * @return
     */
    private static Node sample6() {
        // Construct language elements
        ClassLanguageElement interfaceElement = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "Sample6")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .interfaceModifier(true)
                .build();
        ClassLanguageElement innerClass = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "A")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .superClasses("Sample6")
                .inner(true)
                .typeParameters("T0")
                .build();

        // Build node tree
        Node root = new Node(interfaceElement);
        root.addNode(new Node(innerClass));
        return root;
    }

    @Parameterized.Parameters
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

    @Test
    public void testSample() throws IOException {
        assertEquals(sampleNode, CodeTreeParserFactory.parse(Paths.get(SAMPLES_DIRECTORY + sampleFileName)));
    }
}
