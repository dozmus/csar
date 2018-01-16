package org.qmul.csar;

/**
 * A factory for {@link Csar}.
 */
final class CsarFactory {

    /**
     * Creates a new instance of {@link Csar} with the details contained in the argument.
     *
     * @param ctx the details of the instance to create
     * @return a {@link Csar} with the details contained in the argument
     */
    static Csar create(CsarContext ctx) {
        return new Csar(ctx.getQuery(), ctx.getThreads(), ctx.getProjectDirectory(), ctx.isNarrowSearch(),
                ctx.getIgnoreFile());
    }
}
