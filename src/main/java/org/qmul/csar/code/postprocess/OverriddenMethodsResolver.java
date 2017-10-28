package org.qmul.csar.code.postprocess;

import org.qmul.csar.code.parse.java.statement.*;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.StatementVisitor;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.ClassDescriptor;
import org.qmul.csar.lang.descriptor.EnumDescriptor;
import org.qmul.csar.lang.descriptor.MethodDescriptor;
import org.qmul.csar.lang.descriptor.VisibilityModifier;

import java.nio.file.Path;
import java.util.*;

public class OverriddenMethodsResolver {

    // TODO handle methods overridden from java api classes?

    /**
     * This maps a method's full signature to whether it's overridden or not.
     * e.g. 'com.example.MyClass#int add(int,int)' -> 'true'.
     */
    private final Map<String, Boolean> map = new HashMap<>();
    /**
     * The qualified name resolver to use.
     */
    private final QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();

    public void resolve(Map<Path, Statement> code) {
        MethodStatementVisitor visitor = new MethodStatementVisitor(code);

        for (Map.Entry<Path, Statement> entry : code.entrySet()) {
            Path path = entry.getKey();
            Statement statement = entry.getValue();

            if (statement instanceof TopLevelTypeStatement) {
                TopLevelTypeStatement topLevelTypeStatement = (TopLevelTypeStatement) statement;
                visitor.setPath(path);
                visitor.setTopLevelTypeStatement(topLevelTypeStatement);
                visitor.visit(topLevelTypeStatement.getTypeStatement());
            }
        }
    }

    public boolean isOverridden(String methodSignature) {
        return map.getOrDefault(methodSignature, false);
    }

