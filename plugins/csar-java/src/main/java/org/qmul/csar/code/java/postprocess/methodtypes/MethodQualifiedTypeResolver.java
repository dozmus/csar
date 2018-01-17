package org.qmul.csar.code.java.postprocess.methodtypes;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.parse.statement.AnnotationStatement;
import org.qmul.csar.code.java.parse.statement.TopLevelTypeStatement;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

public class MethodQualifiedTypeResolver implements CodePostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodQualifiedTypeResolver.class);
    private final QualifiedNameResolver qualifiedNameResolver;

    public MethodQualifiedTypeResolver(QualifiedNameResolver qualifiedNameResolver) {
        this.qualifiedNameResolver = qualifiedNameResolver;
    }

    /**
     * Resolves the types of each method in the argument codebase.
     *
     * @param code the code base to resolve for
     */
    public void analyze(Map<Path, Statement> code) {
        LOGGER.info("Starting...");
        long startTime = System.currentTimeMillis();

        // Iterate all code files
        for (Map.Entry<Path, Statement> entry : code.entrySet()) {
            Path path = entry.getKey();
            Statement statement = entry.getValue();

            if (!(statement instanceof TopLevelTypeStatement))
                continue;
            TopLevelTypeStatement topStatement = (TopLevelTypeStatement) statement;
            TypeStatement typeStatement = topStatement.getTypeStatement();

            if (typeStatement instanceof AnnotationStatement)
                continue;

            // Prepare visitor
            MethodStatementVisitor visitor = new MethodStatementVisitor(qualifiedNameResolver);
            visitor.setCode(code);
            visitor.setPath(path);
            visitor.setTopLevelParent(topStatement);
            visitor.setImports(topStatement.getImports());
            visitor.setPackage(topStatement.getPackageStatement());
            visitor.setParent(typeStatement);

            // Visit
            visitor.visitStatement(statement);
        }

        // Log completion message
        LOGGER.debug("Time Taken: {}ms", (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");
    }
}
