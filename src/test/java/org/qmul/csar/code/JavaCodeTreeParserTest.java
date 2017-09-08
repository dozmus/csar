package org.qmul.csar.code;

import org.junit.Test;
import org.qmul.csar.lang.ClassLanguageElement;
import org.qmul.csar.lang.MethodLanguageElement;
import org.qmul.csar.lang.Parameter;
import org.qmul.csar.lang.VisibilityModifier;
import org.qmul.csar.query.CsarQuery;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class JavaCodeTreeParserTest {

    /**
     * Directory of the java code files.
     */
    private static final String SAMPLES_DIRECTORY = "src/test/resources/grammars/java8pt/";

    /**
     * A node representing the contents of src/test/resources/grammars/java8pt/Sample1.java
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
        MethodLanguageElement method1 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "add")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .abstractModifier(true)
                .returnType("void")
                .parameters(new Parameter("int", Optional.of("a"), Optional.of(true)),
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
                .build();

        // Build node tree
        Node root = new Node(clazz);
        root.addNode(new Node(method1));
        root.addNode(new Node(method2));
        root.addNode(new Node(method3));
        return root;
    }

    /**
     * A node representing the contents of src/test/resources/grammars/java8pt/Sample2.java
     * @return
     */
    private static Node sample2() {
        // Construct language elements
        ClassLanguageElement clazz = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "Sample2")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .interfaceModifier(true)
                .superClasses("Runnable")
                .build();
        MethodLanguageElement method1 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "print")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(new Parameter("String", Optional.of("s"), Optional.of(false)))
                .parameterCount(1)
                .build();
        MethodLanguageElement method2 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "print")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(new Parameter("int", Optional.of("level"), Optional.of(false)),
                        new Parameter("String...", Optional.of("s"), Optional.of(false)))
                .parameterCount(2)
                .build();
        MethodLanguageElement method3 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "print")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .returnType("void")
                .parameters(new Parameter("String[]", Optional.of("$"), Optional.of(false)))
                .parameterCount(1)
                .build();

        // Build node tree
        Node root = new Node(clazz);
        root.addNode(new Node(method1));
        root.addNode(new Node(method2));
        root.addNode(new Node(method3));
        return root;
    }

    @Test
    public void testSample1() throws IOException {
        assertEquals(sample1(), CodeTreeParserFactory.parse(Paths.get(SAMPLES_DIRECTORY + "Sample1.java")));
    }

    @Test
    public void testSample2() throws IOException {
        assertEquals(sample2(), CodeTreeParserFactory.parse(Paths.get(SAMPLES_DIRECTORY + "Sample2.java")));
    }
}