    private boolean calculateOverridden(Map<Path, Statement> code, Path path, Optional<PackageStatement> pkg,
            List<ImportStatement> imports, TypeStatement typeStatement, MethodStatement method) {

        // Check if @Override annotation present
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.getIdentifierName().equals("Override") && !annotation.getValue().isPresent()) {
                return true;
            }
        }

        // TODO check methods defined in java.lang.Object

        // Parse parent type
        if (typeStatement instanceof ClassStatement) {
            ClassStatement classStatement = (ClassStatement)typeStatement;
            ClassDescriptor descriptor = classStatement.getDescriptor();

            if (!descriptor.getExtendedClass().isPresent() && descriptor.getImplementedInterfaces().size() == 0)
                return false;
            List<String> superClasses = superClasses(classStatement);
            return calculateOverridden(code, pkg, imports, superClasses, path, typeStatement, method);
        } else if (typeStatement instanceof EnumStatement) {
            EnumStatement enumStatement = (EnumStatement)typeStatement;
            EnumDescriptor descriptor = enumStatement.getDescriptor();

            if (descriptor.getSuperClasses().size() == 0)
                return false;
            return calculateOverridden(code, pkg, imports, descriptor.getSuperClasses(), path,
                    typeStatement, method);
        }
        // NOTE annotation types cannot have superclasses
        return false;
    }

    private boolean calculateOverridden(Map<Path, Statement> code, Optional<PackageStatement> packageStatement,
            List<ImportStatement> imports, List<String> superClasses, Path path, TypeStatement parent,
            MethodStatement method) {
        MethodDescriptor desc = method.getDescriptor();

        for (String superClass : superClasses) {
            QualifiedNameResolver.QualifiedType resolvedType = qualifiedNameResolver.resolve(code, path,
                    packageStatement, imports, superClass);
            Statement resolvedStatement = resolvedType.getStatement();

            if (resolvedStatement != null && resolvedStatement instanceof TopLevelTypeStatement) {
                TopLevelTypeStatement s = (TopLevelTypeStatement)resolvedStatement;
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
                        MethodStatement m2 = (MethodStatement)statement;
                        MethodDescriptor desc2 = m2.getDescriptor();

                        if (!desc2.getStaticModifier().get() && desc2.signatureEquals(desc)
                                && isAccessible(desc, desc2, packageStatement, s.getPackageStatement(), s2)) {
                            return !desc2.getFinalModifier().get();
                        }
                    }
                }

                // Check super classes of super class
                if (calculateOverridden(code, packageStatement, imports, superClasses(s2), path, parent, method))
                    return true;
            }
        }
        return false;
    }

    private static List<String> superClasses(TypeStatement typeStatement) {
        if (typeStatement instanceof ClassStatement) {
            ClassStatement classStatement = (ClassStatement)typeStatement;
            ClassDescriptor descriptor = classStatement.getDescriptor();
            List<String> superClasses = new ArrayList<>();
            descriptor.getExtendedClass().ifPresent(superClasses::add);
            superClasses.addAll(descriptor.getImplementedInterfaces());
            return Collections.unmodifiableList(superClasses);
        } else if (typeStatement instanceof EnumStatement) {
            EnumStatement enumStatement = (EnumStatement)typeStatement;
            EnumDescriptor descriptor = enumStatement.getDescriptor();
            return Collections.unmodifiableList(descriptor.getSuperClasses());
        }
        return Collections.unmodifiableList(new ArrayList<>());
    }

    private static boolean isAccessible(MethodDescriptor childDesc, MethodDescriptor superDesc,
            Optional<PackageStatement> childPkg, Optional<PackageStatement> superPkg, TypeStatement superType) {
        // is the super class an interface
        boolean isSuperInterface = false;

        if (superType instanceof ClassStatement) {
            isSuperInterface = ((ClassStatement)superType).getDescriptor().getInterfaceModifier().orElse(false);
        }

        // compute result
        VisibilityModifier childVis = childDesc.getVisibilityModifier().get();
        VisibilityModifier superVis = superDesc.getVisibilityModifier().get();

        if (isSuperInterface) {
            return childVis == VisibilityModifier.PUBLIC;
        } else {
            if (superVis == VisibilityModifier.PUBLIC && childVis == VisibilityModifier.PUBLIC) {
                return true;
            } else if (superVis == VisibilityModifier.PROTECTED
                    && (childVis == VisibilityModifier.PROTECTED || childVis == VisibilityModifier.PUBLIC)) {
                return true;
            } else if (superVis == VisibilityModifier.PACKAGE_PRIVATE
                    && childPkg.isPresent() && childPkg.equals(superPkg) && childVis != VisibilityModifier.PRIVATE) {
                return true;
            }
        }
        return false;
    }

    private final class MethodStatementVisitor extends StatementVisitor {

        private final Map<Path, Statement> code;
        private final Deque<String> traversalHierarchy = new ArrayDeque<>();
        private final Deque<TypeStatement> traversedTypeStatements = new ArrayDeque<>();
        private Path path;
        private Optional<PackageStatement> packageStatement;
        private List<ImportStatement> imports;

        private MethodStatementVisitor(Map<Path, Statement> code) {
            this.code = code;
        }

        @Override
        public void visitEnumStatement(EnumStatement statement) {
            traversalHierarchy.addLast(prefix() + statement.getDescriptor().getIdentifierName().toString());
            traversedTypeStatements.addLast(statement);
            super.visitEnumStatement(statement);
        }

        @Override
        public void exitEnumStatement(EnumStatement statement) {
            traversalHierarchy.removeLast();
            traversedTypeStatements.removeLast();
        }

        @Override
        public void visitClassStatement(ClassStatement statement) {
            traversalHierarchy.addLast(prefix() + statement.getDescriptor().getIdentifierName().toString());
            traversedTypeStatements.addLast(statement);
            super.visitClassStatement(statement);
        }

        @Override
        public void exitClassStatement(ClassStatement statement) {
            traversalHierarchy.removeLast();
            traversedTypeStatements.removeLast();
        }

        @Override
        public void visitMethodStatement(MethodStatement statement) {
            traversalHierarchy.addLast("#" + statement.getDescriptor().signature());
            mapOverridden(statement);
            super.visitMethodStatement(statement);
        }

        private void mapOverridden(MethodStatement method) {
            if (calculateOverridden(code, path, packageStatement, imports, traversedTypeStatements.getLast(), method)) {
                map.put(createSignature(), true);
            }
        }

        private String prefix() {
            if (traversalHierarchy.size() == 0)
                return "";
            return (traversedTypeStatements.size() == 1) ? "." : "$";
        }

        private String createSignature() {
            String signature = String.join("", traversalHierarchy);
            return signature;
        }

        private void setTopLevelTypeStatement(TopLevelTypeStatement topLevelTypeStatement) {
            traversedTypeStatements.clear();
            traversalHierarchy.clear();
            traversedTypeStatements.addLast(topLevelTypeStatement);
            packageStatement = topLevelTypeStatement.getPackageStatement();
            imports = topLevelTypeStatement.getImports();

            if (topLevelTypeStatement.getPackageStatement().isPresent()) {
                PackageStatement pkg = topLevelTypeStatement.getPackageStatement().get();
                String[] pkgParts = pkg.getPackageName().split("\\.");

                for (int i = 0; i < pkgParts.length; i++) {
                    String s = pkgParts[i];

                    if (i > 0)
                        s = "." + s;
                    traversalHierarchy.addLast(s);
                }
            }
        }

        public void setPath(Path path) {
            this.path = path;
        }
    }
}