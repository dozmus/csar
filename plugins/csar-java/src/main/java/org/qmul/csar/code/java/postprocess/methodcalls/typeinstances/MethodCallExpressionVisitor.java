package org.qmul.csar.code.java.postprocess.methodcalls.typeinstances;

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

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        resolve(expression);
    }

    public void resolve(MethodCallExpression expression) {
        Expression name = expression.getMethodName();
        List<Expression> args = expression.getArguments();

        // Set method source if applicable
        if (name instanceof BinaryExpression) {
            System.out.println("Found binary expression method name");
            BinaryExpression exp = (BinaryExpression)name;
            expression.setMethodSource(resolve(exp, true));
            System.out.println("MethodSource="
                    + (expression.getMethodSource() == null ? "null" : expression.getMethodSource().getType()));
        }

        // Set argument types
        List<TypeInstance> argsTypes = args.stream().map(t -> resolve(t, false)).collect(Collectors.toList());
        expression.setArgumentTypes(Collections.unmodifiableList(argsTypes));
        System.out.println("ArgumentTypes=" + argsTypes.stream()
                .map(t -> t == null ? "null" : t.getType()).collect(Collectors.toList()));
    }

    private TypeInstance resolve(Expression expr, boolean resolvingMethodIdentifierMode) {
        // Set context
        TypeStatement topLevelType = traversalHierarchy.getFirstTypeStatement();
        TypeStatement currentType = traversalHierarchy.getLastTypeStatement();
        List<ImportStatement> imports = traversalHierarchy.getImports();
        Optional<PackageStatement> currentPackage = traversalHierarchy.getPackageStatement();
        BlockStatement currentContext = traversalHierarchy.currentContext();
        return new ExpressionTypeResolver(resolvingMethodIdentifierMode).resolve(path, code, topLevelType, currentType,
                imports, currentPackage, currentContext, qualifiedNameResolver, traversalHierarchy,
                typeHierarchyResolver, expr);
    }
}
