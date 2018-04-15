package org.qmul.csar.code.java;

import org.junit.Assert;
import org.junit.ComparisonFailure;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A set of static utility methods for testing.
 */
public class TestUtils {

    /**
     * A method analogous to {@link Assert#assertEquals(Object, Object)} for lists.
     */
    public static <E> void assertEquals(List<E> expected, List<E> actual) {
        // Check size
        if (expected.size() != actual.size())
            throw new ComparisonFailure("list size mismatch: " + expected.size() + " != " + actual.size(),
                    toString(expected), toString(actual));

        // Check contents
        for (E e : expected) {
            if (!actual.contains(e))
                throw new ComparisonFailure("expected actual list to contain: " + e.toString(),
                        toString(expected), toString(actual));
        }
    }

    /**
     * Returns the argument list as a list in JSON array notation.
     * e.g. [1, 2, 3].
     */
    private static <E> String toString(List<E> expected) {
        return "[" + expected.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }
}
