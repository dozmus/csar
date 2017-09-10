package org.qmul.csar.code;

import java.io.IOException;
import java.nio.file.Path;

public interface CodeTreeParser {

    Node parse(Path file) throws IOException;

    boolean accepts(Path file);
}
