package org.qmul.csar.code.search;

import org.qmul.csar.code.parse.java.statement.MethodStatement;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.StatementVisitor;
import org.qmul.csar.lang.descriptor.MethodDescriptor;
import org.qmul.csar.query.TargetDescriptor;

import java.util.ArrayList;
import java.util.List;

public class SearchStatementVisitor extends StatementVisitor {

    private final TargetDescriptor target;
    private List<Statement> results = new ArrayList<>();

    public SearchStatementVisitor(TargetDescriptor target) {
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
