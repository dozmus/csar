package org.qmul.csar.code.java.postprocess.methodproc;

import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.postprocess.methodusage.TraversalHierarchy;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class MethodCallExpressionVisitor extends StatementVisitor {

    private final TraversalHierarchy traversalHierarchy = new TraversalHierarchy();
    private final QualifiedNameResolver qualifiedNameResolver;
    private final TypeHierarchyResolver typeHierarchyResolver;
    private Map<Path, Statement> code;
    private Path path;

    public MethodCallExpressionVisitor(Map<Path, Statement> code, Path path, QualifiedNameResolver qualifiedNameResolver,
            TypeHierarchyResolver typeHierarchyResolver) {
        this.code = code;
        this.path = path;
        this.qualifiedNameResolver = qualifiedNameResolver;
        this.typeHierarchyResolver = typeHierarchyResolver;
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
        System.out.println("=============================================");
        System.out.println("resolve method type instances:" + expression.toPseudoCode());

        MethodCallProcessor methodCallProcessor = new MethodCallProcessor(path, code, traversalHierarchy,
                qualifiedNameResolver, typeHierarchyResolver);
        methodCallProcessor.resolve(expression);
    }
}
