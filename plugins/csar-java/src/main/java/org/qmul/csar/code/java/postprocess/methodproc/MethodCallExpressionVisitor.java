package org.qmul.csar.code.java.postprocess.methodproc;

import org.qmul.csar.code.java.ExpressionVisitor;
import org.qmul.csar.code.java.parse.expression.*;
import org.qmul.csar.code.java.postprocess.methodusage.TraversalHierarchy;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class MethodCallExpressionVisitor extends ExpressionVisitor {

    private final TraversalHierarchy traversalHierarchy;
    private final Path path;
    private final Map<Path, Statement> code;
    private final QualifiedNameResolver qualifiedNameResolver;
    private final TypeHierarchyResolver typeHierarchyResolver;

    public MethodCallExpressionVisitor(TraversalHierarchy traversalHierarchy, Path path, Map<Path, Statement> code,
            QualifiedNameResolver qualifiedNameResolver, TypeHierarchyResolver typeHierarchyResolver) {
        this.traversalHierarchy = traversalHierarchy;
        this.path = path;
        this.code = code;
        this.qualifiedNameResolver = qualifiedNameResolver;
        this.typeHierarchyResolver = typeHierarchyResolver;
    }

    @Override
    public void visitArrayAccessExpression(ArrayAccessExpression expression) {
        super.visitArrayAccessExpression(expression);
    }

    @Override
    public void exitArrayAccessExpression(ArrayAccessExpression expression) {
        super.exitArrayAccessExpression(expression);
    }

    @Override
    public void visitArrayExpression(ArrayExpression expression) {
        super.visitArrayExpression(expression);
    }

    @Override
    public void exitArrayExpression(ArrayExpression expression) {
        super.exitArrayExpression(expression);
    }

    @Override
    public void visitArrayInitializationExpression(ArrayInitializationExpression expression) {
        super.visitArrayInitializationExpression(expression);
    }

    @Override
    public void exitArrayInitializationExpression(ArrayInitializationExpression expression) {
        super.exitArrayInitializationExpression(expression);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        super.visitBinaryExpression(expression);
    }

    @Override
    public void exitBinaryExpression(BinaryExpression expression) {
        super.exitBinaryExpression(expression);
    }

    @Override
    public void visitCastExpression(CastExpression expression) {
        super.visitCastExpression(expression);
    }

    @Override
    public void exitCastExpression(CastExpression expression) {
        super.exitCastExpression(expression);
    }

    @Override
    public void visitInstantiateClassExpression(InstantiateClassExpression expression) {
        super.visitInstantiateClassExpression(expression);
    }

    @Override
    public void exitInstantiateClassExpression(InstantiateClassExpression expression) {
        super.exitInstantiateClassExpression(expression);
    }

    @Override
    public void visitLambdaExpression(LambdaExpression expression) {
        super.visitLambdaExpression(expression);
    }

    @Override
    public void exitLambdaExpression(LambdaExpression expression) {
        super.exitLambdaExpression(expression);
    }

    @Override
    public void visitPostfixedExpression(PostfixedExpression expression) {
        super.visitPostfixedExpression(expression);
    }

    @Override
    public void exitPostfixedExpression(PostfixedExpression expression) {
        super.exitPostfixedExpression(expression);
    }

    @Override
    public void visitParenthesisExpression(ParenthesisExpression expression) {
        super.visitParenthesisExpression(expression);
    }

    @Override
    public void exitParenthesisExpression(ParenthesisExpression expression) {
        super.exitParenthesisExpression(expression);
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression expression) {
        super.visitMethodCallExpression(expression);
        resolveMethodCall(expression);
    }

    @Override
    public void exitMethodCallExpression(MethodCallExpression expression) {
        super.exitMethodCallExpression(expression);
    }

    @Override
    public void visitPrefixedExpression(PrefixedExpression expression) {
        super.visitPrefixedExpression(expression);
    }

    @Override
    public void exitPrefixedExpression(PrefixedExpression expression) {
        super.exitPrefixedExpression(expression);
    }

    @Override
    public void visitSquareBracketsExpression(SquareBracketsExpression expression) {
        super.visitSquareBracketsExpression(expression);
    }

    @Override
    public void exitSquareBracketsExpression(SquareBracketsExpression expression) {
        super.exitSquareBracketsExpression(expression);
    }

    @Override
    public void visitTernaryExpression(TernaryExpression expression) {
        super.visitTernaryExpression(expression);
    }

    @Override
    public void exitTernaryExpression(TernaryExpression expression) {
        super.exitTernaryExpression(expression);
    }

    @Override
    public void visitUnitExpression(UnitExpression expression) {
        super.visitUnitExpression(expression);
    }

    @Override
    public void exitUnitExpression(UnitExpression expression) {
        super.exitUnitExpression(expression);
    }

    private void resolveMethodCall(MethodCallExpression expression) {
        System.out.println("=============================================");
        System.out.println("resolve method's type stuff:" + expression.toPseudoCode());

        MethodCallProcessor methodCallProcessor = new MethodCallProcessor(path, code, traversalHierarchy,
                qualifiedNameResolver, typeHierarchyResolver);
        methodCallProcessor.resolve(expression);
    }
}
