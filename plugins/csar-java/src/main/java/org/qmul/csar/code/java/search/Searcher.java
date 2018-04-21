package org.qmul.csar.code.java.search;

import org.qmul.csar.code.RefactorTarget;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * A specific searcher.
 */
public interface Searcher {

    /**
     * Returns the results of searching the argument statement, which is from the argument file.
     */
    Result search(CsarQuery query, Path file, Statement statement) throws IOException;

    /**
     * A searcher's result.
     */
    class Result {

        private final List<org.qmul.csar.result.Result> results;
        private final List<RefactorTarget> refactorTargets;

        public Result(List<org.qmul.csar.result.Result> results, List<RefactorTarget> refactorTargets) {
            this.results = results;
            this.refactorTargets = refactorTargets;
        }

        public List<org.qmul.csar.result.Result> getResults() {
            return results;
        }

        public List<RefactorTarget> getRefactorTargets() {
            return refactorTargets;
        }
    }
}
