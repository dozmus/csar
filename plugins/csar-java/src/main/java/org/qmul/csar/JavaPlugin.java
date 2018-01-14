package org.qmul.csar;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.qmul.csar.code.CodeAnalysisUtils;
import org.qmul.csar.code.DefaultProjectCodeParserErrorListener;
import org.qmul.csar.code.ProjectCodeSearcher;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.code.parse.ProjectCodeParser;
import org.qmul.csar.code.parse.java.JavaCodeParser;
import org.qmul.csar.code.postprocess.JavaAnalysisUtils;
import org.qmul.csar.code.postprocess.methodtypes.MethodQualifiedTypeResolver;
import org.qmul.csar.code.postprocess.methodusage.MethodUsageResolver;
import org.qmul.csar.code.postprocess.overriddenmethods.OverriddenMethodsResolver;
import org.qmul.csar.code.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.search.JavaCodeSearcher;
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

public class JavaPlugin extends Plugin {

    public JavaPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Extension
    public static class JavaProcessor implements CsarPlugin {

        private Map<Path, Statement> code;

        @Override
        public void parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount,
                boolean benchmarking) {
            CodeParserFactory factory = new CodeParserFactory(new JavaCodeParser());
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

            ProjectCodeParser parser = new ProjectCodeParser(factory, it, threadCount, benchmarking);
            parser.setErrorListener(new DefaultProjectCodeParserErrorListener());
            code = parser.results();
        }

        @Override
        public void postprocess(boolean benchmarking) {
            QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();
            TypeHierarchyResolver typeHierarchyResolver = new TypeHierarchyResolver(qualifiedNameResolver,
                    benchmarking);
            MethodQualifiedTypeResolver methodQualifiedTypeResolver
                    = new MethodQualifiedTypeResolver(qualifiedNameResolver);
            OverriddenMethodsResolver overriddenMethodsResolver = new OverriddenMethodsResolver(qualifiedNameResolver,
                    typeHierarchyResolver, benchmarking);
            MethodUsageResolver methodUsageResolver = new MethodUsageResolver();

            CodeAnalysisUtils javaCodeAnalysisUtils = new JavaAnalysisUtils(typeHierarchyResolver,
                    methodQualifiedTypeResolver, overriddenMethodsResolver, methodUsageResolver);
            javaCodeAnalysisUtils.setCode(code);
            javaCodeAnalysisUtils.analyze();
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
