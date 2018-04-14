package org.qmul.csar.code;

import java.nio.file.Path;

/**
 * A refactor target.
 */
public interface RefactorChange {

    /**
     * Returns the path of the change.
     */
    Path path();

    /**
     * Returns the line number of the change (starts at 1).
     */
    int lineNumber();

    /**
     * Returns the start index of the change (starts at 0).
     */
    int startIndex();

    /**
     * Returns the end index of the change (starts at 0).
     */
    int endIndex();
}
