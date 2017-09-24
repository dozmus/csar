package org.qmul.csar;

import org.qmul.csar.io.ProjectIterator;
import org.qmul.csar.io.ProjectIteratorFactory;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;

/**
 * A code search and refactorer.
 */
public final class Csar {

    /**
     * The URL of this project on the internet.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Csar.class);
    private final CsarContext ctx;
    private final Iterator<Path> it;

    /**
     * Constructs a new Csar, with a standard {@link ProjectIterator}.
     * @param ctx the details of should be performed
     */
    public Csar(CsarContext ctx) {
        this(ctx, ProjectIteratorFactory.create(ctx.getDirectory(), ctx.isNarrowSearch()));
    }

    /**
     * Constructs a new Csar.
     * @param ctx the details of what it should perform
     * @param it the project code iterator to use
     */
    public Csar(CsarContext ctx, Iterator<Path> it) {
        this.ctx = ctx;
        this.it = it;
    }

    public void init() {
        LOGGER.info("Initializing");

        if (!it.hasNext()) {
            LOGGER.error("No code files found");
            System.exit(0);
        }
    }

    public void process() {
        LOGGER.info("Processing");

        // Parse query
        LOGGER.trace("Parsing query...");
        CsarQuery csarQuery;

        try {
            csarQuery = CsarQueryFactory.parse(ctx.getQuery());
        } catch (Exception ex) {
            LOGGER.error("Failed to parse csar query because {}", ex.getMessage());
            return;
        }

        // Parse code
        LOGGER.trace("Parsing code...");
        CodeParser parser = new CodeParser(it, ctx.getThreads());

        try {
            parser.runAndWait();
        } catch (InterruptedException e) {
            LOGGER.error("Failed to wait because {}", e.getMessage());
        }

        // TODO search, refactor, print results
        LOGGER.info("Finished");
    }
}
