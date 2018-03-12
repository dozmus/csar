package org.qmul.csar.code.java.postprocess.methodcalls.typeinstances;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

/**
 * A post-processor which attaches to each {@link org.qmul.csar.code.java.parse.expression.MethodCallExpression}
 * a {@link org.qmul.csar.code.java.postprocess.util.TypeInstance} for its source and all of its parameters.
 */
public class MethodCallTypeInstanceResolver implements CodePostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodCallTypeInstanceResolver.class);
    private QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();
    private TypeHierarchyResolver typeHierarchyResolver;

    public void postprocess(Map<Path, Statement> code) {
        LOGGER.info("Starting...");
        long startTime = System.currentTimeMillis();

        for (Map.Entry<Path, Statement> file : code.entrySet()) {
            Path path = file.getKey();
            Statement statement = file.getValue();

            MethodCallExpressionVisitor visitor = new MethodCallExpressionVisitor(code, path, qualifiedNameResolver,
                    typeHierarchyResolver);
            visitor.visitStatement(statement);
        }

        // Log completion message
        LOGGER.debug("Time Taken: {}ms", (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");
    }

    public void setTypeHierarchyResolver(TypeHierarchyResolver typeHierarchyResolver) {
        this.typeHierarchyResolver = typeHierarchyResolver;
    }
}
