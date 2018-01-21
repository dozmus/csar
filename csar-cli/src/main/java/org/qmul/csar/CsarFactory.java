package org.qmul.csar;

/**
 * A factory for {@link Csar}.
 */
final class CsarFactory {

    /**
     * Creates a new instance of {@link Csar} with the details contained in the argument, and the standard
     * {@link CliCsarErrorListener}.
     *
     * @param ctx the details of the instance to create
     * @return a {@link Csar} with the details contained in the argument
     */
    static Csar create(CsarContext ctx) {
        Csar csar = new Csar(ctx.getQuery(), ctx.getThreads(), ctx.getProjectDirectory(), ctx.isNarrowSearch(),
                ctx.getIgnoreFile());

        // Add standard cli error listener
        csar.addErrorListener(new CliCsarErrorListener());
        return csar;
    }
}
