package org.qmul.csar.code.postprocess;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.CodeBase;

/**
 * A code post-processor.
 */
public interface CodePostProcessor {

    /**
     * Post-processes the argument code base.
     */
    void postprocess(CodeBase code);

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
