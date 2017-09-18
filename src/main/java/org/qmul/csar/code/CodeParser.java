package org.qmul.csar.code;

import org.qmul.csar.lang.Statement;

import java.io.IOException;
import java.nio.file.Path;

public interface CodeParser {

    /**
     * Parses the argument into a {@link Statement}.
     *
     * @param file the file to parse
     * @return the file as a {@link Statement}
     * @throws IOException if an I/O exception occurs
     */
    Statement parse(Path file) throws IOException;

    boolean accepts(Path file);
}
