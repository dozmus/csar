package org.qmul.csar;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.qmul.csar.io.ProjectCodeIterator;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application main class.
 */
public final class Csar {

    private static String PROJECT_URL = "https://github.research.its.qmul.ac.uk/ec15116/csar";
    private static final Logger LOGGER = LoggerFactory.getLogger(Csar.class);
    private final CsarContext ctx;
    private final ProjectCodeIterator it;

    public Csar(CsarContext ctx) {
        this.ctx = ctx;
        this.it = new ProjectCodeIterator(ctx);
    }

    /**
     * Application main method. Parses command-line arguments and executes actions corresponding to them.
     *
     * @see {@link CsarContext}
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
        it.init();

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
