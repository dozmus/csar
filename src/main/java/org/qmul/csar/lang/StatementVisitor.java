package org.qmul.csar.lang;

import org.qmul.csar.code.parse.java.statement.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class StatementVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementVisitor.class);

    public void visitStatement(Statement statement) {
        LOGGER.trace("Visit: {}", statement.getClass().toString());

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
        } else if (statement instanceof TopLevelTypeStatement) {
            visitTopLevelTypeStatement((TopLevelTypeStatement)statement);
            exitTopLevelTypeStatement((TopLevelTypeStatement)statement);
        } else if (statement instanceof TryStatement) {
            visitTryStatement((TryStatement)statement);
            exitTryStatement((TryStatement)statement);
        } else if (statement instanceof WhileStatement) {
            visitWhileStatement((WhileStatement)statement);
            exitWhileStatement((WhileStatement)statement);
        }
    }

    public void visitAnnotation(Annotation statement) {
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
    }

    public void exitAssertStatement(AssertStatement statement) {
    }

    public void visitBlockStatement(BlockStatement statement) {
        for (Statement s : statement.getStatements()) {
            visitStatement(s);
        }
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
        visitStatement(statement.getStatement());
    }

    public void exitDoWhileStatement(DoWhileStatement statement) {
    }

    public void visitEnumConstantStatement(EnumConstantStatement statement) {
        statement.getBlock().ifPresent(s -> {
            visitBlockStatement(s);
            exitBlockStatement(s);
        });
        visitAnnotations(statement.getAnnotations());
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
    }

    public void exitExpressionStatement(ExpressionStatement statement) {
    }

    public void visitForEachStatement(ForEachStatement statement) {
        visitLocalVariableStatement(statement.getVariable());
        exitLocalVariableStatement(statement.getVariable());
        visitStatement(statement.getStatement());
    }

    public void exitForEachStatement(ForEachStatement statement) {
    }

    public void visitForStatement(ForStatement statement) {
        statement.getInitVariables().ifPresent(l -> {
            visitLocalVariableStatements(l);
            exitLocalVariableStatements(l);
        });
        visitStatement(statement.getStatement());
    }

    public void exitForStatement(ForStatement statement) {
    }

    public void visitIfStatement(IfStatement statement) {
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
    }

    public void exitInstanceVariableStatement(InstanceVariableStatement statement) {
    }

    public void visitLabelStatement(LabelStatement statement) {
    }

    public void exitLabelStatement(LabelStatement statement) {
    }

    public void visitLocalVariableStatement(LocalVariableStatement statement) {
        visitAnnotations(statement.getAnnotations());
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
        visitBlockStatement(statement.getBlock());
        exitBlockStatement(statement.getBlock());
    }

    public void exitSynchronizedStatement(SynchronizedStatement statement) {
    }

    public void visitThrowStatement(ThrowStatement statement) {
    }

    public void exitThrowStatement(ThrowStatement statement) {
    }

    public void visitTopLevelTypeStatement(TopLevelTypeStatement statement) {
        statement.getPackageStatement().ifPresent(pkg -> {
            visitPackageStatement(pkg);
            exitPackageStatement(pkg);
        });
        visitImports(statement.getImports());
        visitStatement(statement.getTypeStatement());
    }

    public void exitTopLevelTypeStatement(TopLevelTypeStatement statement) {
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
}
