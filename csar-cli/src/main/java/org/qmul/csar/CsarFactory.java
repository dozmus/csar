package org.qmul.csar;

import java.io.IOException;

/**
 * A factory for {@link Csar}.
 */
final class CsarFactory {

    /**
     * Creates a new instance of {@link Csar} with the details contained in the argument.
     *
     * @param ctx the details of the instance to create
     * @return a {@link Csar} with the details contained in the argument
     * @throws IOException if an I/O error occurs while reading an ignore file
     */
    static Csar create(CsarContext ctx) throws IOException {
        return new Csar(ctx.getQuery(), ctx.getThreads(), ctx.getProjectDirectory(), ctx.isNarrowSearch(),
                ctx.getIgnoreFile());
    }
}
