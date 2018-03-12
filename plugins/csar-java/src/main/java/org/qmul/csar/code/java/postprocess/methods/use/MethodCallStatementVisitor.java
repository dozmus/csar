package org.qmul.csar.code.java.postprocess.methods.use;

import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.postprocess.util.MethodResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

public class MethodCallStatementVisitor extends StatementVisitor {

    private final Logger LOGGER = LoggerFactory.getLogger(MethodCallStatementVisitor.class);
    private final TraversalHierarchy traversalHierarchy = new TraversalHierarchy();
    private final TypeHierarchyResolver typeHierarchyResolver;
    private final QualifiedNameResolver qualifiedNameResolver;
    private final Map<Path, Statement> code;
    private Path path;

    public MethodCallStatementVisitor(Map<Path, Statement> code, TypeHierarchyResolver typeHierarchyResolver,
            QualifiedNameResolver qualifiedNameResolver) {
        this.code = code;
        this.typeHierarchyResolver = typeHierarchyResolver;
        this.qualifiedNameResolver = qualifiedNameResolver;
    }

    public void setPath(Path path) {
        this.path = path;
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
