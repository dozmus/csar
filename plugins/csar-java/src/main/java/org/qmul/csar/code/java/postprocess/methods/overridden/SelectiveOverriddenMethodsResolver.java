package org.qmul.csar.code.java.postprocess.methods.overridden;

import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.descriptors.MethodDescriptor;

import java.util.Optional;

/**
 * A {@link DefaultOverriddenMethodsResolver} which filters the methods it processes.
 * It only processes them if they match the search target descriptor, except for {@link MethodDescriptor#overridden}.
 */
public class SelectiveOverriddenMethodsResolver extends DefaultOverriddenMethodsResolver {

    public SelectiveOverriddenMethodsResolver(QualifiedNameResolver qnr, TypeHierarchyResolver thr,
            MethodDescriptor searchTarget) {
        this(1, qnr, thr, searchTarget);
    }

    public SelectiveOverriddenMethodsResolver(int threadCount, QualifiedNameResolver qnr, TypeHierarchyResolver thr,
            MethodDescriptor searchTarget) {
        super(threadCount, qnr, thr, m -> !isIgnored(m.getDescriptor(), removeOverridden(searchTarget)));
    }

    /**
     * Returns a new copy of the argument with the {@link MethodDescriptor#overridden} to empty.
     */
    public static MethodDescriptor removeOverridden(MethodDescriptor descriptor) {
        MethodDescriptor target = descriptor.clone();
        target.setOverridden(Optional.empty());
        return target;
    }

    /**
     * Returns true if the other descriptor does not match the target descriptor in every set file in target, except
     * {@link MethodDescriptor#overridden}.
     */
    public static boolean isIgnored(MethodDescriptor other, MethodDescriptor target) {
        return !other.lenientEquals(target);
    }
}
