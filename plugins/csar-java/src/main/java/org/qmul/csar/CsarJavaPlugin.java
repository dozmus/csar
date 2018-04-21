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
import org.qmul.csar.io.it.ProjectIteratorFactory;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.plugin.CsarPlugin;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.result.Result;

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
            errorListeners.forEach(l -> l.fatalInitializingParsing(ex));
            throw ex;
        }
        Iterator<Path> it = iterator(projectDirectory, narrowSearch, ignoreFile, factory);

        // Create parser
        DefaultProjectCodeParser parser = new DefaultProjectCodeParser(factory, it, threadCount);
        errorListeners.forEach(parser::addErrorListener);
        code = parser.results();
        return code;
    }

    @Override
    public void postprocess(int threadCount) {
        try {
            // Create components
            QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();
            TypeHierarchyResolver typeHierarchyResolver = new TypeHierarchyResolver(qualifiedNameResolver);
            MethodQualifiedTypeResolver methodQualifiedTypeResolver
                    = new MethodQualifiedTypeResolver(qualifiedNameResolver);
            OverriddenMethodsResolver overriddenMethodsResolver
                    = new OverriddenMethodsResolver(threadCount, qualifiedNameResolver, typeHierarchyResolver);
            MethodUseResolver methodUseResolver = new MethodUseResolver(qualifiedNameResolver);
            MethodCallTypeInstanceResolver methodCallTypeInstanceResolver = new MethodCallTypeInstanceResolver();

            // Create post-processor
            CodePostProcessor javaPostProcessor = new JavaPostProcessor(typeHierarchyResolver,
                    methodQualifiedTypeResolver, overriddenMethodsResolver, methodCallTypeInstanceResolver,
                    methodUseResolver
            );
            javaPostProcessor.postprocess(code);
        } catch (Exception ex) {
            errorListeners.forEach(l -> l.errorPostProcessing(ex));
        }
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
        ProjectCodeRefactorer refactorer = new JavaCodeRefactorer(threadCount, true);
        refactorer.setRefactorDescriptor(csarQuery.getRefactorDescriptor().orElseThrow(IllegalArgumentException::new));
        refactorer.setRefactorTargets(refactorTargets);
        return refactorer.results();
    }

    @Override
    public void addErrorListener(CsarErrorListener errorListener) {
        errorListeners.add(errorListener);
    }

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
