package org.qmul.csar.code.java;

import org.qmul.csar.CsarJavaPlugin;
import org.qmul.csar.code.CodeBase;
import org.qmul.csar.code.Result;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.postprocess.JavaPostProcessor;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.methods.overridden.OverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.methods.overridden.SelectiveOverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methods.use.MethodUseResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.DefaultTypeHierarchyResolver;
import org.qmul.csar.code.java.refactor.JavaCodeRefactorer;
import org.qmul.csar.code.java.search.JavaCodeSearcher;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.code.parse.ProjectCodeParser;
import org.qmul.csar.code.postprocess.CodePostProcessor;
import org.qmul.csar.code.refactor.ProjectCodeRefactorer;
import org.qmul.csar.code.refactor.writer.DummyRefactorChangeWriter;
import org.qmul.csar.code.search.ProjectCodeSearcher;
import org.qmul.csar.io.it.ProjectIteratorFactory;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.query.CsarQuery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 * A java plugin which does not use caching, and does not write the refactor results to the files.
 * This is so that the tests will continue to work afterwards (if they change these tests will only work once).
 */
public class JavaTestCsarPlugin extends CsarJavaPlugin {

    private CodeBase code;
    private List<SerializableCode> searchResultObjects;
    private DefaultTypeHierarchyResolver thr;

    @Override
    public CodeBase parse(Path projectDirectory, Path csarDirectory, boolean narrowSearch, Path ignoreFile,
                          boolean noCache, int threadCount) throws Exception {
        CodeParserFactory factory = new CodeParserFactory(JavaCodeParser.class);

        // Create iterator
        Iterator<Path> it = iterator(projectDirectory, narrowSearch, ignoreFile, factory);

        // Create parser
        ProjectCodeParser parser = new DefaultProjectCodeParser(factory, it, threadCount);
        code = parser.results();
        return code;
    }

    @Override
    public void postprocess(int threadCount, CsarQuery csarQuery) {
        // Create components
        QualifiedNameResolver qnr = new QualifiedNameResolver();
        thr = new DefaultTypeHierarchyResolver(qnr);
        MethodQualifiedTypeResolver mqtr = new MethodQualifiedTypeResolver();
        OverriddenMethodsResolver omr = new SelectiveOverriddenMethodsResolver(threadCount, qnr, thr,
                (MethodDescriptor) csarQuery.getSearchTarget().getDescriptor());
        MethodUseResolver mur = new MethodUseResolver(qnr, thr);
        MethodCallTypeInstanceResolver mctir = new MethodCallTypeInstanceResolver(qnr, thr);

        // Create post-processor
        CodePostProcessor processor = new JavaPostProcessor(thr, mqtr, omr, mctir, mur);
        processor.postprocess(code);
    }

    @Override
    public List<Result> search(CsarQuery csarQuery, int threadCount) {
        ProjectCodeSearcher searcher = new JavaCodeSearcher(threadCount);
        searcher.setCsarQuery(csarQuery);
        searcher.setIterator(code.iterator());
        searchResultObjects = searcher.resultObjects();
        return searcher.results();
    }

    @Override
    public List<Result> refactor(CsarQuery csarQuery, List<Result> searchResults, int threadCount) {
        ProjectCodeRefactorer refactorer = new JavaCodeRefactorer(threadCount, thr,
                new DummyRefactorChangeWriter());
        refactorer.setRefactorDescriptor(csarQuery.getRefactorDescriptor().
                orElseThrow(IllegalArgumentException::new));
        refactorer.setSearchResultObjects(searchResultObjects);
        return refactorer.results();
    }

    private static Iterator<Path> iterator(Path projectDirectory, boolean narrowSearch, Path ignoreFile,
            CodeParserFactory factory) {
        if (Files.exists(ignoreFile)) {
            try {
                return ProjectIteratorFactory.createFiltered(projectDirectory, Paths.get(".csar"), narrowSearch,
                        ignoreFile, factory);
            } catch (IOException ignored) {
            }
        }
        return ProjectIteratorFactory.createFiltered(projectDirectory, narrowSearch, factory);
    }
}
