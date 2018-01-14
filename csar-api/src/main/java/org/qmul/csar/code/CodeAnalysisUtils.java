package org.qmul.csar.code;

import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public interface CodeAnalysisUtils {

    void setCode(Map<Path, Statement> code);

    void analyze();
}
