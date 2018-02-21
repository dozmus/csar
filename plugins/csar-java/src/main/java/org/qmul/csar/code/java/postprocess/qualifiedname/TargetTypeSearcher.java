package org.qmul.csar.code.java.postprocess.qualifiedname;

import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.statement.AnnotationStatement;
import org.qmul.csar.code.java.parse.statement.ClassStatement;
import org.qmul.csar.code.java.parse.statement.EnumStatement;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.Statement;

final class TargetTypeSearcher extends StatementVisitor {

    private String[] targetName;
    private int matches = 0;
    private int nesting = 0;
    private Statement matchedStatement;
    private boolean notFound;

    public void resetState(String targetName) {
        this.targetName = targetName.split("\\.");
        matches = 0;
        nesting = -1;
        matchedStatement = null;
        notFound = false;
    }

    public void visitStatement(Statement statement) {
        if (nesting >= targetName.length || isMatched())
            return;
        super.visitStatement(statement);
    }

    @Override
    public void visitClassStatement(ClassStatement statement) {
        nesting++;
        boolean match = matches(statement, statement.getDescriptor().getIdentifierName());

        if (match) {
            super.visitClassStatement(statement);
        } else {
            notFound = true;
        }
        nesting--;
    }

    @Override
    public void visitEnumStatement(EnumStatement statement) {
        nesting++;
        boolean match = matches(statement, statement.getDescriptor().getIdentifierName());

        if (match) {
            super.visitEnumStatement(statement);
        } else {
            notFound = true;
        }
        nesting--;
    }

    @Override
    public void visitAnnotationStatement(AnnotationStatement statement) {
        nesting++;
        boolean match = matches(statement, statement.getDescriptor().getIdentifierName());

        if (match) {
            super.visitAnnotationStatement(statement);
        } else {
            notFound = true;
        }
        nesting--;
    }

    private boolean matches(Statement statement, IdentifierName name) {
        if (name.toString().equals(targetName[nesting])) {
            matches++;

            if (isMatched()) {
                matchedStatement = statement;
            }
            return true;
        }
        return false;
    }

    public boolean isMatched() {
        return !notFound && matches == targetName.length;
    }

    public Statement getMatchedStatement() {
        return matchedStatement;
    }
}
