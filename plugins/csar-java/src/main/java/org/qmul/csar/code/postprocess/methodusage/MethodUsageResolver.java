package org.qmul.csar.code.postprocess.methodusage;

import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class MethodUsageResolver {

    public void resolve(Map<Path, Statement> code) {
        for (Map.Entry<Path, Statement> file : code.entrySet()) {
            Path path = file.getKey();
            Statement statement = file.getValue();

            MethodUsageStatementVisitor visitor = new MethodUsageStatementVisitor(code, path);
            visitor.visitStatement(statement);
        }
    }
}
