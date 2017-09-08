package org.qmul.csar.code;

import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractCodeTreeParser {

    public abstract Node parse(Path file) throws IOException;

    public abstract boolean accepts(Path file);
}
