package org.qmul;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.qmul.io.ProjectFileScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application main class.
 */
public final class Csar {

    private static final Logger LOGGER = LoggerFactory.getLogger(Csar.class);
    private final CsarContext ctx;
    private final ProjectFileScanner scanner;

    public Csar(CsarContext ctx) {
        this.ctx = ctx;
        this.scanner = new ProjectFileScanner(ctx);
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

            if (ctx.isPrintHelp()) {
                com.usage();
                System.exit(0);
            }
        } catch (ParameterException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        // Run csar
        Csar csar = new Csar(ctx);
        csar.init();
        csar.process();
    }

    private void init() {
        System.out.println("Initializing");
        scanner.scan();

        if (ctx.getCodeFiles().size() == 0) {
            System.err.println("No code files found");
            System.exit(0);
        }
    }

    private void process() {
        System.out.println("Processing");
        // TODO impl
    }
}
