package org.qmul.csar;

import org.qmul.csar.code.AbstractProjectCodeParser;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;

/**
 * A code search and refactor tool instance.
 */
public final class Csar {

    /**
     * The URL of this project on the internet.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Csar.class);
    private final AbstractProjectCodeParser parser;
    private final String query;
    private CsarQuery csarQuery;
    private Map<Path, Statement> code;

    /**
     * Constructs a new {@link Csar} with the given arguments.
     * @param query the csar query to perform
     * @param parser the project code parser to use
     */
    public Csar(String query, AbstractProjectCodeParser parser) {
        this.query = query;
        this.parser = parser;
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
}
