package org.qmul.csar.code.parse;

import org.qmul.csar.lang.Statement;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A code parser, used in {@link ProjectCodeParser}. All implementations must have a 0 argument constructor or
 * exceptions will be thrown in {@link CodeParserFactory}.
 */
public interface CodeParser {

    /**
     * Parses the argument into a {@link Statement}.
     *
     * @param file the file to parse
     * @return the file as a {@link Statement}
     * @throws IOException if an I/O exception occurs
     */
    Statement parse(Path file) throws IOException;

    /**
     * Returns <tt>true</tt> if the argument is a code file which this parser can parse (based on file extension).
     *
     * @param file the path to check is accepted
     * @return if the given file can be parsed by this parser
     */
    boolean accepts(Path file);
}
