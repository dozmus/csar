package org.qmul.csar;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.qmul.csar.code.CodeAnalyzer;
import org.qmul.csar.code.DefaultPathProcessorErrorListener;
import org.qmul.csar.code.ProjectCodeSearcher;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.ProjectCodeParser;
import org.qmul.csar.code.java.parse.JavaCodeParser;
import org.qmul.csar.code.java.postprocess.JavaAnalyzer;
import org.qmul.csar.code.java.postprocess.methodtypes.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methodusage.MethodUsageResolver;
import org.qmul.csar.code.java.postprocess.overriddenmethods.OverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.java.search.JavaCodeSearcher;
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
        public void parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount) {
            CodeParserFactory factory = null;

            try {
                factory = new CodeParserFactory(JavaCodeParser.class);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            Iterator<Path> it;

            if (Files.exists(ignoreFile)) {
                try {
                    it = ProjectIteratorFactory.createFilteredIterator(projectDirectory, narrowSearch, ignoreFile,
                            factory);
                } catch (IOException e) {
                    it = ProjectIteratorFactory.createFilteredIterator(projectDirectory, narrowSearch, factory);
                }
            } else {
                it = ProjectIteratorFactory.createFilteredIterator(projectDirectory, narrowSearch, factory);
            }

            ProjectCodeParser parser = new ProjectCodeParser(factory, it, threadCount);
            parser.setErrorListener(new DefaultPathProcessorErrorListener());
            code = parser.results();
        }

        @Override
        public void postprocess() {
            QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();
            TypeHierarchyResolver typeHierarchyResolver = new TypeHierarchyResolver(qualifiedNameResolver);
            MethodQualifiedTypeResolver methodQualifiedTypeResolver
                    = new MethodQualifiedTypeResolver(qualifiedNameResolver);
            OverriddenMethodsResolver overriddenMethodsResolver = new OverriddenMethodsResolver(qualifiedNameResolver,
                    typeHierarchyResolver);
            MethodUsageResolver methodUsageResolver = new MethodUsageResolver();

            CodeAnalyzer javaCodeAnalyzer = new JavaAnalyzer(typeHierarchyResolver,
                    methodQualifiedTypeResolver, overriddenMethodsResolver, methodUsageResolver);
            javaCodeAnalyzer.analyze(code);
        }

        @Override
        public List<Result> search(CsarQuery csarQuery, int threadCount) {
            ProjectCodeSearcher searcher = new JavaCodeSearcher(threadCount);
            searcher.setCsarQuery(csarQuery);
            searcher.setIterator(code.entrySet().iterator());
            return searcher.results();
        }
    }
}
