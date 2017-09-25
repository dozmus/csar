package org.qmul.csar;

import org.qmul.csar.code.AbstractProjectCodeParser;
import org.qmul.csar.code.ProjectCodeParser;
import org.qmul.csar.io.ProjectIterator;
import org.qmul.csar.io.ProjectIteratorFactory;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;
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
     * Constructs a new Csar, with a standard {@link ProjectIterator}.
     * @param ctx the details of should be performed
     */
    public Csar(CsarContext ctx) {
        Iterator<Path> it = ProjectIteratorFactory.create(ctx.getDirectory(), ctx.isNarrowSearch());
        AbstractProjectCodeParser parser = new ProjectCodeParser(it, ctx.getThreads());
        this.query = ctx.getQuery();
        this.parser = parser;
    }

    /**
     * Constructs a new Csar.
     * @param query the csar query to perform
     * @param parser the project code parser to use
     */
    public Csar(String query, AbstractProjectCodeParser parser) {
        this.query = query;
        this.parser = parser;
    }

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

    public boolean parseCode() {
        LOGGER.trace("Parsing code...");
        code = parser.results();
        return !parser.errorOccurred();
    }
}
