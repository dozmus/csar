package org.qmul.csar;

import org.qmul.csar.code.DefaultProjectCodeParserErrorListener;
import org.qmul.csar.code.parse.ProjectCodeParser;
import org.qmul.csar.code.postprocess.CodeAnalysisUtils;
import org.qmul.csar.code.postprocess.methodusage.MethodUsageResolver;
import org.qmul.csar.code.postprocess.overriddenmethods.OverriddenMethodsResolver;
import org.qmul.csar.code.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.postprocess.typehierarchy.TypeHierarchyResolver;
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
        ProjectCodeParser parser = new ProjectCodeParser(it, threads, ctx.isBenchmarking());
        parser.setErrorListener(new DefaultProjectCodeParserErrorListener());
        ProjectCodeSearcher searcher = new ProjectCodeSearcher(threads);

        // Code analysis utils, we share the QualifiedNameResolver instance to speed up OverriddenMethodsResolver
        QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();
        TypeHierarchyResolver typeHierarchyResolver = new TypeHierarchyResolver(qualifiedNameResolver,
                ctx.isBenchmarking());
        OverriddenMethodsResolver overriddenMethodsResolver = new OverriddenMethodsResolver(qualifiedNameResolver,
                ctx.isBenchmarking());
        MethodUsageResolver methodUsageResolver = new MethodUsageResolver();
        CodeAnalysisUtils codeAnalysisUtils = new CodeAnalysisUtils(typeHierarchyResolver, overriddenMethodsResolver,
                methodUsageResolver);
        return new Csar(ctx.getQuery(), parser, searcher, ctx.getResultFormatter(), codeAnalysisUtils);
    }
}
