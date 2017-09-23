package org.qmul.csar;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.impl.SimpleLogger;

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
                com.usage();
                System.exit(0);
            }
        } catch (ParameterException ex) {
            System.err.println("Error parsing command-line arguments: " + ex.getMessage());
            System.exit(1);
        }

        // Run csar
        Csar csar = new Csar(ctx);
        csar.init();
        csar.process();
    }
}
