package org.qmul.csar;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.DefaultProjectCodeErrorListener;
import org.qmul.csar.code.ProjectCodeSearcher;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.postprocess.JavaPostProcessor;
import org.qmul.csar.code.java.postprocess.methodtypes.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methodusage.MethodUsageResolver;
import org.qmul.csar.code.java.postprocess.overriddenmethods.OverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.java.search.JavaCodeSearcher;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.DefaultProjectCodeParser;
import org.qmul.csar.io.ProjectIteratorFactory;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The java language plugin.
 */
public class JavaPlugin extends Plugin {

    public JavaPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * The java language csar plugin.
     */
    @Extension
    public static class CsarJavaPlugin implements CsarPlugin {

        private Map<Path, Statement> code;

        @Override
        public boolean parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount) {
            // Create iterator
            CodeParserFactory factory;

            try {
                factory = new CodeParserFactory(JavaCodeParser.class);
            } catch (InstantiationException | IllegalAccessException e) {
                return false;
            }
            Iterator<Path> it = iterator(projectDirectory, narrowSearch, ignoreFile, factory);

            // Create parser
            DefaultProjectCodeParser parser = new DefaultProjectCodeParser(factory, it, threadCount);
            parser.setErrorListener(new DefaultProjectCodeErrorListener());
            code = parser.results();
            return !parser.errorOccurred();
        }

        @Override
        public boolean postprocess() {
            // Create components
            QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();
            TypeHierarchyResolver typeHierarchyResolver = new TypeHierarchyResolver(qualifiedNameResolver);
            MethodQualifiedTypeResolver methodQualifiedTypeResolver
                    = new MethodQualifiedTypeResolver(qualifiedNameResolver);
            OverriddenMethodsResolver overriddenMethodsResolver = new OverriddenMethodsResolver(qualifiedNameResolver,
                    typeHierarchyResolver);
            MethodUsageResolver methodUsageResolver = new MethodUsageResolver();

            // Create post-processor
            CodePostProcessor javaCodePostProcessor = new JavaPostProcessor(typeHierarchyResolver,
                    methodQualifiedTypeResolver, overriddenMethodsResolver, methodUsageResolver);
            javaCodePostProcessor.analyze(code);
            return true;
        }

        @Override
        public List<Result> search(CsarQuery csarQuery, int threadCount) throws Exception {
            ProjectCodeSearcher searcher = new JavaCodeSearcher(threadCount);
            searcher.setCsarQuery(csarQuery);
            searcher.setIterator(code.entrySet().iterator());
            List<Result> results = searcher.results();

            if (searcher.errorOccurred())
                throw new Exception("error occurred");
            return results;
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
}
