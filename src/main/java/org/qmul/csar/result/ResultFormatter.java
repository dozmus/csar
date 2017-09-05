package org.qmul.csar.result;

import java.util.List;

/**
 * A {@link Result} formatter which translates it into a <tt>String</tt>.
 */
public abstract class ResultFormatter {

    /**
     * Returns a <tt>String</tt> representation of the provided argument.
     * @param results the results to format
     * @return a <tt>String</tt> representing the argument
     * @throws Exception an arbitrary exception may occur in subclasses, depending on their behaviour
     */
    public abstract String format(List<Result> results) throws Exception;
}
