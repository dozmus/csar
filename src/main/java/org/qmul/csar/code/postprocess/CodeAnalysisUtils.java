package org.qmul.csar.code.postprocess;

import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class CodeAnalysisUtils {

    private final TypeHierarchyResolver typeHierarchyResolver;
    private final OverriddenMethodsResolver overriddenMethodsResolver;
    private Map<Path, Statement> code;

    public CodeAnalysisUtils(TypeHierarchyResolver typeHierarchyResolver,
            OverriddenMethodsResolver overriddenMethodsResolver) {
        this.typeHierarchyResolver = typeHierarchyResolver;
        this.overriddenMethodsResolver = overriddenMethodsResolver;
    }

    public void setCode(Map<Path, Statement> code) {
        this.code = code;
    }

    /**
     * Initialize the underlying analyzers.
     */
    public void analyze() {
        typeHierarchyResolver.resolve(code);
        overriddenMethodsResolver.resolve(code);
    }
}
