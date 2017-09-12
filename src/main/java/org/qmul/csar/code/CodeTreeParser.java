package org.qmul.csar.code;

import java.io.IOException;
import java.nio.file.Path;

public interface CodeTreeParser {

    /**
     * Parses the argument into a {@link Node}.
     *
     * @param file the file to parse
     * @return the file as a {@link Node}
     * @throws IOException if an I/O exception occurs
     */
    Node parse(Path file) throws IOException;

    boolean accepts(Path file);
}
