package org.qmul.csar;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.impl.SimpleLogger;

import java.io.IOException;

/**
 * Application entry-point, contains the main method. All calls to {@link System#exit(int)} in the project appear here.
 */
public final class Main {

    /**
     * The project website URL.
     */
    private static final String PROJECT_URL = "https://github.research.its.qmul.ac.uk/ec15116/csar";

    /**
     * Application main method.
     * <p>
     * This parses command-line arguments and stores them in an instance of {@link CsarContext}.
     * Then, it executes the actions which they describe.
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
            System.err.println("Error parsing command-line arguments: " + ex.getMessage());
            printUsage();
            System.exit(1);
        }

        // Run csar
        Csar csar = null;

        try {
            csar = CsarFactory.create(ctx);
        } catch (IOException ex) {
            System.err.println("Error reading ignore file because " + ex.getMessage());
            System.exit(5);
        }

        if (!csar.parseQuery()) {
            System.exit(2);
        }

        if (!csar.parseCode()) {
            System.exit(3);
        }

        csar.postProcess();

        if (!csar.searchCode()) {
            System.exit(4);
        }
        csar.printResults();

        // TODO refactor, print results
    }

    /**
     * Prints CLI usage. This is hardcoded and should be updated as the CLI changes.
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
                + "    --benchmark\n"
                + "      Print benchmarking values (default: false)\n"
                + "    --project-url, --url\n"
                + "      Print project URL\n"
                + "    --help, -h\n"
                + "      Print help information";
        System.out.println(usage);
    }
}
