package org.qmul.csar;

import org.qmul.csar.code.DefaultProjectCodeParserErrorListener;
import org.qmul.csar.code.parse.ProjectCodeParser;
import org.qmul.csar.code.search.ProjectCodeSearcher;
import org.qmul.csar.io.ProjectIteratorFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

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
        Iterator<Path> it;

        if (Files.exists(ignoreFile)) {
            it = ProjectIteratorFactory.createFilteredIterator(ctx.getProjectDirectory(), ctx.isNarrowSearch(),
                    ignoreFile);
        } else {
            it = ProjectIteratorFactory.createFilteredIterator(ctx.getProjectDirectory(), ctx.isNarrowSearch());
        }
        ProjectCodeParser parser = new ProjectCodeParser(it, threads);
        parser.setErrorListener(new DefaultProjectCodeParserErrorListener());
        ProjectCodeSearcher searcher = new ProjectCodeSearcher(threads);
        return new Csar(ctx.getQuery(), parser, searcher, ctx.getResultFormatter());
    }
}
