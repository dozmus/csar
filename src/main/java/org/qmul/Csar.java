package org.qmul;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Application main class.
 */
public final class Csar {

    /**
     * Application main method. Parses command-line arguments and executes actions corresponding to them.
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

        // TODO implement processing
        // TODO check if input list values can be fooled
    }
}
