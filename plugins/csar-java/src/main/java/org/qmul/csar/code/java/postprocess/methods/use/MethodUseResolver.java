package org.qmul.csar.code.java.postprocess.methods.use;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.SimpleTypeHierarchyResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.java.postprocess.util.MethodCallResolver;
import org.qmul.csar.lang.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This maps method usages to their corresponding definitions. It requires {@link SimpleTypeHierarchyResolver} and
 * {@link MethodCallTypeInstanceResolver} to be run first.
 */
public class MethodUseResolver implements CodePostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodUseResolver.class);
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();
    private final QualifiedNameResolver qnr;
    private final TypeHierarchyResolver thr;

    public MethodUseResolver(QualifiedNameResolver qnr, TypeHierarchyResolver thr) {
        this.qnr = qnr;
        this.thr = thr;
    }

    public MethodUseResolver(SimpleTypeHierarchyResolver thr) {
        this(new QualifiedNameResolver(), thr);
    }

    public void postprocess(Map<Path, Statement> code) {
        LOGGER.info("Starting...");
        long startTime = System.currentTimeMillis();

        try {
            MethodCallStatementVisitor visitor = new MethodCallStatementVisitor(code);

            for (Map.Entry<Path, Statement> file : code.entrySet()) {
                Path path = file.getKey();
                Statement statement = file.getValue();

                visitor.reset();
                visitor.setPath(path);
                visitor.visitStatement(statement);
            }
        } catch (Exception ex) {
            errorListeners.forEach(l -> l.fatalErrorPostProcessing(ex));
        }

        // Log completion message
        LOGGER.debug("Time taken {}ms", (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");
    }

    @Override
    public void addErrorListener(CsarErrorListener errorListener) {
        errorListeners.remove(errorListener);
    }

    @Override
    public void removeErrorListener(CsarErrorListener errorListener) {
        errorListeners.remove(errorListener);
    }

    /**
     * Visits {@link Statement} and resolves each method call.
     */
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
            MethodCallResolver resolver = new MethodCallResolver(path, code, qnr, thr);
            MethodStatement method = resolver.resolve(expression, traversalHierarchy);

            // Add to method usages, if method was found
            if (method != null) {
                method.getMethodUsages().add(expression);
            }
        }
    }
}
