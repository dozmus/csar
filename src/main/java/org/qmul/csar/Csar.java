package org.qmul.csar;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.qmul.csar.io.PathIterator;
import org.qmul.csar.io.ProjectIterator;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A code search and refactorer.
 */
public final class Csar {

    /**
     * The URL of this project on the internet.
     */
    private static String PROJECT_URL = "https://github.research.its.qmul.ac.uk/ec15116/csar";
    private static final Logger LOGGER = LoggerFactory.getLogger(Csar.class);
    private final CsarContext ctx;
    private final PathIterator it;

    /**
     * Constructs a new Csar, with a standard {@link ProjectIterator}.
     * @param ctx the details of should be performed
     */
    public Csar(CsarContext ctx) {
        this(ctx, new ProjectIterator(ctx.getDirectory()));
    }

    /**
     * Constructs a new Csar.
     * @param ctx the details of what it should perform
     * @param it the project code iterator to use
     */
    public Csar(CsarContext ctx, ProjectIterator it) {
        this.ctx = ctx;
        this.it = it;
    }

    /**
     * Application main method.
     * This parses command-line arguments and stores them in an instance of {@link CsarContext}.
     * Then executes the actions which they describe.
     *
     * @param args application command-line arguments
     * @see CsarContext
     */
    public static void main(String[] args) {
        // Parse command-line arguments
        CsarContext ctx = new CsarContext();

        try {
            JCommander com = JCommander.newBuilder()
                    .addObject(ctx)
                    .programName("java -jar csar.jar")
                    .build();
            com.parse(args);

            if (ctx.isPrintProjectUrl()) {
                System.out.println(PROJECT_URL);
                System.exit(0);
            }

            if (ctx.isPrintHelp()) {
                com.usage();
                System.exit(0);
            }
        } catch (ParameterException ex) {
            LOGGER.error(ex.getMessage());
            System.exit(1);
        }

        // Run csar
        Csar csar = new Csar(ctx);
        csar.init();
        csar.process();
    }

    private void init() {
        LOGGER.info("Initializing");

        if (it instanceof ProjectIterator) {
            ((ProjectIterator)it).init();
        }

        if (!it.hasNext()) {
            LOGGER.error("No code files found");
            System.exit(0);
        }
    }

    private void process() {
        LOGGER.info("Processing");

        // Parse query
        CsarQuery csarQuery;

        try {
            csarQuery = CsarQueryFactory.parse(ctx.getQuery());
        } catch (Exception ex) {
            LOGGER.error("Failed to parse csar query because {}", ex.getMessage());
            return;
        }

        // Parse code
        CodeParser parser = new CodeParser(it, ctx.getThreads());

        try {
            parser.runAndWait();
        } catch (InterruptedException e) {
            LOGGER.error("Failed to wait because {}", e.getMessage());
        }

        // TODO impl
        LOGGER.info("Finished");
    }
}
