package org.qmul.csar.code.java;

import org.qmul.csar.code.java.parse.expression.*;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;

import java.util.List;

public abstract class StatementVisitor {

    public void visitStatement(Statement statement) {
        if (statement instanceof Annotation) {
            visitAnnotation((Annotation)statement);
            exitAnnotation((Annotation)statement);
        } else if (statement instanceof AnnotationStatement) {
            visitAnnotationStatement((AnnotationStatement)statement);
            exitAnnotationStatement((AnnotationStatement)statement);
        } else if (statement instanceof AssertStatement) {
            visitAssertStatement((AssertStatement)statement);
            exitAssertStatement((AssertStatement)statement);
        } else if (statement instanceof BlockStatement) {
            visitBlockStatement((BlockStatement)statement);
            exitBlockStatement((BlockStatement)statement);
        } else if (statement instanceof BreakStatement) {
            visitBreakStatement((BreakStatement)statement);
            exitBreakStatement((BreakStatement)statement);
        } else if (statement instanceof CatchStatement) {
            visitCatchStatement((CatchStatement)statement);
            exitCatchStatement((CatchStatement)statement);
        } else if (statement instanceof ClassStatement) {
            visitClassStatement((ClassStatement)statement);
            exitClassStatement((ClassStatement)statement);
        } else if (statement instanceof ConstructorStatement) {
            visitConstructorStatement((ConstructorStatement)statement);
            exitConstructorStatement((ConstructorStatement)statement);
        } else if (statement instanceof ContinueStatement) {
            visitContinueStatement((ContinueStatement)statement);
            exitContinueStatement((ContinueStatement)statement);
        } else if (statement instanceof DoWhileStatement) {
            visitDoWhileStatement((DoWhileStatement)statement);
            exitDoWhileStatement((DoWhileStatement)statement);
        } else if (statement instanceof EnumConstantStatement) {
            visitEnumConstantStatement((EnumConstantStatement)statement);
            exitEnumConstantStatement((EnumConstantStatement)statement);
        } else if (statement instanceof EnumStatement) {
            visitEnumStatement((EnumStatement)statement);
            exitEnumStatement((EnumStatement)statement);
        } else if (statement instanceof ThrowStatement) {
            visitThrowStatement((ThrowStatement)statement);
            exitThrowStatement((ThrowStatement)statement);
        } else if (statement instanceof TryWithResourcesStatement) {
            visitTryWithResourcesStatement((TryWithResourcesStatement)statement);
            exitTryWithResourcesStatement((TryWithResourcesStatement)statement);
        } else if (statement instanceof ExpressionStatement) {
            visitExpressionStatement((ExpressionStatement)statement);
            exitExpressionStatement((ExpressionStatement)statement);
        } else if (statement instanceof ForEachStatement) {
            visitForEachStatement((ForEachStatement)statement);
            exitForEachStatement((ForEachStatement)statement);
        } else if (statement instanceof ForStatement) {
            visitForStatement((ForStatement)statement);
            exitForStatement((ForStatement)statement);
        } else if (statement instanceof IfStatement) {
            visitIfStatement((IfStatement)statement);
            exitIfStatement((IfStatement)statement);
        } else if (statement instanceof ImportStatement) {
            visitImportStatement((ImportStatement)statement);
            exitImportStatement((ImportStatement)statement);
        } else if (statement instanceof InstanceVariableStatement) {
            visitInstanceVariableStatement((InstanceVariableStatement)statement);
            exitInstanceVariableStatement((InstanceVariableStatement)statement);
        } else if (statement instanceof LabelStatement) {
            visitLabelStatement((LabelStatement)statement);
            exitLabelStatement((LabelStatement)statement);
        } else if (statement instanceof LocalVariableStatement) {
            visitLocalVariableStatement((LocalVariableStatement)statement);
            exitLocalVariableStatement((LocalVariableStatement)statement);
        } else if (statement instanceof LocalVariableStatements) {
            visitLocalVariableStatements((LocalVariableStatements)statement);
            exitLocalVariableStatements((LocalVariableStatements)statement);
        } else if (statement instanceof MethodStatement) {
            visitMethodStatement((MethodStatement)statement);
            exitMethodStatement((MethodStatement)statement);
        } else if (statement instanceof PackageStatement) {
            visitPackageStatement((PackageStatement)statement);
            exitPackageStatement((PackageStatement)statement);
        } else if (statement instanceof ParameterVariableStatement) {
            visitParameterVariableStatement((ParameterVariableStatement)statement);
            exitParameterVariableStatement((ParameterVariableStatement)statement);
        } else if (statement instanceof ReturnStatement) {
            visitReturnStatement((ReturnStatement)statement);
            exitReturnStatement((ReturnStatement)statement);
        } else if (statement instanceof SemiColonStatement) {
            visitSemiColonStatement((SemiColonStatement)statement);
            exitSemiColonStatement((SemiColonStatement)statement);
        } else if (statement instanceof StaticBlockStatement) {
            visitStaticBlockStatement((StaticBlockStatement)statement);
            exitStaticBlockStatement((StaticBlockStatement)statement);
        } else if (statement instanceof SwitchLabelStatement) {
            visitSwitchLabelStatement((SwitchLabelStatement)statement);
            exitSwitchLabelStatement((SwitchLabelStatement)statement);
        } else if (statement instanceof SwitchStatement) {
            visitSwitchStatement((SwitchStatement)statement);
            exitSwitchStatement((SwitchStatement)statement);
        } else if (statement instanceof SynchronizedStatement) {
            visitSynchronizedStatement((SynchronizedStatement)statement);
            exitSynchronizedStatement((SynchronizedStatement)statement);
        } else if (statement instanceof CompilationUnitStatement) {
            visitTopLevelTypeStatement((CompilationUnitStatement)statement);
            exitTopLevelTypeStatement((CompilationUnitStatement)statement);
        } else if (statement instanceof TryStatement) {
            visitTryStatement((TryStatement)statement);
            exitTryStatement((TryStatement)statement);
        } else if (statement instanceof WhileStatement) {
            visitWhileStatement((WhileStatement)statement);
            exitWhileStatement((WhileStatement)statement);
        }
    }

