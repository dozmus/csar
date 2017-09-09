package org.qmul.csar.code;

import org.junit.Assert;
import org.junit.Test;
import org.qmul.csar.lang.ClassLanguageElement;
import org.qmul.csar.lang.MethodLanguageElement;
import org.qmul.csar.lang.Parameter;
import org.qmul.csar.lang.VisibilityModifier;
import org.qmul.csar.query.CsarQuery;

import java.util.Optional;

public final class NodeHelperTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static void assertEquals(String expected, Node node) {
        Assert.assertEquals(expected, NodeHelper.toStringRecursively(node));
    }

    @Test
    public void toStringRecursively() throws Exception {
        // Test #1: single class element
        ClassLanguageElement clazz1 = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "Sample1")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .strictfpModifier(true)
                .abstractModifier(true)
                .superClasses("AbstractSample")
                .build();
        assertEquals("DEF:public strictfp abstract class Sample1(AbstractSample)", new Node(clazz1));

        // Test #2: single method element
        MethodLanguageElement method1 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "add")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .abstractModifier(true)
                .returnType("void")
                .parameters(new Parameter("int", Optional.of("a"), Optional.of(true)),
                        new Parameter("int", Optional.of("b"), Optional.of(false)))
                .parameterCount(2)
                .build();
        assertEquals("DEF:public abstract void add(final int a, int b)", new Node(method1));

        // Test #3: class element containing two methods and a class
        MethodLanguageElement method2 = MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "getResult")
                .visibilityModifier(VisibilityModifier.PROTECTED)
                .finalModifier(true)
                .returnType("int")
                .parameterCount(0)
                .build();
        ClassLanguageElement clazz2 = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "Sample2")
                .visibilityModifier(VisibilityModifier.PRIVATE)
                .staticModifier(true)
                .build();
        Node node = new Node(clazz1);
        node.addNode(new Node(method1));
        node.addNode(new Node(method2));
        node.addNode(new Node(clazz2));
        assertEquals("DEF:public strictfp abstract class Sample1(AbstractSample)" + NEW_LINE
                + "  DEF:public abstract void add(final int a, int b)" + NEW_LINE
                + "  DEF:protected final int getResult()" + NEW_LINE
                + "  DEF:private static class Sample2", node);
    }
}