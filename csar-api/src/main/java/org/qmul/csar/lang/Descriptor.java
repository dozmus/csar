package org.qmul.csar.lang;

import org.qmul.csar.util.OptionalUtils;

import java.util.Optional;

/**
 * A descriptor, typically used to describe searchable statements.
 */
public interface Descriptor {

    /**
     * Returns the result of a lenient comparison against the argument.
     * If a variable in the argument has value <tt>Optional.empty()</tt>, then that one is skipped.
     * Otherwise, a standard comparison using {@link #equals(Object)} is used.
     *
     * @param other the descriptor to compare against
     * @return <tt>true</tt> if the lenient comparison is successful, otherwise <tt>false</tt>
     * @see OptionalUtils#lenientEquals(Optional, Optional)
     */
    boolean lenientEquals(Descriptor other);
}
