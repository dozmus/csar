package org.qmul.csar.code.java.postprocess.methods.overridden;

import org.qmul.csar.code.CodeBase;
import org.qmul.csar.code.java.parse.statement.ImportStatement;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.java.parse.statement.PackageStatement;
import org.qmul.csar.code.postprocess.CodePostProcessor;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptors.MethodDescriptor;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * A post-processor which assigns to each {@link MethodStatement} whether its overridden or not, to its
 * {@link MethodDescriptor#getOverridden()}.
 */
public interface OverriddenMethodsResolver extends CodePostProcessor {

    /**
     * Returns true if the argument method is an overridden method, and sets {@link MethodDescriptor#overridden} on the
     * argument method.
     */
    boolean calculateOverridden(CodeBase code, Path path, Optional<PackageStatement> pkg,
            List<ImportStatement> imports, TypeStatement typeStatement, TypeStatement parent, MethodStatement method);
}
