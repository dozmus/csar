package org.qmul.csar.code.java.postprocess.overriddenmethods;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.java.postprocess.PostProcessUtils;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.ClassDescriptor;
import org.qmul.csar.lang.descriptor.EnumDescriptor;
import org.qmul.csar.lang.descriptor.MethodDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OverriddenMethodsResolver implements CodePostProcessor {

    // TODO handle methods overridden from java api classes?

    private static final Logger LOGGER = LoggerFactory.getLogger(OverriddenMethodsResolver.class);
    /**
     * Maps a method's full signature to whether it's overridden or not. e.g. 'com.example.MyClass#int add(int,int)' ->
     * 'true'.
     */
    private final Map<String, Boolean> map = new HashMap<>();
    /**
     * The qualified name resolver to use.
     */
    private final QualifiedNameResolver qualifiedNameResolver;
    /**
     * The type hierarchy resolver to use.
     */
    private final TypeHierarchyResolver typeHierarchyResolver;

    public OverriddenMethodsResolver(QualifiedNameResolver qualifiedNameResolver,
            TypeHierarchyResolver typeHierarchyResolver) {
        this.qualifiedNameResolver = qualifiedNameResolver;
        this.typeHierarchyResolver = typeHierarchyResolver;
    }

    public void analyze(Map<Path, Statement> code) {
        LOGGER.info("Starting...");
        long startTime = System.currentTimeMillis();
        MethodStatementVisitor visitor = new MethodStatementVisitor(this, code);

        for (Map.Entry<Path, Statement> entry : code.entrySet()) {
            Path path = entry.getKey();
            Statement statement = entry.getValue();

            if (statement instanceof TopLevelTypeStatement) {
                TopLevelTypeStatement topLevelTypeStatement = (TopLevelTypeStatement) statement;
                visitor.setPath(path);
                visitor.setTopLevelTypeStatement(topLevelTypeStatement);
                visitor.visitStatement(topLevelTypeStatement);
            }
        }

        // Log completion message
        LOGGER.debug("Found {} overridden methods from {} files in {}ms", map.size(), code.size(),
                (System.currentTimeMillis() - startTime));
        LOGGER.debug("Statistics: " + qualifiedNameResolver.getStatistics().toString());
        LOGGER.info("Finished");
    }

    public boolean isOverridden(String methodSignature) {
        return map.getOrDefault(methodSignature, false);
    }

    public boolean calculateOverridden(Map<Path, Statement> code, Path path, Optional<PackageStatement> pkg,
            List<ImportStatement> imports, TypeStatement typeStatement, TypeStatement parent, MethodStatement method) {

        // Check if @Override annotation present
        // XXX it's a compile error to specify non-overridden methods as @Override
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.getIdentifierName().equals("Override") && !annotation.getValue().isPresent()) {
                return true;
            }
        }

        // TODO check methods defined in java.lang.Object

        // Parse parent type
        if (typeStatement instanceof ClassStatement) {
            ClassStatement classStatement = (ClassStatement) typeStatement;
            ClassDescriptor descriptor = classStatement.getDescriptor();

            if (!descriptor.getExtendedClass().isPresent() && descriptor.getImplementedInterfaces().size() == 0)
                return false;
            List<String> superClasses = PostProcessUtils.superClasses(classStatement);
            return calculateOverridden(code, pkg, imports, superClasses, path, typeStatement, parent, method);
        } else if (typeStatement instanceof EnumStatement) {
            EnumStatement enumStatement = (EnumStatement) typeStatement;
            EnumDescriptor descriptor = enumStatement.getDescriptor();

            if (descriptor.getSuperClasses().size() == 0)
                return false;
            return calculateOverridden(code, pkg, imports, descriptor.getSuperClasses(), path, typeStatement, parent,
                    method);
        }
        // NOTE annotation types cannot have superclasses
        return false;
    }

    private boolean calculateOverridden(Map<Path, Statement> code, Optional<PackageStatement> packageStatement,
            List<ImportStatement> imports, List<String> superClasses, Path path, TypeStatement parent,
            TypeStatement topLevelParent, MethodStatement method) {
        MethodDescriptor desc = method.getDescriptor();

        for (String superClass : superClasses) {
            QualifiedType resolvedType = qualifiedNameResolver.resolve(code, path, parent, topLevelParent,
                    packageStatement, imports, superClass);
            Statement resolvedStatement = resolvedType.getStatement();

            // NOTE we ignore (fully) un-resolved statements here
            if (resolvedStatement != null && resolvedStatement instanceof TopLevelTypeStatement) {
                TopLevelTypeStatement s = (TopLevelTypeStatement) resolvedStatement;
                TypeStatement s2 = s.getTypeStatement();
                boolean isClassOrEnum = (s2 instanceof ClassStatement || s2 instanceof EnumStatement);

                // Check current class
                if (!s2.equals(parent) && isClassOrEnum) {
                    BlockStatement blockStatement;

                    if (s.getTypeStatement() instanceof ClassStatement) {
                        blockStatement = ((ClassStatement) s.getTypeStatement()).getBlock();
                    } else { // enum
                        blockStatement = ((EnumStatement) s.getTypeStatement()).getBlock();
                    }

                    for (Statement statement : blockStatement.getStatements()) {
                        if (!(statement instanceof MethodStatement))
                            continue;
                        MethodStatement m2 = (MethodStatement) statement;
                        MethodDescriptor desc2 = m2.getDescriptor();
                        boolean signatureEquals = MethodSignatureComparator.signatureEquals(m2, method,
                                typeHierarchyResolver);
                        boolean accessible = PostProcessUtils.isAccessible(desc, desc2, packageStatement,
                                s.getPackageStatement(), s2);

                        if (!desc2.getStaticModifier().get() && signatureEquals && accessible) {
                            return !desc2.getFinalModifier().get();
                        }
                    }
                }

                // Check super classes of super class
                if (calculateOverridden(code, s.getPackageStatement(), s.getImports(),
                        PostProcessUtils.superClasses(s2), resolvedType.getPath(), parent,
                        s, method)) { // TODO some args passed here are incorrect: make sure parent is right
                    return true;
                }
            }
        }
        return false;
    }

    public Map<String, Boolean> getMap() {
        return map;
    }
}