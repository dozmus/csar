package org.qmul.csar.code.java.postprocess.typehierarchy;

import org.qmul.csar.code.CodePostProcessor;

/**
 * A type hierarchy resolver for a code base.
 */
public interface TypeHierarchyResolver extends CodePostProcessor {

    /**
     * Returns <tt>true</tt> if the first type is a superclass of, or equal to, the second type.
     */
    boolean isSubtype(String type1, String type2);

    /**
     * Returns <tt>true</tt> if the first type is a superclass of the second type.
     */
    boolean isStrictlySubtype(String type1, String type2);
}
