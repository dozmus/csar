package org.qmul.csar.code;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

/**
 * A code post-processor.
 */
public interface CodePostProcessor {

    /**
     * Post-processes the argument code base.
     */
    void postprocess(Map<Path, Statement> code);

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
