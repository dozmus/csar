package org.qmul.csar.code.java.search;

import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.TargetDescriptor;
import org.qmul.csar.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A method definition searcher.
 */
public class MethodDefinitionSearcher implements Searcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodDefinitionSearcher.class);

    @Override
    public Result search(CsarQuery query, Path file, Statement statement) {
        TargetDescriptor targetDescriptor = query.getSearchTarget();

        // From Query
        if (skipped(query, file))
            return new Result(new ArrayList<>(), new ArrayList<>());

        // Search
        SearchStatementVisitor visitor = new SearchStatementVisitor(targetDescriptor);
        visitor.visitStatement(statement);

        // Aggregate and return results
        List<org.qmul.csar.code.Result> results = visitor.getResults().stream()
                .map(s -> methodStatementToResult(file, s))
                .collect(Collectors.toList());

        // Create result objects (these are not restricted by search domain, or the output would be incorrect)
        List<SerializableCode> resultObjects = new ArrayList<>();
        visitor.getResults().stream()
                .map(s -> (MethodStatement)s)
                .forEach(m -> {
                    resultObjects.add(m);
                    resultObjects.addAll(m.getMethodUsages());
                });
        return new Result(results, resultObjects);
    }

    /**
     * Returns if the current file should be skipped - this handles the fromQuery.
     */
    private boolean skipped(CsarQuery query, Path file) {
        if (query.getFromTarget().size() > 0) {
            String fileNameWithoutExt = StringUtils.fileNameWithoutExtension(file);
            boolean valid = false;

            for (String fromDomain : query.getFromTarget()) {
                if (fromDomain.equals(fileNameWithoutExt)) {
                    valid = true;
                    LOGGER.trace("Accepted: {}", fileNameWithoutExt);
                    break;
                }
            }

            if (!valid) {
                LOGGER.trace("Skipped {}", file);
                return true;
            }
        }
        return false;
    }

    private org.qmul.csar.code.Result methodStatementToResult(Path file, Statement s) {
        return new org.qmul.csar.code.Result(file, ((MethodStatement)s).getIdentifierFilePosition().getLineNumber(),
                s.toPseudoCode());
    }
}
