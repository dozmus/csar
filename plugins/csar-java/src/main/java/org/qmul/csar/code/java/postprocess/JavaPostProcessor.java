package org.qmul.csar.code.java.postprocess;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methods.use.MethodUseResolver;
import org.qmul.csar.code.java.postprocess.methods.overridden.OverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

public class JavaPostProcessor implements CodePostProcessor {

    private final TypeHierarchyResolver typeHierarchyResolver;
    private final MethodQualifiedTypeResolver methodQualifiedTypeResolver;
    private final OverriddenMethodsResolver overriddenMethodsResolver;
    private final MethodCallTypeInstanceResolver methodCallTypeInstanceResolver;
    private final MethodUseResolver methodUseResolver;

    public JavaPostProcessor(TypeHierarchyResolver typeHierarchyResolver,
            MethodQualifiedTypeResolver methodQualifiedTypeResolver,
            OverriddenMethodsResolver overriddenMethodsResolver,
            MethodCallTypeInstanceResolver methodCallTypeInstanceResolver, MethodUseResolver methodUseResolver) {
        this.typeHierarchyResolver = typeHierarchyResolver;
        this.methodQualifiedTypeResolver = methodQualifiedTypeResolver;
        this.overriddenMethodsResolver = overriddenMethodsResolver;
        this.methodCallTypeInstanceResolver = methodCallTypeInstanceResolver;
        this.methodUseResolver = methodUseResolver;
    }

    /**
     * Initialize the underlying post-processors.
     */
    public void postprocess(Map<Path, Statement> code) {
        typeHierarchyResolver.postprocess(code);
        methodQualifiedTypeResolver.postprocess(code);
        overriddenMethodsResolver.postprocess(code);
        methodCallTypeInstanceResolver.setTypeHierarchyResolver(typeHierarchyResolver);
        methodCallTypeInstanceResolver.postprocess(code);
        methodUseResolver.setTypeHierarchyResolver(typeHierarchyResolver);
        methodUseResolver.postprocess(code);
    }
}
