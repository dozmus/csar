package org.qmul.csar.code.java.postprocess;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.postprocess.CodePostProcessor;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.methods.overridden.OverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methods.use.MethodUseResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;
import java.util.Map;

/**
 * A Java project code post-processor. It is comprised of the following parts, which are executed in order:
 * <ul>
 *     <li>TypeHierarchyResolver - resolves the type hierarchy of the project.</li>
 *     <li>MethodQualifiedTypeResolver - attaches QualifiedType instances for the return type and parameters of each
 *     method statement.</li>
 *     <li>OverriddenMethodsResolver - sets the overridden flag of each method statement.</li>
 *     <li>MethodCallTypeInstanceResolver - attaches TypeInstance instances for the name and parameters of each method
 *     call expression.</li>
 *     <li>MethodUseResolver - resolves and sets method calls which correspond to each method statement.</li>
 * </ul>
 */
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
        methodCallTypeInstanceResolver.postprocess(code);
        methodUseResolver.postprocess(code);
    }

    /**
     * Adds error listeners to all of its component post-processors.
     *
     * @param errorListener the error listener
     */
    @Override
    public void addErrorListener(CsarErrorListener errorListener) {
        typeHierarchyResolver.addErrorListener(errorListener);
        methodQualifiedTypeResolver.addErrorListener(errorListener);
        overriddenMethodsResolver.addErrorListener(errorListener);
        methodCallTypeInstanceResolver.addErrorListener(errorListener);
        methodUseResolver.addErrorListener(errorListener);
    }

    /**
     * Adds error listeners to all of its component post-processors.
     *
     * @param errorListener the error listener
     */
    @Override
    public void removeErrorListener(CsarErrorListener errorListener) {
        typeHierarchyResolver.removeErrorListener(errorListener);
        methodQualifiedTypeResolver.removeErrorListener(errorListener);
        overriddenMethodsResolver.removeErrorListener(errorListener);
        methodCallTypeInstanceResolver.removeErrorListener(errorListener);
        methodUseResolver.removeErrorListener(errorListener);
    }
}
