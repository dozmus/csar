package org.qmul.csar.code.java.postprocess.methodcalls.typeinstances;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.expression.BinaryExpression;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.BlockStatement;
import org.qmul.csar.code.java.parse.statement.ImportStatement;
import org.qmul.csar.code.java.parse.statement.PackageStatement;
import org.qmul.csar.code.java.postprocess.methods.use.TraversalHierarchy;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.java.postprocess.util.ExpressionTypeResolver;
import org.qmul.csar.code.java.postprocess.util.TypeInstance;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A post-processor which attaches to each {@link MethodCallExpression} a {@link TypeInstance} for its source and all
 * of its parameters.
 */
public class MethodCallTypeInstanceResolver implements CodePostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodCallTypeInstanceResolver.class);
    private final QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();
    private TypeHierarchyResolver typeHierarchyResolver;

    public void postprocess(Map<Path, Statement> code) {
        LOGGER.info("Starting...");
        long startTime = System.currentTimeMillis();
        int methodCallsProcessed = 0;
        MethodCallExpressionVisitor visitor = new MethodCallExpressionVisitor(code);

        for (Map.Entry<Path, Statement> file : code.entrySet()) {
            Path path = file.getKey();
            Statement statement = file.getValue();

            visitor.reset();
            visitor.setPath(path);
            visitor.visitStatement(statement);
            methodCallsProcessed += visitor.methodCallsProcessed;
        }

        // Log completion message
        LOGGER.debug("Processed {} method call expressions in {}ms", methodCallsProcessed,
                (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");
    }

    public void setTypeHierarchyResolver(TypeHierarchyResolver typeHierarchyResolver) {
        this.typeHierarchyResolver = typeHierarchyResolver;
    }

    private final class MethodCallExpressionVisitor extends StatementVisitor {

        private final TraversalHierarchy traversalHierarchy = new TraversalHierarchy();
        private final Map<Path, Statement> code;
        private Path path;
        private int methodCallsProcessed;

        public MethodCallExpressionVisitor(Map<Path, Statement> code) {
            this.code = code;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public void reset() {
            methodCallsProcessed = 0;
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
            LOGGER.trace("Resolving method type instances: {} (line={})", expression.toPseudoCode(),
                    expression.getLineNumber());
            resolve(expression);
        }

        public void resolve(MethodCallExpression expression) {
            methodCallsProcessed++;
            Expression name = expression.getMethodName();
            List<Expression> args = expression.getArguments();

            // Set method source if applicable
            if (name instanceof BinaryExpression) {
                LOGGER.trace("Found binary expression method name");
                BinaryExpression exp = (BinaryExpression) name;
                TypeInstance methodSource = resolve(exp, true);
                expression.setMethodSource(methodSource);
                LOGGER.trace("MethodSource = {}", (methodSource == null ? "null" : methodSource.getType()));
            }

            // Set argument types
            List<TypeInstance> argsTypes = args.stream().map(t -> resolve(t, false)).collect(Collectors.toList());
            expression.setArgumentTypes(Collections.unmodifiableList(argsTypes));
            LOGGER.trace("Argument Types = {}", argsTypes.stream()
                    .map(t -> t == null ? "null" : t.getType()).collect(Collectors.toList()));
        }

        private TypeInstance resolve(Expression expr, boolean resolvingMethodIdentifierMode) {
            // Set context
            TypeStatement topLevelType = traversalHierarchy.getFirstTypeStatement();
            TypeStatement currentType = traversalHierarchy.getLastTypeStatement();
            List<ImportStatement> imports = traversalHierarchy.getImports();
            Optional<PackageStatement> currentPackage = traversalHierarchy.getPackageStatement();
            BlockStatement currentContext = traversalHierarchy.currentContext();
            return new ExpressionTypeResolver(resolvingMethodIdentifierMode).resolve(path, code, topLevelType,
                    currentType, imports, currentPackage, currentContext, qualifiedNameResolver, traversalHierarchy,
                    typeHierarchyResolver, expr);
        }
    }
}
