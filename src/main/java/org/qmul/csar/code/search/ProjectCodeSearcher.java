package org.qmul.csar.code.search;

import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptor.MethodDescriptor;
import org.qmul.csar.query.CsarQuery;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectCodeSearcher extends AbstractProjectCodeSearcher {

    private final CsarQuery query;
    private final Map<Path, Statement> code;

    public ProjectCodeSearcher(CsarQuery query, Map<Path, Statement> code) {
        this.query = query;
        this.code = code;
    }

    @Override
    public List<Statement> search() {
        Descriptor desc = query.getSearchTarget().getDescriptor();

        if (desc instanceof MethodDescriptor) {
            return methodSearch();
        } else {
            throw new UnsupportedOperationException("invalid search target: " + desc.getClass().getName());
        }
    }

    private List<Statement> methodSearch() {
        List<Statement> results = new ArrayList<>();

        for (Map.Entry<Path, Statement> entry : code.entrySet()) {
            Path path = entry.getKey();
            Statement statement = entry.getValue();

            // Search
            SearchStatementVisitor visitor = new SearchStatementVisitor(query.getSearchTarget());
            visitor.visit(statement);

            // TODO: containsQuery
            // TODO: fromTarget

            // Add results
            results.addAll(visitor.getResults());
        }
        return results;
    }

    @Override
    public boolean errorOccurred() {
        return false;
    }
}
