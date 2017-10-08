package org.qmul.csar;

import org.qmul.csar.code.parse.ProjectCodeParser;
import org.qmul.csar.code.search.ProjectCodeSearcher;
import org.qmul.csar.io.ProjectIteratorFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public final class CsarFactory {

    /**
     * Creates a {@link Csar} with the details contained in the argument.
     *
     * @param ctx the details of the instance to create
     * @return a {@link Csar} with the details contained in the argument
     * @throws IOException if an I/O error occurs while reading an ignore file
     */
    public static Csar create(CsarContext ctx) throws IOException {
        Iterator<Path> it;
        Path ignoreFile = ctx.getIgnoreFile();

        if (Files.exists(ignoreFile)) {
            it = ProjectIteratorFactory.createFilteredIterator(ctx.getProjectDirectory(), ctx.isNarrowSearch(),
                    ignoreFile);
        } else {
            it = ProjectIteratorFactory.createFilteredIterator(ctx.getProjectDirectory(), ctx.isNarrowSearch());
        }
        return new Csar(ctx.getQuery(), new ProjectCodeParser(it, ctx.getThreads()), new ProjectCodeSearcher(),
                ctx.getResultFormatter());
    }
}
