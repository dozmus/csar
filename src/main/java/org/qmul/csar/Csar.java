package org.qmul.csar;

import org.qmul.csar.code.AbstractProjectCodeParser;
import org.qmul.csar.code.search.AbstractProjectCodeSearcher;
import org.qmul.csar.code.search.ProjectCodeSearcher;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.qmul.csar.result.Result;
import org.qmul.csar.result.ResultFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * A code search and refactor tool instance.
 */
public final class Csar {

    private static final Logger LOGGER = LoggerFactory.getLogger(Csar.class);
    private final AbstractProjectCodeParser parser;
    private final ResultFormatter resultFormatter;
    private final String query;
    private CsarQuery csarQuery;
    private Map<Path, Statement> code;
    private List<Result> results;

    /**
     * Constructs a new {@link Csar} with the given arguments.
     * @param query the csar query to perform
     * @param parser the project code parser to use
     * @param resultFormatter the result formatter to use
     */
    public Csar(String query, AbstractProjectCodeParser parser, ResultFormatter resultFormatter) {
        this.query = query;
        this.parser = parser;
        this.resultFormatter = resultFormatter;
    }

    /**
     * Returns <tt>true</tt> if {@link #query} was parsed and assigned to {@link #csarQuery} successfully.
     * @return <tt>true</tt> if the query was parsed successfully.
     */
    public boolean parseQuery() {
        LOGGER.trace("Parsing query...");

        try {
            csarQuery = CsarQueryFactory.parse(query);
        } catch (Exception ex) {
            LOGGER.error("Failed to parse csar query because {}", ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Returns <tt>true</tt> if the code was parsed and the results assigned to {@link #code} successfully.
     * @return <tt>true</tt> if the code was parsed successfully.
     */
    public boolean parseCode() {
        LOGGER.trace("Parsing code...");
        code = parser.results();
        return !parser.errorOccurred();
    }

    public boolean searchCode() {
        AbstractProjectCodeSearcher searcher = new ProjectCodeSearcher(csarQuery, code); // TODO cons. inject this
        LOGGER.trace("Searching code...");
        List<Statement> tmpResults = searcher.search();

        // TODO remove temp printing below
        LOGGER.info("Search results (tmp):");
        LOGGER.info("amt:" + tmpResults.size());
        tmpResults.forEach(s -> LOGGER.info("Result: " + s.toPseudoCode()));

        // TODO convert tmpResults->results

        try {
            LOGGER.info("Search results:");
            LOGGER.info(resultFormatter.format(results)); // TODO move print to another method
        } catch (Exception ex) {
            LOGGER.error("error formatting search results: " + ex.getMessage());
        }
        return !searcher.errorOccurred();
    }
}
