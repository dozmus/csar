package org.qmul.csar.code.postprocess;

import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class CodeAnalysisUtils {

    private final TypeHierarchyResolver typeHierarchyResolver = new TypeHierarchyResolver();
    private final OverriddenMethodsResolver overriddenMethodsResolver = new OverriddenMethodsResolver();
    private final Map<Path, Statement> code;

    public CodeAnalysisUtils(Map<Path, Statement> code) {
        this.code = code;
    }

    /**
     * Initialize the underlying analyzers.
     */
    public void analyze() {
        typeHierarchyResolver.resolve(code);
        overriddenMethodsResolver.resolve(code);
    }

    /**
     *
     * @param type1
     * @param type2
     * @return
     * @see TypeHierarchyResolver#isSubtype(String, String)
     */
    public boolean isSubtype(String type1, String type2) {
        return typeHierarchyResolver.isSubtype(type1, type2);
    }

    /**
     *
     * @param methodSignature
     * @return
     * @see OverriddenMethodsResolver#isOverridden(String)
     */
    public boolean isOverridden(String methodSignature) {
        return overriddenMethodsResolver.isOverridden(methodSignature);
    }
}
