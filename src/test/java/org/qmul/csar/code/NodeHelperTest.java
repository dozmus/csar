package org.qmul.csar.code;

import org.junit.Assert;
import org.junit.Test;
import org.qmul.csar.lang.*;
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
        ClassLanguageElement clazz1 = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "MainClass")
                .visibilityModifier(VisibilityModifier.PUBLIC)
                .strictfpModifier(true)
                .abstractModifier(true)
                .typeParameters("T0")
                .superClasses("AbstractSample")
                .build();
        assertEquals("DEF:public strictfp abstract class MainClass<T0>(AbstractSample)", new Node(clazz1));

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

        // Test #3: class element containing a field, two methods and a class
        Node field1Node = new Node(InstanceVariableLanguageElement.Builder
                .allFalse(CsarQuery.Type.DEF, "str")
                .visibilityModifier(VisibilityModifier.PACKAGE_PRIVATE)
                .identifierType("String")
                .build());
        Node method2Node = new Node(MethodLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "getResult")
                .visibilityModifier(VisibilityModifier.PROTECTED)
                .finalModifier(true)
                .returnType("int")
                .parameterCount(0)
                .typeParameters("E", "K extends Sample")
                .build()
        );
        method2Node.addNode(new Node(
                new VariableLanguageElement.Builder(CsarQuery.Type.DEF, VariableType.LOCAL, "s")
                        .finalModifier(false)
                        .identifierType("String[]")
                        .build())
        );
        method2Node.addNode(new Node(
                new VariableLanguageElement.Builder(CsarQuery.Type.DEF, VariableType.LOCAL, "x")
                        .finalModifier(true)
                        .identifierType("int")
                        .valueExpression("40")
                        .build())
        );
        ClassLanguageElement clazz2 = ClassLanguageElement.Builder.allFalse(CsarQuery.Type.DEF, "SerializeContract")
                .visibilityModifier(VisibilityModifier.PRIVATE)
                .interfaceModifier(true)
                .inner(true)
                .anonymous(true)
                .build();

        Node root = new Node(clazz1);
        root.addNode(field1Node);
        root.addNode(new Node(method1));
        root.addNode(method2Node);
        root.addNode(new Node(clazz2));

        assertEquals("DEF:public strictfp abstract class MainClass<T0>(AbstractSample)" + NEW_LINE
                + "  DEF:package_private String str" + NEW_LINE
                + "  DEF:public abstract void add(final int a, int b)" + NEW_LINE
                + "  DEF:protected final <E, K extends Sample> int getResult()" + NEW_LINE
                + "    DEF:String[] s" + NEW_LINE
                + "    DEF:final int x = 40" + NEW_LINE
                + "  DEF:private (anonymous) (inner) interface SerializeContract", root);
    }
}