    public void visitAnnotation(Annotation statement) {
        statement.getValue().ifPresent(this::visitAnnotationValue);
    }

    private void visitAnnotationValue(Annotation.Value value) {
        if (value instanceof Annotation.ExpressionValue) {
            visitExpression(((Annotation.ExpressionValue)value).getValue());
        } else if (value instanceof Annotation.Values) {
            for (Annotation.Value v : ((Annotation.Values)value).getValues()) {
                visitAnnotationValue(v);
            }
        } else if (value instanceof Annotation.AnnotationValue) {
            visitAnnotation(((Annotation.AnnotationValue)value).getValue());
        }
    }

    public void exitAnnotation(Annotation statement) {
    }

    public void visitAnnotationStatement(AnnotationStatement statement) {
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
    }

    public void exitAnnotationStatement(AnnotationStatement statement) {
    }

    public void visitAssertStatement(AssertStatement statement) {
        visitExpression(statement.getExpression());
        statement.getErrorMessageExpression().ifPresent(this::visitExpression);
    }

    public void exitAssertStatement(AssertStatement statement) {
    }

    public void visitBlockStatement(BlockStatement statement) {
        statement.getStatements().forEach(this::visitStatement);
    }

    public void exitBlockStatement(BlockStatement statement) {
    }

    public void visitBreakStatement(BreakStatement statement) {
    }

    public void exitBreakStatement(BreakStatement statement) {
    }

    public void visitCatchStatement(CatchStatement statement) {
        visitLocalVariableStatements(statement.getVariable());
        exitLocalVariableStatements(statement.getVariable());
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
    }

    public void exitCatchStatement(CatchStatement statement) {
    }

    public void visitClassStatement(ClassStatement statement) {
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
    }

    public void exitClassStatement(ClassStatement statement) {
    }

    public void visitConstructorStatement(ConstructorStatement statement) {
        visitParameterVariableStatements(statement.getParameters());
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
    }

    public void exitConstructorStatement(ConstructorStatement statement) {
    }

    public void visitContinueStatement(ContinueStatement statement) {
    }

    public void exitContinueStatement(ContinueStatement statement) {
    }

    public void visitDoWhileStatement(DoWhileStatement statement) {
        visitExpression(statement.getCondition());
        visitStatement(statement.getStatement());
    }

