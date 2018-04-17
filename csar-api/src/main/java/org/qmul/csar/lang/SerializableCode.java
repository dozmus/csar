package org.qmul.csar.lang;

/**
 * A serializable code.
 */
public interface SerializableCode {

    /**
     * Returns {@link #toPseudoCode(int)} with indentation 0.
     *
     * @return returns pseudocode with indentation 0.
     */
    default String toPseudoCode() {
        return toPseudoCode(0);
    }

    /**
     * Returns pseudo-code of this object with the argument indentation count.
     *
     * @param indentation the amount of indentation before each line
     * @return returns pseudocode with the argument indentation.
     */
    String toPseudoCode(int indentation);
}
