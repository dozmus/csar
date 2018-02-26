package org.qmul.csar.code.java.postprocess.methodusage;

import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.postprocess.util.MethodResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class MethodUsageStatementVisitor extends StatementVisitor {

    private final TraversalHierarchy traversalHierarchy = new TraversalHierarchy();
    private final TypeHierarchyResolver typeHierarchyResolver;
    private final QualifiedNameResolver qualifiedNameResolver;
    private Map<Path, Statement> code;
    private Path path;

    public MethodUsageStatementVisitor(Map<Path, Statement> code, Path path,
            TypeHierarchyResolver typeHierarchyResolver, QualifiedNameResolver qualifiedNameResolver) {
        this.code = code;
        this.path = path;
        this.typeHierarchyResolver = typeHierarchyResolver;
        this.qualifiedNameResolver = qualifiedNameResolver;
    }

    public MethodUsageStatementVisitor(Map<Path, Statement> code, Path path,
            TypeHierarchyResolver typeHierarchyResolver) {
        this(code, path, typeHierarchyResolver, new QualifiedNameResolver());
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
        System.out.println("--------------------------------------------------------");
        System.out.println("resolve method call:" + expression.toPseudoCode());


        MethodResolver resolver = new MethodResolver(path, code, qualifiedNameResolver, typeHierarchyResolver);
        MethodStatement method = resolver.resolve(expression, traversalHierarchy);

        if (method != null) {
            method.getMethodUsages().add(expression);
        }
    }
}