    public void exitDoWhileStatement(DoWhileStatement statement) {
    }

    public void visitEnumConstantStatement(EnumConstantStatement statement) {
        visitAnnotations(statement.getAnnotations());
        statement.getArguments().forEach(this::visitExpression);
        statement.getBlock().ifPresent(s -> {
            visitBlockStatement(s);
            exitBlockStatement(s);
        });
    }

    public void exitEnumConstantStatement(EnumConstantStatement statement) {
    }

    public void visitEnumStatement(EnumStatement statement) {
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
    }

    public void exitEnumStatement(EnumStatement statement) {
    }

    public void visitExpressionStatement(ExpressionStatement statement) {
        visitExpression(statement.getExpression());
    }

    public void exitExpressionStatement(ExpressionStatement statement) {
    }

    public void visitForEachStatement(ForEachStatement statement) {
        visitLocalVariableStatement(statement.getVariable());
        exitLocalVariableStatement(statement.getVariable());
        visitExpression(statement.getCollection());
        visitStatement(statement.getStatement());
    }

    public void exitForEachStatement(ForEachStatement statement) {
    }

    public void visitForStatement(ForStatement statement) {
        statement.getInitExpressions().forEach(this::visitExpression);
        statement.getInitVariables().ifPresent(l -> {
            visitLocalVariableStatements(l);
            exitLocalVariableStatements(l);
        });
        statement.getCondition().ifPresent(this::visitExpression);
        statement.getUpdateExpressions().forEach(this::visitExpression);
        visitStatement(statement.getStatement());
    }

    public void exitForStatement(ForStatement statement) {
    }

    public void visitIfStatement(IfStatement statement) {
        visitExpression(statement.getCondition());
        visitStatement(statement.getStatement());
        statement.getElseStatement().ifPresent(this::visitStatement);
    }

    public void exitIfStatement(IfStatement statement) {
    }

    public void visitImportStatement(ImportStatement statement) {
    }

    public void exitImportStatement(ImportStatement statement) {
    }

    public void visitInstanceVariableStatement(InstanceVariableStatement statement) {
        visitAnnotations(statement.getAnnotations());
        statement.getValueExpression().ifPresent(this::visitExpression);
    }

    public void exitInstanceVariableStatement(InstanceVariableStatement statement) {
    }

    public void visitLabelStatement(LabelStatement statement) {
    }

    public void exitLabelStatement(LabelStatement statement) {
    }

    public void visitLocalVariableStatement(LocalVariableStatement statement) {
        visitAnnotations(statement.getAnnotations());
        statement.getValueExpression().ifPresent(this::visitExpression);
    }

    public void exitLocalVariableStatement(LocalVariableStatement statement) {
    }

    public void visitLocalVariableStatements(LocalVariableStatements statement) {
        for (LocalVariableStatement s : statement.getLocals()) {
            visitLocalVariableStatement(s);
            exitLocalVariableStatement(s);
        }
    }

    public void exitLocalVariableStatements(LocalVariableStatements statement) {
    }

    public void visitMethodStatement(MethodStatement statement) {
        visitParameterVariableStatements(statement.getParameters());
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
    }

    public void exitMethodStatement(MethodStatement statement) {
    }

    public void visitPackageStatement(PackageStatement statement) {
        visitAnnotations(statement.getAnnotations());
    }

    public void exitPackageStatement(PackageStatement statement) {
    }

    public void visitParameterVariableStatement(ParameterVariableStatement statement) {
        visitAnnotations(statement.getAnnotations());
    }

    public void exitParameterVariableStatement(ParameterVariableStatement statement) {
    }

    public void visitReturnStatement(ReturnStatement statement) {
        statement.getExpression().ifPresent(this::visitExpression);
    }

    public void exitReturnStatement(ReturnStatement statement) {
    }

    public void visitSemiColonStatement(SemiColonStatement statement) {
    }

    public void exitSemiColonStatement(SemiColonStatement statement) {
    }

    public void visitStaticBlockStatement(StaticBlockStatement statement) {
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
    }

    public void exitStaticBlockStatement(StaticBlockStatement statement) {
    }

    public void visitSwitchLabelStatement(SwitchLabelStatement statement) {
        visitExpression(statement.getLabelExpression());
    }

