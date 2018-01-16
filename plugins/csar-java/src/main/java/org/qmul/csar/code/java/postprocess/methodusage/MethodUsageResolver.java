package org.qmul.csar.code.java.postprocess.methodusage;

import org.qmul.csar.code.CodeAnalyzer;
import org.qmul.csar.lang.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

public class MethodUsageResolver implements CodeAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodUsageResolver.class);

    public void analyze(Map<Path, Statement> code) {
        LOGGER.info("Starting...");
        long startTime = System.currentTimeMillis();

        for (Map.Entry<Path, Statement> file : code.entrySet()) {
            Path path = file.getKey();
            Statement statement = file.getValue();

            MethodUsageStatementVisitor visitor = new MethodUsageStatementVisitor(code, path);
            visitor.visitStatement(statement);
        }

        // Log completion message
        LOGGER.debug("Time Taken: {}ms", (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");
    }
}
