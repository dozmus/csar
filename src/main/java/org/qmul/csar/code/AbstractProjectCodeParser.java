package org.qmul.csar.code;

import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

/**
 * A project-wide code file parser.
 */
public abstract class AbstractProjectCodeParser {

    /**
     * Returns a map with parsed files as keys, and the output statements as values.
     *
     * @return a map with parsed files as keys, and the output statements as values.
     */
    public abstract Map<Path, Statement> results();

    /**
     * Returns <tt>true</tt> if an error occurred within {@link #results()} which did not result in an exception being
     * thrown.
     * @return <tt>true</tt> if an error occurred within {@link #results()}
     */
    public abstract boolean errorOccurred();
}
