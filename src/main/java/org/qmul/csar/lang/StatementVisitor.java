package org.qmul.csar.lang;

import org.qmul.csar.code.parse.java.statement.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class StatementVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementVisitor.class);

    public void visit(Statement statement) {
        LOGGER.trace("Visit: {}", statement.getClass().toString());

        if (statement instanceof ClassStatement) {
            visitClassStatement((ClassStatement)statement);
        } else if (statement instanceof MethodStatement) {
            visitMethodStatement((MethodStatement)statement);
        } else if (statement instanceof EnumStatement) {
            visitEnumStatement((EnumStatement)statement);
        }
        // TODO let user pick what should happen if unsupported node encountered, optimally we should support them all
    }

    public void visitEnumStatement(EnumStatement statement) {
        visitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
    }

    public void visitClassStatement(ClassStatement statement) {
        visitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
    }

    public void visitMethodStatement(MethodStatement statement) {
        visitParameterVariableStatements(statement.getParams());
        visitBlockStatement(statement.getBlock());
        visitAnnotations(statement.getAnnotations());
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
}
