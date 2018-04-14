package org.qmul.csar.code.java.search;

import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.query.TargetDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Statement} searcher, this currently only works on methods.
 */
class SearchStatementVisitor extends StatementVisitor {

    private final TargetDescriptor target;
    private final List<Statement> results = new ArrayList<>();

    /**
     * Creates a new SearchStatementVisitor.
     *
     * @param target the descriptor to search for
     */
    SearchStatementVisitor(TargetDescriptor target) {
        this.target = target;
    }

    @Override
    public void visitMethodStatement(MethodStatement statement) {
        super.visitMethodStatement(statement);

        if (target.getDescriptor() instanceof MethodDescriptor) {
            MethodDescriptor desc = (MethodDescriptor)target.getDescriptor();

            if (statement.getDescriptor().lenientEquals(desc)) {
                results.add(statement);
            }
        }
    }

    public List<Statement> getResults() {
        return results;
    }
}
