package org.qmul.csar.code.java.postprocess.qualifiedname;

import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.statement.AnnotationStatement;
import org.qmul.csar.code.java.parse.statement.ClassStatement;
import org.qmul.csar.code.java.parse.statement.EnumStatement;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.Statement;

final class TargetTypeSearcher extends StatementVisitor {

    private String[] targetName;
    private boolean[] matches;
    private int nesting = -1;
    private Statement matchedStatement;

    public void resetState(String targetName) {
        this.targetName = targetName.split("\\.");
        matches = new boolean[this.targetName.length];
        nesting = -1;
        matchedStatement = null;
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
            matches[nesting] = false;
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
            matches[nesting] = false;
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
            matches[nesting] = false;
        }
        nesting--;
    }

    private boolean matches(Statement statement, IdentifierName name) {
        if (name.toString().equals(targetName[nesting])) {
            matches[nesting] = true;

            if (isMatched()) {
                matchedStatement = statement;
            }
            return true;
        }
        return false;
    }

    public boolean isMatched() {
        for (boolean b : matches) {
            if (!b)
                return false;
        }
        return true;
    }

    public Statement getMatchedStatement() {
        return matchedStatement;
    }
}
