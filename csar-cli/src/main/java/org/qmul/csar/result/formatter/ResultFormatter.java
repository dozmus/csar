package org.qmul.csar.result.formatter;

import org.qmul.csar.code.Result;

import java.util.List;

/**
 * A formatter which translates a {@link List} of {@link Result} to a <tt>String</tt>.
 */
public interface ResultFormatter {

    /**
     * Returns a <tt>String</tt> representation of the provided argument.
     *
     * @param results the results to format
     * @return a <tt>String</tt> representing the argument
     * @throws Exception an arbitrary exception may occur in subclasses, depending on their behaviour
     */
    String format(List<Result> results) throws Exception;
}
