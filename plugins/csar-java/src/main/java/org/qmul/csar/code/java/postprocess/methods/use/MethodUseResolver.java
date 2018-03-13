package org.qmul.csar.code.java.postprocess.methods.use;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.java.postprocess.util.MethodResolver;
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
        MethodCallStatementVisitor visitor = new MethodCallStatementVisitor(code);

        for (Map.Entry<Path, Statement> file : code.entrySet()) {
            Path path = file.getKey();
            Statement statement = file.getValue();

            visitor.reset();
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

    private final class MethodCallStatementVisitor extends StatementVisitor {

        private final TraversalHierarchy traversalHierarchy = new TraversalHierarchy();
        private final Map<Path, Statement> code;
        private Path path;

        public MethodCallStatementVisitor(Map<Path, Statement> code) {
            this.code = code;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public void reset() {
            traversalHierarchy.clear();
        }

        @Override
        public void visitStatement(Statement statement) {
            traversalHierarchy.addLast(statement);
            super.visitStatement(statement);
            traversalHierarchy.removeLast();
        }

        @Override
        public void visitMethodCallExpression(MethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            resolveMethodCall(expression);
        }

        private void resolveMethodCall(MethodCallExpression expression) {
            LOGGER.trace("Resolving method call: {}", expression.toPseudoCode());

            // Resolve method
            MethodResolver resolver = new MethodResolver(path, code, qualifiedNameResolver, typeHierarchyResolver);
            MethodStatement method = resolver.resolve(expression, traversalHierarchy);

            // Add to method usages, if method was found
            if (method != null) {
                method.getMethodUsages().add(expression);
            }
        }
    }
}
