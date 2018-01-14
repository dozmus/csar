package org.qmul.csar.code.postprocess.qualifiedname;

import org.qmul.csar.code.parse.java.statement.ClassStatement;
import org.qmul.csar.code.parse.java.statement.EnumStatement;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.code.parse.java.StatementVisitor;

final class TargetTypeSearcher extends StatementVisitor {

    private String[] targetName;
    private int matches = 0;
    private int nesting = -1;
    private boolean cancelled;

    public void resetState(String targetName) {
        this.targetName = targetName.split("\\.");
        matches = 0;
        nesting = -1;
        cancelled = false;
    }

    public void visitStatement(Statement statement) {
        if (isMatched() || cancelled) // TODO is early termination erroneous (optimization)?
            return;
        super.visitStatement(statement);
    }

    @Override
    public void visitClassStatement(ClassStatement statement) {
        nesting++;
        attemptMatch(statement.getDescriptor().getIdentifierName());
        super.visitClassStatement(statement);
        nesting--;
    }

    @Override
    public void visitEnumStatement(EnumStatement statement) {
        nesting++;
        attemptMatch(statement.getDescriptor().getIdentifierName());
        super.visitEnumStatement(statement);
        nesting--;
    }

    private void attemptMatch(IdentifierName identifierName) {
        if (nesting >= targetName.length) {
            cancelled = true;
        } else if (!isMatched() && identifierName.toString().equals(targetName[nesting])) {
            matches++; // XXX is error prone, check some stuff w this w diff nesting interleaving
        }
    }

    boolean isMatched() {
        return matches == targetName.length;
    }
}
