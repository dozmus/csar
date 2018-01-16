package org.qmul.csar;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

/**
 * Application entry-point, contains the main method. All calls to {@link System#exit(int)} in the project appear here.
 */
public final class Main {

    /**
     * The project website URL.
     */
    private static final String PROJECT_URL = "https://github.research.its.qmul.ac.uk/ec15116/csar";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Application main method.
     * <p>
     * This parses command-line arguments and stores them in an instance of {@link CsarContext}.
     * Then, it constructs an appropriate instance of {@link Csar} and executes this.
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
                System.exit(0);
            }

            // Print help
            if (ctx.isPrintHelp()) {
                printUsage();
                System.exit(0);
            }
        } catch (ParameterException ex) {
            LOGGER.error("Error parsing command-line arguments: {}", ex.getMessage());
            printUsage();
            System.exit(1);
        }

        // Run csar
        Csar csar = CsarFactory.create(ctx);
        exitIfFalse(csar.init(), 5);
        exitIfFalse(csar.parseQuery(), 2);
        exitIfFalse(csar.parseCode(), 3);
        exitIfFalse(csar.postprocess(), 6);
        exitIfFalse(csar.searchCode(), 4);

        try {
            LOGGER.info("Search results:");
            LOGGER.info(ctx.getResultFormatter().format(csar.getResults()));
        } catch (Exception ex) {
            LOGGER.error("Error formatting search results: " + ex.getMessage());
            System.exit(7);
        }

        // TODO refactor

        // Fall-back: successful
        System.exit(0);
    }

    /**
     * If the argument boolean is <tt>false</tt>, then this calls {@link System#exit(int)} with the argument integer.
     *
     * @param value the argument boolean
     * @param exitCode the exit code
     */
    private static void exitIfFalse(boolean value, int exitCode) {
        if (!value) {
            System.exit(exitCode);
        }
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
