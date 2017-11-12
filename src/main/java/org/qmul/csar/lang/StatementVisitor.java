package org.qmul.csar.lang;

import org.qmul.csar.code.parse.java.statement.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public abstract class StatementVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementVisitor.class);

    public void visit(Statement statement) {
        LOGGER.trace("Visit: {}", statement.getClass().toString());

        if (statement instanceof TopLevelTypeStatement) {
            visitTopLevelTypeStatement((TopLevelTypeStatement)statement);
            exitTopLevelTypeStatement((TopLevelTypeStatement)statement);
        } else if (statement instanceof ClassStatement) {
            visitClassStatement((ClassStatement)statement);
            exitClassStatement((ClassStatement)statement);
        } else if (statement instanceof MethodStatement) {
            visitMethodStatement((MethodStatement)statement);
            exitMethodStatement((MethodStatement)statement);
        } else if (statement instanceof EnumStatement) {
            visitEnumStatement((EnumStatement)statement);
            exitEnumStatement((EnumStatement)statement);
        }
        // TODO let user pick what should happen if unsupported node encountered, optimally we should support them all
    }

    private void visitTopLevelTypeStatement(TopLevelTypeStatement statement) {
        visitPackageStatement(statement.getPackageStatement());
        visitImportStatements(statement.getImports());
        visit(statement.getTypeStatement());
    }

    private void exitTopLevelTypeStatement(TopLevelTypeStatement statement) {
    }

    public void visitEnumStatement(EnumStatement statement) {
        visitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
    }

    public void exitEnumStatement(EnumStatement statement) {
    }

    public void visitClassStatement(ClassStatement statement) {
        visitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
    }

    public void exitClassStatement(ClassStatement statement) {
    }

    public void visitMethodStatement(MethodStatement statement) {
        visitParameterVariableStatements(statement.getParams());
        visitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
    }

    public void exitMethodStatement(MethodStatement statement) {
    }

    public void visitBlockStatement(BlockStatement statement) {
        for (Statement st : statement.getStatements()) {
            visit(st);
        }
    }

    public void visitParameterVariableStatements(List<ParameterVariableStatement> statements) {
    }

    public void visitAnnotations(List<Annotation> statement) {
    }

    private void visitImportStatements(List<ImportStatement> imports) {
    }

    private void visitPackageStatement(Optional<PackageStatement> statement) {
    }
}
