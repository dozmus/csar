package org.qmul.csar.code.search;

import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptor.MethodDescriptor;
import org.qmul.csar.query.CsarQuery;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A single-threaded project code searcher.
 */
public class ProjectCodeSearcher {
    // TODO multi-thread
    // TODO document

    public List<Statement> search(CsarQuery query, Map<Path, Statement> code) {
        Descriptor targetDescriptor = query.getSearchTarget().getDescriptor();

        if (targetDescriptor instanceof MethodDescriptor) {
            return methodSearch(query, code);
        } else {
            throw new UnsupportedOperationException("invalid search target: " + targetDescriptor.getClass().getName());
        }
    }

    private List<Statement> methodSearch(CsarQuery query, Map<Path, Statement> code) {
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

    public boolean errorOccurred() {
        return false;
    }
}
