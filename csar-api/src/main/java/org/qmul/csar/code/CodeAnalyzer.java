package org.qmul.csar.code;

import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

/**
 * A code post-processor.
 */
public interface CodeAnalyzer {

    void analyze(Map<Path, Statement> code);
}
