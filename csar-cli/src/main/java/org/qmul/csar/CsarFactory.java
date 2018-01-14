package org.qmul.csar;

import org.qmul.csar.code.DefaultProjectCodeParserErrorListener;
import org.qmul.csar.code.parse.ProjectCodeParser;

import java.io.IOException;
import java.nio.file.Path;

public final class CsarFactory {

    /**
     * Creates a {@link Csar} with the details contained in the argument. The {@link ProjectCodeParser} created will
     * have the {@link DefaultProjectCodeParserErrorListener} set as its error listener.
     *
     * @param ctx the details of the instance to create
     * @return a {@link Csar} with the details contained in the argument
     * @throws IOException if an I/O error occurs while reading an ignore file
     */
    public static Csar create(CsarContext ctx) throws IOException {
        Path ignoreFile = ctx.getIgnoreFile();
        int threads = ctx.getThreads();
        return new Csar(ctx.getQuery(), threads, ctx.isBenchmarking(), ctx.getProjectDirectory(),
                ctx.isNarrowSearch(), ignoreFile);
    }
}
