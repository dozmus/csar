package org.qmul.csar.code.java.postprocess;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.postprocess.methodtypes.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methodusage.MethodUsageResolver;
import org.qmul.csar.code.java.postprocess.overriddenmethods.OverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class JavaPostProcessor implements CodePostProcessor {

    private final TypeHierarchyResolver typeHierarchyResolver;
    private final MethodQualifiedTypeResolver methodQualifiedTypeResolver;
    private final OverriddenMethodsResolver overriddenMethodsResolver;
    private final MethodUsageResolver methodUsageResolver;

    public JavaPostProcessor(TypeHierarchyResolver typeHierarchyResolver,
            MethodQualifiedTypeResolver methodQualifiedTypeResolver,
            OverriddenMethodsResolver overriddenMethodsResolver,
            MethodUsageResolver methodUsageResolver) {
        this.typeHierarchyResolver = typeHierarchyResolver;
        this.methodQualifiedTypeResolver = methodQualifiedTypeResolver;
        this.overriddenMethodsResolver = overriddenMethodsResolver;
        this.methodUsageResolver = methodUsageResolver;
    }

    /**
     * Initialize the underlying post-processors.
     */
    public void postprocess(Map<Path, Statement> code) {
        typeHierarchyResolver.postprocess(code);
        methodQualifiedTypeResolver.postprocess(code);
        overriddenMethodsResolver.postprocess(code);
        methodUsageResolver.postprocess(code);
    }
}
