package org.qmul.csar;

import org.qmul.csar.code.*;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.postprocess.JavaPostProcessor;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methods.use.MethodUseResolver;
import org.qmul.csar.code.java.postprocess.methods.overridden.OverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.java.refactor.JavaCodeRefactorer;
import org.qmul.csar.code.java.search.JavaCodeSearcher;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.code.parse.ProjectCodeParser;
import org.qmul.csar.code.refactor.ProjectCodeRefactorer;
import org.qmul.csar.code.refactor.RefactorTarget;
import org.qmul.csar.io.it.ProjectIteratorFactory;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.plugin.CsarPlugin;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.RefactorDescriptor;
import org.qmul.csar.code.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * The Java language csar plugin.
 */
public class CsarJavaPlugin implements CsarPlugin {

    private final List<CsarErrorListener> errorListeners = new ArrayList<>();
    private Map<Path, Statement> code;
    private List<RefactorTarget> refactorTargets;

    @Override
    public Map<Path, Statement> parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount)
            throws Exception {
        // Create iterator
        CodeParserFactory factory;

        try {
            factory = new CodeParserFactory(JavaCodeParser.class);
        } catch (InstantiationException | IllegalAccessException ex) {
            errorListeners.forEach(l -> l.fatalErrorInitializingParsing(ex));
            throw ex;
        }
        Iterator<Path> it = iterator(projectDirectory, narrowSearch, ignoreFile, factory);

        // Create parser
        ProjectCodeParser parser = new DefaultProjectCodeParser(factory, it, threadCount);
        errorListeners.forEach(parser::addErrorListener);
        code = parser.results();
        return code;
    }

    @Override
    public void postprocess(int threadCount) {
        // Create components
        QualifiedNameResolver qnr = new QualifiedNameResolver();
        TypeHierarchyResolver thr = new TypeHierarchyResolver(qnr);
        MethodQualifiedTypeResolver mqtr = new MethodQualifiedTypeResolver(qnr);
        OverriddenMethodsResolver omr = new OverriddenMethodsResolver(threadCount, qnr, thr);
        MethodUseResolver mur = new MethodUseResolver(qnr, thr);
        MethodCallTypeInstanceResolver mctir = new MethodCallTypeInstanceResolver(qnr, thr);

        // Create post-processor
        CodePostProcessor processor = new JavaPostProcessor(thr, mqtr, omr, mctir, mur);
        errorListeners.forEach(processor::addErrorListener); // XXX also attaches error listeners to its components
        processor.postprocess(code);
    }

    @Override
    public List<Result> search(CsarQuery csarQuery, int threadCount) {
        ProjectCodeSearcher searcher = new JavaCodeSearcher(threadCount);
        searcher.setCsarQuery(csarQuery);
        searcher.setIterator(code.entrySet().iterator());
        errorListeners.forEach(searcher::addErrorListener);
        refactorTargets = searcher.refactorTargets();
        return searcher.results();
    }

    @Override
    public List<Result> refactor(CsarQuery csarQuery, List<Result> searchResults, int threadCount) {
        RefactorDescriptor descriptor = csarQuery.getRefactorDescriptor().orElseThrow(IllegalArgumentException::new);

        // Create refactorer
        ProjectCodeRefactorer refactorer = new JavaCodeRefactorer(threadCount, true);
        refactorer.setRefactorDescriptor(descriptor);
        refactorer.setRefactorTargets(refactorTargets);
        errorListeners.forEach(refactorer::addErrorListener);
        return refactorer.results();
    }

    /**
     * Adds an error listener. Make sure you add all the error listeners before invoking methods of this class.
     *
     * @param errorListener the error listener
     */
    @Override
    public void addErrorListener(CsarErrorListener errorListener) {
        errorListeners.add(errorListener);
    }

    /**
     * Remove an error listener. Make sure you remove all the error listeners before invoking methods of this class.
     *
     * @param errorListener the error listener
     */
    @Override
    public void removeErrorListener(CsarErrorListener errorListener) {
        errorListeners.remove(errorListener);
    }

    private static Iterator<Path> iterator(Path projectDirectory, boolean narrowSearch, Path ignoreFile,
            CodeParserFactory factory) {
        if (Files.exists(ignoreFile)) {
            try {
                return ProjectIteratorFactory.createFilteredIterator(projectDirectory, narrowSearch, ignoreFile,
                        factory);
            } catch (IOException ignored) {
            }
        }
        return ProjectIteratorFactory.createFilteredIterator(projectDirectory, narrowSearch, factory);
    }
}
