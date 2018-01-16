package org.qmul.csar.code.java.postprocess;

import org.qmul.csar.code.CodeAnalyzer;
import org.qmul.csar.code.java.postprocess.methodtypes.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methodusage.MethodUsageResolver;
import org.qmul.csar.code.java.postprocess.overriddenmethods.OverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class JavaAnalyzer implements CodeAnalyzer {

    private final TypeHierarchyResolver typeHierarchyResolver;
    private final MethodQualifiedTypeResolver methodQualifiedTypeResolver;
    private final OverriddenMethodsResolver overriddenMethodsResolver;
    private final MethodUsageResolver methodUsageResolver;

    public JavaAnalyzer(TypeHierarchyResolver typeHierarchyResolver,
            MethodQualifiedTypeResolver methodQualifiedTypeResolver,
            OverriddenMethodsResolver overriddenMethodsResolver,
            MethodUsageResolver methodUsageResolver) {
        this.typeHierarchyResolver = typeHierarchyResolver;
        this.methodQualifiedTypeResolver = methodQualifiedTypeResolver;
        this.overriddenMethodsResolver = overriddenMethodsResolver;
        this.methodUsageResolver = methodUsageResolver;
    }

    /**
     * Initialize the underlying analyzers.
     */
    public void analyze(Map<Path, Statement> code) {
        typeHierarchyResolver.analyze(code);
        methodQualifiedTypeResolver.analyze(code);
        overriddenMethodsResolver.analyze(code);
        methodUsageResolver.analyze(code);
    }
}
