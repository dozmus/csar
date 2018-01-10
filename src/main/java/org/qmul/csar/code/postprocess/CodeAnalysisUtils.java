package org.qmul.csar.code.postprocess;

import org.qmul.csar.code.postprocess.methodtypes.MethodQualifiedTypeResolver;
import org.qmul.csar.code.postprocess.methodusage.MethodUsageResolver;
import org.qmul.csar.code.postprocess.overriddenmethods.OverriddenMethodsResolver;
import org.qmul.csar.code.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class CodeAnalysisUtils {

    private final TypeHierarchyResolver typeHierarchyResolver;
    private final MethodQualifiedTypeResolver methodQualifiedTypeResolver;
    private final OverriddenMethodsResolver overriddenMethodsResolver;
    private final MethodUsageResolver methodUsageResolver;
    private Map<Path, Statement> code;

    public CodeAnalysisUtils(TypeHierarchyResolver typeHierarchyResolver,
            MethodQualifiedTypeResolver methodQualifiedTypeResolver,
            OverriddenMethodsResolver overriddenMethodsResolver,
            MethodUsageResolver methodUsageResolver) {
        this.typeHierarchyResolver = typeHierarchyResolver;
        this.methodQualifiedTypeResolver = methodQualifiedTypeResolver;
        this.overriddenMethodsResolver = overriddenMethodsResolver;
        this.methodUsageResolver = methodUsageResolver;
    }

    public void setCode(Map<Path, Statement> code) {
        this.code = code;
    }

    /**
     * Initialize the underlying analyzers.
     */
    public void analyze() {
        typeHierarchyResolver.resolve(code);
        methodQualifiedTypeResolver.resolve(code);
        overriddenMethodsResolver.resolve(code);
        methodUsageResolver.resolve(code);
    }
}
