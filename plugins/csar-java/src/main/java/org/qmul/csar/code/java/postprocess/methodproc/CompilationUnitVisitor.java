package org.qmul.csar.code.java.postprocess.methodproc;

import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.java.postprocess.methodusage.TraversalHierarchy;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class CompilationUnitVisitor extends StatementVisitor {

    private final TraversalHierarchy traversalHierarchy = new TraversalHierarchy();
    private final MethodCallExpressionVisitor expressionVisitor;
    private Map<Path, Statement> code;
    private Path path;

    public CompilationUnitVisitor(Map<Path, Statement> code, Path path, QualifiedNameResolver qualifiedNameResolver,
            TypeHierarchyResolver typeHierarchyResolver) {
        this.expressionVisitor = new MethodCallExpressionVisitor(traversalHierarchy, path, code, qualifiedNameResolver,
                typeHierarchyResolver);
        this.code = code;
        this.path = path;
    }

    @Override
    public void visitStatement(Statement statement) {
        traversalHierarchy.addLast(statement);
        super.visitStatement(statement);
        traversalHierarchy.removeLast();
    }

    @Override
    public void visitAnnotation(Annotation statement) {
        statement.getValue().ifPresent(this::visitAnnotationValue);
        super.visitAnnotation(statement);
    }

    private void visitAnnotationValue(Annotation.Value value) {
        if (value instanceof Annotation.ExpressionValue) {
            expressionVisitor.visitExpression(((Annotation.ExpressionValue)value).getValue());
        } else if (value instanceof Annotation.Values) {
            for (Annotation.Value v : ((Annotation.Values)value).getValues()) {
                visitAnnotationValue(v);
            }
        } else if (value instanceof Annotation.AnnotationValue) {
            visitAnnotation(((Annotation.AnnotationValue)value).getValue());
        }
    }

    @Override
    public void exitAnnotation(Annotation statement) {
        super.exitAnnotation(statement);
    }

    @Override
    public void visitAnnotationStatement(AnnotationStatement statement) {
        super.visitAnnotationStatement(statement);
    }

    @Override
    public void exitAnnotationStatement(AnnotationStatement statement) {
        super.exitAnnotationStatement(statement);
    }

    @Override
    public void visitAssertStatement(AssertStatement statement) {
        expressionVisitor.visitExpression(statement.getExpression());
        statement.getErrorMessageExpression().ifPresent(expressionVisitor::visitExpression);
        super.visitAssertStatement(statement);
    }

    @Override
    public void exitAssertStatement(AssertStatement statement) {
        super.exitAssertStatement(statement);
    }

    @Override
    public void visitBlockStatement(BlockStatement statement) {
        super.visitBlockStatement(statement);
    }

    @Override
    public void exitBlockStatement(BlockStatement statement) {
        super.exitBlockStatement(statement);
    }

    @Override
    public void visitBreakStatement(BreakStatement statement) {
        super.visitBreakStatement(statement);
    }

    @Override
    public void exitBreakStatement(BreakStatement statement) {
        super.exitBreakStatement(statement);
    }

    @Override
    public void visitCatchStatement(CatchStatement statement) {
        super.visitCatchStatement(statement);
    }

    @Override
    public void exitCatchStatement(CatchStatement statement) {
        super.exitCatchStatement(statement);
    }

    @Override
    public void visitClassStatement(ClassStatement statement) {
        super.visitClassStatement(statement);
    }

    @Override
    public void exitClassStatement(ClassStatement statement) {
        super.exitClassStatement(statement);
    }

    @Override
    public void visitConstructorStatement(ConstructorStatement statement) {
        super.visitConstructorStatement(statement);
    }

    @Override
    public void exitConstructorStatement(ConstructorStatement statement) {
        super.exitConstructorStatement(statement);
    }

    @Override
    public void visitContinueStatement(ContinueStatement statement) {
        super.visitContinueStatement(statement);
    }

    @Override
    public void exitContinueStatement(ContinueStatement statement) {
        super.exitContinueStatement(statement);
    }

    @Override
    public void visitDoWhileStatement(DoWhileStatement statement) {
        super.visitDoWhileStatement(statement);
    }

    @Override
    public void exitDoWhileStatement(DoWhileStatement statement) {
        expressionVisitor.visitExpression(statement.getCondition());
        super.exitDoWhileStatement(statement);
    }

    @Override
    public void visitEnumConstantStatement(EnumConstantStatement statement) {
        super.visitEnumConstantStatement(statement);
    }

    @Override
    public void exitEnumConstantStatement(EnumConstantStatement statement) {
        for (Expression e : statement.getArguments()) {
            expressionVisitor.visitExpression(e);
        }
        super.exitEnumConstantStatement(statement);
    }

    @Override
    public void visitEnumStatement(EnumStatement statement) {
        super.visitEnumStatement(statement);
    }

    @Override
    public void exitEnumStatement(EnumStatement statement) {
        super.exitEnumStatement(statement);
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        expressionVisitor.visitExpression(statement.getExpression());
        super.visitExpressionStatement(statement);
    }

    @Override
    public void exitExpressionStatement(ExpressionStatement statement) {
        super.exitExpressionStatement(statement);
    }

    @Override
    public void visitForEachStatement(ForEachStatement statement) {
        expressionVisitor.visitExpression(statement.getCollection());
        super.visitForEachStatement(statement);
    }

    @Override
    public void exitForEachStatement(ForEachStatement statement) {
        super.exitForEachStatement(statement);
    }

    @Override
    public void visitForStatement(ForStatement statement) {
        statement.getCondition().ifPresent(expressionVisitor::visitExpression);

        for (Expression e : statement.getInitExpressions()) {
            expressionVisitor.visitExpression(e);
        }

        for (Expression e : statement.getUpdateExpressions()) {
            expressionVisitor.visitExpression(e);
        }
        super.visitForStatement(statement);
    }

    @Override
    public void exitForStatement(ForStatement statement) {
        super.exitForStatement(statement);
    }

    @Override
    public void visitIfStatement(IfStatement statement) {
        expressionVisitor.visitExpression(statement.getCondition());
        super.visitIfStatement(statement);
    }

    @Override
    public void exitIfStatement(IfStatement statement) {
        super.exitIfStatement(statement);
    }

    @Override
    public void visitImportStatement(ImportStatement statement) {
        super.visitImportStatement(statement);
    }

    @Override
    public void exitImportStatement(ImportStatement statement) {
        super.exitImportStatement(statement);
    }

    @Override
    public void visitInstanceVariableStatement(InstanceVariableStatement statement) {
        statement.getValueExpression().ifPresent(expressionVisitor::visitExpression);
        super.visitInstanceVariableStatement(statement);
    }

    @Override
    public void exitInstanceVariableStatement(InstanceVariableStatement statement) {
        super.exitInstanceVariableStatement(statement);
    }

    @Override
    public void visitLabelStatement(LabelStatement statement) {
        super.visitLabelStatement(statement);
    }

    @Override
    public void exitLabelStatement(LabelStatement statement) {
        super.exitLabelStatement(statement);
    }

    @Override
    public void visitLocalVariableStatement(LocalVariableStatement statement) {
        statement.getValueExpression().ifPresent(expressionVisitor::visitExpression);
        super.visitLocalVariableStatement(statement);
    }

    @Override
    public void exitLocalVariableStatement(LocalVariableStatement statement) {
        super.exitLocalVariableStatement(statement);
    }

    @Override
    public void visitLocalVariableStatements(LocalVariableStatements statement) {
        super.visitLocalVariableStatements(statement);
    }

    @Override
    public void exitLocalVariableStatements(LocalVariableStatements statement) {
        super.exitLocalVariableStatements(statement);
    }

    @Override
    public void visitMethodStatement(MethodStatement statement) {
        super.visitMethodStatement(statement);
    }

    @Override
    public void exitMethodStatement(MethodStatement statement) {
        super.exitMethodStatement(statement);
    }

    @Override
    public void visitPackageStatement(PackageStatement statement) {
        super.visitPackageStatement(statement);
    }

    @Override
    public void exitPackageStatement(PackageStatement statement) {
        super.exitPackageStatement(statement);
    }

    @Override
    public void visitParameterVariableStatement(ParameterVariableStatement statement) {
        super.visitParameterVariableStatement(statement);
    }

    @Override
    public void exitParameterVariableStatement(ParameterVariableStatement statement) {
        super.exitParameterVariableStatement(statement);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        statement.getExpression().ifPresent(expressionVisitor::visitExpression);
        super.visitReturnStatement(statement);
    }

    @Override
    public void exitReturnStatement(ReturnStatement statement) {
        super.exitReturnStatement(statement);
    }

    @Override
    public void visitSemiColonStatement(SemiColonStatement statement) {
        super.visitSemiColonStatement(statement);
    }

    @Override
    public void exitSemiColonStatement(SemiColonStatement statement) {
        super.exitSemiColonStatement(statement);
    }

    @Override
    public void visitStaticBlockStatement(StaticBlockStatement statement) {
        super.visitStaticBlockStatement(statement);
    }

    @Override
    public void exitStaticBlockStatement(StaticBlockStatement statement) {
        super.exitStaticBlockStatement(statement);
    }

    @Override
    public void visitSwitchLabelStatement(SwitchLabelStatement statement) {
        super.visitSwitchLabelStatement(statement);
    }

    @Override
    public void exitSwitchLabelStatement(SwitchLabelStatement statement) {
        expressionVisitor.visitExpression(statement.getLabelExpression());
        super.exitSwitchLabelStatement(statement);
    }

    @Override
    public void visitSwitchStatement(SwitchStatement statement) {
        super.visitSwitchStatement(statement);
    }

    @Override
    public void exitSwitchStatement(SwitchStatement statement) {
        super.exitSwitchStatement(statement);
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        expressionVisitor.visitExpression(statement.getElement());
        super.visitSynchronizedStatement(statement);
    }

    @Override
    public void exitSynchronizedStatement(SynchronizedStatement statement) {
        super.exitSynchronizedStatement(statement);
    }

    @Override
    public void visitThrowStatement(ThrowStatement statement) {
        expressionVisitor.visitExpression(statement.getExpression());
        super.visitThrowStatement(statement);
    }

    @Override
    public void exitThrowStatement(ThrowStatement statement) {
        super.exitThrowStatement(statement);
    }

    @Override
    public void visitTopLevelTypeStatement(CompilationUnitStatement statement) {
        super.visitTopLevelTypeStatement(statement);
    }

    @Override
    public void exitTopLevelTypeStatement(CompilationUnitStatement statement) {
        super.exitTopLevelTypeStatement(statement);
    }

    @Override
    public void visitTryStatement(TryStatement statement) {
        super.visitTryStatement(statement);
    }

    @Override
    public void exitTryStatement(TryStatement statement) {
        super.exitTryStatement(statement);
    }

    @Override
    public void visitTryWithResourcesStatement(TryWithResourcesStatement statement) {
        super.visitTryWithResourcesStatement(statement);
    }

    @Override
    public void exitTryWithResourcesStatement(TryWithResourcesStatement statement) {
        super.exitTryWithResourcesStatement(statement);
    }

    @Override
    public void visitWhileStatement(WhileStatement statement) {
        expressionVisitor.visitExpression(statement.getCondition());
        super.visitWhileStatement(statement);
    }

    @Override
    public void exitWhileStatement(WhileStatement statement) {
        super.exitWhileStatement(statement);
    }
}
