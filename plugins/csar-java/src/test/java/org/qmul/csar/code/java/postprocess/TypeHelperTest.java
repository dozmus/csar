package org.qmul.csar.code.java.postprocess;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TypeHelperTest {

    private static void assertSanitizeType(String expected, String input) {
        assertSanitizeType(expected, input, Collections.emptyList());
    }

    private static void assertSanitizeType(String expected, String input, List<String> typeParameters) {
        assertEquals(expected, TypeHelper.resolveGenericTypes(input, typeParameters));
    }

    private static void assertEraseBoundsOnTypeParameter(String expected, String typeParameter) {
        assertEquals(expected, TypeHelper.eraseBoundsOnTypeParameter(typeParameter));
    }

    @Test
    public void testRemoveGenericArgument() {
        assertEquals("String", TypeHelper.removeGenericArgument("String"));
        assertEquals("String[]", TypeHelper.removeGenericArgument("String[]"));
        assertEquals("String[]", TypeHelper.removeGenericArgument("String<String>[]"));
        assertEquals("String...", TypeHelper.removeGenericArgument("String<String extends T, E>..."));
    }

    @Test
    public void testIdentifierOfGenericTypeParameter() {
        assertEquals("T", TypeHelper.identifierOfGenericTypeParameter("T"));
        assertEquals("T", TypeHelper.identifierOfGenericTypeParameter("T extends String"));
        assertEquals("T", TypeHelper.identifierOfGenericTypeParameter("T super String"));
    }

    @Test
    public void testSanitizeTypeWithoutTypeParameters() {
        assertSanitizeType("List<T>", "List<? extends T>");
        assertSanitizeType("List<Object>", "List<? super Object>");
        assertSanitizeType("List<Object, T<Object>>", "List<? super Object, ? extends T<? extends Object>>");
    }

    @Test
    public void testSanitizeTypeWithTypeParameters() {
        assertSanitizeType("Object", "T", Arrays.asList("T"));
        assertSanitizeType("List<Object>", "List<? extends T>", Arrays.asList("T"));
        assertSanitizeType("List<Object, String>", "List<T, ? super String>", Arrays.asList("T"));
        assertSanitizeType("List<Apple, String>", "List<T, ? super String>", Arrays.asList("T extends Apple"));
        assertSanitizeType("List<Object, Apple<Object>>", "List<? super Object, ? extends T<? extends Object>>",
                Arrays.asList("T extends Apple"));
    }

//    @Test
//    public void testSanitizeTypeWithTypeParametersAndArrays() {
//        assertSanitizeType("Object[]", "T[]", Arrays.asList("T"));
//        assertSanitizeType("List<Object[]>", "List<? extends T[]>", Arrays.asList("T"));
//        assertSanitizeType("List<Object[], String>", "List<T[], ? super String>", Arrays.asList("T"));
//        assertSanitizeType("List<Apple[], String>", "List<T[], ? super String>", Arrays.asList("T extends Apple"));
//    }

    @Test
    public void testEraseBoundsOnTypeParameter() {
        assertEraseBoundsOnTypeParameter("String", "T extends String");
        assertEraseBoundsOnTypeParameter("Object", "T super Object");
        assertEraseBoundsOnTypeParameter("Object", "T");
    }
}
