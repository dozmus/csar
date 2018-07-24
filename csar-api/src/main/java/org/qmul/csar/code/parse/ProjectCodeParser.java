package org.qmul.csar.code.parse;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.CodeBase;

/**
 * A project-wide code parser.
 */
public interface ProjectCodeParser {


    /**
     * Returns the parsed code base, which is a mapping of a file's path to its parsed statement.
     *
     * @return parsed code base.
     */
    CodeBase results();

    /**
     * Adds an error listener.
     *
     * @param errorListener the error listener
     */
    void addErrorListener(CsarErrorListener errorListener);

    /**
     * Removes an error listener.
     *
     * @param errorListener the error listener
     */
    void removeErrorListener(CsarErrorListener errorListener);
}
