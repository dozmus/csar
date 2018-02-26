package org.qmul.csar.code.java.postprocess.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TypeHelperTest {

    @Test
    public void testNormalizeVarArgs() {
        assertEquals("Class", TypeHelper.normalizeVarArgs("Class"));
        assertEquals("Class[]", TypeHelper.normalizeVarArgs("Class[]"));
        assertEquals("Class[][]", TypeHelper.normalizeVarArgs("Class[]..."));
        assertEquals("Class[]", TypeHelper.normalizeVarArgs("Class..."));
        assertEquals("org.pkg.Class", TypeHelper.normalizeVarArgs("org.pkg.Class"));
    }

    @Test
    public void testRemoveDimensions() {
        assertEquals("Class", TypeHelper.removeDimensions("Class"));
        assertEquals("Class", TypeHelper.removeDimensions("Class[]"));
        assertEquals("Class", TypeHelper.removeDimensions("Class[]..."));
        assertEquals("Class", TypeHelper.removeDimensions("Class..."));
        assertEquals("org.pkg.Class", TypeHelper.removeDimensions("org.pkg.Class"));
    }

    @Test
    public void testDimensions() {
        assertEquals(0, TypeHelper.dimensions("Class"));
        assertEquals(0, TypeHelper.dimensions("Class<E>"));
        assertEquals(1, TypeHelper.dimensions("Class[]"));
        assertEquals(1, TypeHelper.dimensions("Class<Integer[]>[]"));
        assertEquals(1, TypeHelper.dimensions("Class..."));
        assertEquals(2, TypeHelper.dimensions("Class[][]"));
        assertEquals(2, TypeHelper.dimensions("Class[]..."));
    }

    @Test
    public void testRemoveGenericArgument() {
        assertEquals("Class", TypeHelper.removeGenericArgument("Class"));
        assertEquals("Class[]", TypeHelper.removeGenericArgument("Class[]"));
        assertEquals("Class", TypeHelper.removeGenericArgument("Class<E>"));
        assertEquals("Class", TypeHelper.removeGenericArgument("Class<E, V>"));
        assertEquals("Class[]", TypeHelper.removeGenericArgument("Class<Integer[]>[]"));
    }

    @Test
    public void testResolveGenericTypes() {
        // TODO write
        assertEquals("Class<String, Integer>", TypeHelper.resolveGenericTypes("Class<E, V>",
                Arrays.asList("E extends String", "V super Integer")));
        assertEquals("Class<String, String>", TypeHelper.resolveGenericTypes("Class<E, V>",
                Arrays.asList("E extends String", "V super E")));
    }

    @Test
    public void testIdentifierOfGenericTypeParameter() {
        assertEquals("T", TypeHelper.identifierOfGenericTypeParameter("T"));
        assertEquals("T", TypeHelper.identifierOfGenericTypeParameter("T extends String"));
        assertEquals("T[]", TypeHelper.identifierOfGenericTypeParameter("T[] super E[][]"));
    }

    @Test
    public void testApplyTypeParameter() {
        // Type name
        assertEquals("String", TypeHelper.applyTypeParameter("E", "E extends String"));
        assertEquals("String[]", TypeHelper.applyTypeParameter("E[]", "E extends String"));

        // In list
        assertEquals("String<V>", TypeHelper.applyTypeParameter("E<V>", "E extends String"));
        assertEquals("String<V<String>>", TypeHelper.applyTypeParameter("E<V<E>>", "E extends String"));

        assertEquals("Class<String>", TypeHelper.applyTypeParameter("Class<E>", "E extends String"));
        assertEquals("Class<String<V>>", TypeHelper.applyTypeParameter("Class<E<V>>", "E extends String"));

        assertEquals("Class<String, V>", TypeHelper.applyTypeParameter("Class<E, V>", "E super String"));
        assertEquals("Class<String<V>, V>", TypeHelper.applyTypeParameter("Class<E<V>, V>", "E super String"));

        assertEquals("Class<V, String>", TypeHelper.applyTypeParameter("Class<V, E>", "E extends String"));
        assertEquals("Class<V, String<V>>", TypeHelper.applyTypeParameter("Class<V, E<V>>", "E extends String"));

        assertEquals("Class<V, String, V>", TypeHelper.applyTypeParameter("Class<V, E, V>", "E extends String"));
        assertEquals("Class<V, String<V>, V>", TypeHelper.applyTypeParameter("Class<V, E<V>, V>", "E extends String"));

        // Type name and list
        assertEquals("String<String>", TypeHelper.applyTypeParameter("E<E>", "E extends String"));

        // Arrays
        assertEquals("String<V>[]", TypeHelper.applyTypeParameter("E<V>[]", "E extends String"));

        assertEquals("Class<String[]>", TypeHelper.applyTypeParameter("Class<E[]>", "E extends String"));
        assertEquals("Class<String[], V>", TypeHelper.applyTypeParameter("Class<E[], V>", "E super String"));

        assertEquals("Class<V, String[]>", TypeHelper.applyTypeParameter("Class<V, E[]>", "E extends String"));
        assertEquals("Class<V, String[], V>", TypeHelper.applyTypeParameter("Class<V, E[], V>", "E extends String"));
    }

    @Test
    public void testEraseBoundsOnTypeParameter() {
        assertEquals("K", TypeHelper.eraseBoundsOnTypeParameter("T extends K"));
        assertEquals("Object", TypeHelper.eraseBoundsOnTypeParameter("E"));
        assertEquals("K", TypeHelper.eraseBoundsOnTypeParameter("? super K"));
        assertEquals("K[]", TypeHelper.eraseBoundsOnTypeParameter("? super K[]"));
    }

    @Test
    public void testEraseBounds() {
        assertEquals("Class", TypeHelper.eraseBounds("Class"));
        assertEquals("Class<E>", TypeHelper.eraseBounds("Class<E>"));
        assertEquals("Class<String>", TypeHelper.eraseBounds("Class<? super String>"));
        assertEquals("Class<String, Integer>", TypeHelper.eraseBounds("Class<? super String, Integer>"));
        assertEquals("Class<String[], Integer>", TypeHelper.eraseBounds("Class<? super String[], Integer>"));
    }

    @Test
    public void testExtractGenericArgument() {
        assertEquals("", TypeHelper.extractGenericArgument("Class"));
        assertEquals("<E>", TypeHelper.extractGenericArgument("Class<E>"));
        assertEquals("<E, V>", TypeHelper.extractGenericArgument("Class<E, V>"));
        assertEquals("<Integer[]>", TypeHelper.extractGenericArgument("Class<Integer[]>[]"));
    }
}