package org.qmul.csar.code.java.postprocess.methods.use;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

/**
 * This maps method usages to their corresponding definitions. It requires {@link TypeHierarchyResolver} and
 * {@link MethodCallTypeInstanceResolver} to be run first.
 */
public class MethodUseResolver implements CodePostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodUseResolver.class);
    private final QualifiedNameResolver qualifiedNameResolver;
    private TypeHierarchyResolver typeHierarchyResolver;

    public MethodUseResolver(QualifiedNameResolver qualifiedNameResolver) {
        this.qualifiedNameResolver = qualifiedNameResolver;
    }

    public MethodUseResolver() {
        this(new QualifiedNameResolver());
    }

    public void postprocess(Map<Path, Statement> code) {
        LOGGER.info("Starting...");
        long startTime = System.currentTimeMillis();
        MethodCallStatementVisitor visitor = new MethodCallStatementVisitor(code, typeHierarchyResolver,
                qualifiedNameResolver);

        for (Map.Entry<Path, Statement> file : code.entrySet()) {
            Path path = file.getKey();
            Statement statement = file.getValue();
            visitor.setPath(path);
            visitor.visitStatement(statement);
        }

        // Log completion message
        LOGGER.debug("Time taken {}ms", (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");
    }

    public void setTypeHierarchyResolver(TypeHierarchyResolver typeHierarchyResolver) {
        this.typeHierarchyResolver = typeHierarchyResolver;
    }
}