    public void exitSwitchLabelStatement(SwitchLabelStatement statement) {
    }

    public void visitSwitchStatement(SwitchStatement statement) {
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
    }

    public void exitSwitchStatement(SwitchStatement statement) {
    }

    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        visitExpression(statement.getElement());
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
    }

    public void exitSynchronizedStatement(SynchronizedStatement statement) {
    }

    public void visitThrowStatement(ThrowStatement statement) {
        visitExpression(statement.getExpression());
    }

    public void exitThrowStatement(ThrowStatement statement) {
    }

    public void visitTopLevelTypeStatement(CompilationUnitStatement statement) {
        statement.getPackageStatement().ifPresent(pkg -> {
            visitPackageStatement(pkg);
            exitPackageStatement(pkg);
        });
        visitImports(statement.getImports());
        visitStatement(statement.getTypeStatement());
    }

    public void exitTopLevelTypeStatement(CompilationUnitStatement statement) {
    }

    public void visitTryStatement(TryStatement statement) {
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
        visitCatches(statement.getCatches());
        statement.getFinallyBlock().ifPresent(f -> {
            visitBlockStatement(f);
            exitBlockStatement(f);
        });
    }

    public void exitTryStatement(TryStatement statement) {
    }

    public void visitTryWithResourcesStatement(TryWithResourcesStatement statement) {
        visitLocalVariableStatements(statement.getResources());
        exitLocalVariableStatements(statement.getResources());
        visitTryStatement(statement);
        exitTryStatement(statement);
    }

    public void exitTryWithResourcesStatement(TryWithResourcesStatement statement) {
    }

    public void visitWhileStatement(WhileStatement statement) {
        visitExpression(statement.getCondition());
        visitStatement(statement.getStatement());
    }

    public void exitWhileStatement(WhileStatement statement) {
    }

    private void visitAnnotations(List<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            visitAnnotation(annotation);
            exitAnnotation(annotation);
        }
    }

    private void visitParameterVariableStatements(List<ParameterVariableStatement> parameters) {
        for (ParameterVariableStatement parameter : parameters) {
            visitParameterVariableStatement(parameter);
            exitParameterVariableStatement(parameter);
        }
    }

    private void visitImports(List<ImportStatement> imports) {
        for (ImportStatement s : imports) {
            visitImportStatement(s);
            exitImportStatement(s);
        }
    }

    private void visitCatches(List<CatchStatement> catches) {
        for (CatchStatement s : catches) {
            visitCatchStatement(s);
            exitCatchStatement(s);
        }
    }

    public void visitExpression(Expression expression) {
        if (expression instanceof ArrayAccessExpression) {
            visitArrayAccessExpression((ArrayAccessExpression)expression);
            exitArrayAccessExpression((ArrayAccessExpression)expression);
        } else if (expression instanceof ArrayExpression) {
            visitArrayExpression((ArrayExpression)expression);
            exitArrayExpression((ArrayExpression)expression);
        } else if (expression instanceof ArrayInitializationExpression) {
            visitArrayInitializationExpression((ArrayInitializationExpression)expression);
            exitArrayInitializationExpression((ArrayInitializationExpression)expression);
        } else if (expression instanceof BinaryExpression) {
            visitBinaryExpression((BinaryExpression)expression);
            exitBinaryExpression((BinaryExpression)expression);
        } else if (expression instanceof CastExpression) {
            visitCastExpression((CastExpression)expression);
            exitCastExpression((CastExpression)expression);
        } else if (expression instanceof InstantiateClassExpression) {
            visitInstantiateClassExpression((InstantiateClassExpression)expression);
            exitInstantiateClassExpression((InstantiateClassExpression)expression);
        } else if (expression instanceof LambdaExpression) {
            visitLambdaExpression((LambdaExpression)expression);
            exitLambdaExpression((LambdaExpression)expression);
        } else if (expression instanceof MethodCallExpression) {
            visitMethodCallExpression((MethodCallExpression)expression);
            exitMethodCallExpression((MethodCallExpression)expression);
        } else if (expression instanceof ParenthesisExpression) {
            visitParenthesisExpression((ParenthesisExpression)expression);
            exitParenthesisExpression((ParenthesisExpression)expression);
        } else if (expression instanceof PostfixedExpression) {
            visitPostfixedExpression((PostfixedExpression)expression);
            exitPostfixedExpression((PostfixedExpression)expression);
        } else if (expression instanceof PrefixedExpression) {
            visitPrefixedExpression((PrefixedExpression)expression);
            exitPrefixedExpression((PrefixedExpression)expression);
        } else if (expression instanceof SquareBracketsExpression) {
            visitSquareBracketsExpression((SquareBracketsExpression)expression);
            exitSquareBracketsExpression((SquareBracketsExpression)expression);
        } else if (expression instanceof TernaryExpression) {
            visitTernaryExpression((TernaryExpression)expression);
            exitTernaryExpression((TernaryExpression)expression);
        } else if (expression instanceof UnitExpression) {
            visitUnitExpression((UnitExpression)expression);
            exitUnitExpression((UnitExpression)expression);
        }
    }

    public void visitArrayAccessExpression(ArrayAccessExpression expression) {
        visitExpression(expression.getArray());
        visitExpression(expression.getIndex());
    }

    public void exitArrayAccessExpression(ArrayAccessExpression expression) {
    }

    public void visitArrayExpression(ArrayExpression expression) {
        expression.getExpressions().forEach(this::visitExpression);
    }

    public void exitArrayExpression(ArrayExpression expression) {
    }

    public void visitArrayInitializationExpression(ArrayInitializationExpression expression) {
        expression.getExpressions().forEach(this::visitExpression);
    }

    public void exitArrayInitializationExpression(ArrayInitializationExpression expression) {
    }

    public void visitBinaryExpression(BinaryExpression expression) {
        visitExpression(expression.getLeft());
        visitExpression(expression.getRight());
    }

    public void exitBinaryExpression(BinaryExpression expression) {
    }

    public void visitCastExpression(CastExpression expression) {
        visitExpression(expression.getExpression());
    }

    public void exitCastExpression(CastExpression expression) {
    }

    public void visitInstantiateClassExpression(InstantiateClassExpression expression) {
        expression.getArguments().forEach(this::visitExpression);
        expression.getBlock().ifPresent(this::visitStatement);
    }

    public void exitInstantiateClassExpression(InstantiateClassExpression expression) {
    }

    public void visitLambdaExpression(LambdaExpression expression) {
        if (expression.getParameter() instanceof LambdaParameter.ParameterVariables) {
            LambdaParameter.ParameterVariables parameterVariables
                    = (LambdaParameter.ParameterVariables) expression.getParameter();
            parameterVariables.getVariables().forEach(this::visitStatement);
        }
        visitStatement(expression.getValue());
    }

    public void exitLambdaExpression(LambdaExpression expression) {
    }

    public void visitPostfixedExpression(PostfixedExpression expression) {
        visitExpression(expression.getExpression());
    }

    public void exitPostfixedExpression(PostfixedExpression expression) {
    }

    public void visitParenthesisExpression(ParenthesisExpression expression) {
        visitExpression(expression.getExpression());
    }

    public void exitParenthesisExpression(ParenthesisExpression expression) {
    }

    public void visitMethodCallExpression(MethodCallExpression expression) {
        visitExpression(expression.getMethodName());
        expression.getArguments().forEach(this::visitExpression);
    }

    public void exitMethodCallExpression(MethodCallExpression expression) {
    }

    public void visitPrefixedExpression(PrefixedExpression expression) {
        visitExpression(expression.getExpression());
    }

    public void exitPrefixedExpression(PrefixedExpression expression) {
    }

    public void visitSquareBracketsExpression(SquareBracketsExpression expression) {
        expression.getExpression().ifPresent(this::visitExpression);
    }

    public void exitSquareBracketsExpression(SquareBracketsExpression expression) {
    }

    public void visitTernaryExpression(TernaryExpression expression) {
        visitExpression(expression.getCondition());
        visitExpression(expression.getValueIfTrue());
        visitExpression(expression.getValueIfFalse());
    }

    public void exitTernaryExpression(TernaryExpression expression) {
    }

    public void visitUnitExpression(UnitExpression expression) {
    }

    public void exitUnitExpression(UnitExpression expression) {
    }
}
