package org.qmul.csar;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.ProjectCodeSearcher;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.postprocess.JavaPostProcessor;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methods.use.MethodUseResolver;
import org.qmul.csar.code.java.postprocess.methods.overridden.OverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.java.search.JavaCodeSearcher;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.io.ProjectIteratorFactory;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.plugin.CsarPlugin;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The java language csar plugin.
 */
public class CsarJavaPlugin implements CsarPlugin {

    private Map<Path, Statement> code;
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();

    @Override
    public void parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount) {
        // Create iterator
        CodeParserFactory factory;

        try {
            factory = new CodeParserFactory(JavaCodeParser.class);
        } catch (InstantiationException | IllegalAccessException ex) {
            errorListeners.forEach(l -> l.fatalInitializingParsing(ex));
            return;
        }
        Iterator<Path> it = iterator(projectDirectory, narrowSearch, ignoreFile, factory);

        // Create parser
        DefaultProjectCodeParser parser = new DefaultProjectCodeParser(factory, it, threadCount);
        errorListeners.forEach(parser::addErrorListener);
        code = parser.results();
    }

    @Override
    public void postprocess() {
        try {
            // Create components
            QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();
            TypeHierarchyResolver typeHierarchyResolver = new TypeHierarchyResolver(qualifiedNameResolver);
            MethodQualifiedTypeResolver methodQualifiedTypeResolver
                    = new MethodQualifiedTypeResolver(qualifiedNameResolver);
            OverriddenMethodsResolver overriddenMethodsResolver
                    = new OverriddenMethodsResolver(qualifiedNameResolver, typeHierarchyResolver);
            MethodUseResolver methodUseResolver = new MethodUseResolver();
            MethodCallTypeInstanceResolver methodCallTypeInstanceResolver = new MethodCallTypeInstanceResolver();

            // Create post-processor
            CodePostProcessor javaPostProcessor = new JavaPostProcessor(typeHierarchyResolver,
                    methodQualifiedTypeResolver, overriddenMethodsResolver, methodUseResolver,
                    methodCallTypeInstanceResolver);
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
        return searcher.results();
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
            } catch (IOException e) {
                return ProjectIteratorFactory.createFilteredIterator(projectDirectory, narrowSearch, factory);
            }
        } else {
            return ProjectIteratorFactory.createFilteredIterator(projectDirectory, narrowSearch, factory);
        }
    }
}
