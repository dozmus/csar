package org.qmul.csar;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.qmul.csar.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import java.util.List;

/**
 * Application entry-point, contains the main method. All calls to {@link System#exit(int)} in the project appear here.
 */
public final class Main {

    /**
     * The project website URL.
     */
    private static final String PROJECT_URL = "https://github.research.its.qmul.ac.uk/ec15116/csar";

    public static final int EXIT_CODE_SUCCESS = 0;
    public static final int EXIT_CODE_ERROR_PARSING_CLI_ARGUMENTS = 1;
    public static final int EXIT_CODE_ERROR_PARSING_CSAR_QUERY = 2;
    public static final int EXIT_CODE_ERROR_PARSING_CODE = 3;
    public static final int EXIT_CODE_ERROR_SEARCHING_CODE = 4;
    public static final int EXIT_CODE_INITIALIZING = 5;
    public static final int EXIT_CODE_ERROR_POSTPROCESSING_CODE = 6;
    public static final int EXIT_CODE_ERROR_FORMATTING_SEARCH_RESULTS = 7;
    public static final int EXIT_CODE_ERROR_FORMATTING_REFACTOR_RESULTS = 8;
    public static final int EXIT_CODE_ERROR_REFACTORING_CODE = 9;

    /**
     * Application main method.
     * <p>
     * This parses command-line arguments and stores them in an instance of {@link CsarContext}.
     * Then, it constructs an appropriate instance of {@link Csar} and executes this.
     * It will use the {@link LoggingCsarErrorListener}.
     *
     * @param args application command-line arguments
     * @see CsarContext
     * @see CsarFactory
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

            // Set logging level
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, ctx.getLogLevel().toString());

            // Print project url
            if (ctx.isPrintProjectUrl()) {
                System.out.println(PROJECT_URL);
                System.exit(EXIT_CODE_SUCCESS);
            }

            // Print help
            if (ctx.isPrintHelp()) {
                printUsage();
                System.exit(EXIT_CODE_SUCCESS);
            }
        } catch (ParameterException ex) {
            Logger logger = LoggerFactory.getLogger(Main.class);
            logger.error("Error parsing command-line arguments: {}", ex.getMessage());
            printUsage();
            System.exit(EXIT_CODE_ERROR_PARSING_CLI_ARGUMENTS);
        }

        // Run
        new Main().main(ctx);
    }

    /**
     * This is an extension of {@link #main(String[])} because once any {@link Logger} is created, all following
     * instantiations of it will have the same logging level. Thus, the logger instance in this class is not
     * <tt>static</tt>, and this method exists so that it can be used once configured.
     */
    private void main(CsarContext ctx) {
        Logger logger = LoggerFactory.getLogger(Main.class);

        // Run csar
        logger.info("Starting...");
        Csar csar = CsarFactory.create(ctx);
        csar.init();
        csar.parseQuery();
        csar.parseCode();
        csar.postprocess();
        csar.searchCode();
        csar.refactorCode();

        try {
            List<Result> results = csar.getSearchResults();
            System.out.println("Search results (" + results.size() + " found):");
            System.out.println(ctx.getResultFormatter().format(results));
        } catch (Exception ex) {
            logger.error("Error formatting search results: {}", ex.getMessage());
            logger.debug("Error formatting search results.", ex);
            System.exit(EXIT_CODE_ERROR_FORMATTING_SEARCH_RESULTS);
        }

        try {
            List<Result> results = csar.getRefactorResults();

            if (results != null) { // only print if a refactor happened
                System.out.println("Refactor results (" + results.size() + " found):");
                System.out.println(ctx.getResultFormatter().format(results));
            }
        } catch (Exception ex) {
            logger.error("Error formatting refactor results: {}", ex.getMessage());
            logger.debug("Error formatting refactor results.", ex);
            System.exit(EXIT_CODE_ERROR_FORMATTING_REFACTOR_RESULTS);
        }

        // Fall-back: successful
        System.exit(EXIT_CODE_SUCCESS);
    }

    /**
     * Prints CLI usage. This is hard-coded and should be updated as the CLI changes.
     * @see CsarContext
     */
    private static void printUsage() {
        String usage = "Usage: java -jar csar.jar [options] search-query\n"
                + "  Options:\n"
                + "    --threads, -t\n"
                + "      Thread count (default: 1)\n"
                + "    --log-level\n"
                + "      Log level (default: INFO)\n"
                + "      Possible Values (most restrictive to least): ERROR, WARN, INFO, DEBUG, TRACE\n"
                + "    --format, -f\n"
                + "      Output format (default: PlainText)\n"
                + "      Possible Values: PlainText, JSON\n"
                + "    --output, -o\n"
                + "      Output file name\n"
                + "    --narrow-search\n"
                + "      Narrow search domain (default: true)\n"
                + "    --ignore-file\n"
                + "      Ignore file (default: .csarignore)\n"
                + "    --project-url, --url\n"
                + "      Print project URL\n"
                + "    --help, -h\n"
                + "      Print help information";
        System.out.println(usage);
    }
}
