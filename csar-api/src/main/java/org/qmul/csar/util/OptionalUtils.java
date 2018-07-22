package org.qmul.csar.util;

import java.util.List;
import java.util.Optional;

/**
 * Miscellaneous utility methods for interacting with {@link Optional}.
 */
public class OptionalUtils {

    /**
     * Returns the result of a lenient comparison against the argument.
     * If the second argument has value <tt>Optional.empty()</tt>, then it returns <tt>true</tt>.
     * Otherwise, a standard comparison using {@link #equals(Object)} on the elements inside the Optionals is used.
     *
     * @param o1 the base element
     * @param o2 the element to leniently compare
     * @param <T> the type of the optionals
     * @return <tt>true</tt> if the lenient comparison is successful, otherwise <tt>false</tt>
     */
    public static <T> boolean lenientEquals(Optional<T> o1, Optional<T> o2) {
        return !o2.isPresent() || (o1.isPresent() && o1.get().equals(o2.get()));
    }

    public static <T> boolean lenientEquals(Optional<Boolean> present, List<T> elements, Optional<Boolean> otherPresent,
            List<T> otherElements) {
        return !otherPresent.isPresent() || present.isPresent() && elements.equals(otherElements);
    }
}
