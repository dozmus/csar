package org.qmul.csar.code.postprocess;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.qmul.csar.code.parse.java.statement.*;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.StatementVisitor;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.ClassDescriptor;
import org.qmul.csar.lang.descriptor.EnumDescriptor;

import java.nio.file.Path;
import java.util.*;

public class TypeHierarchyResolver {

    // TODO how do no package stuff work - i.e. do they??
    private final TypeNode root = new TypeNode("java.lang.Object");

    public void resolve(Map<Path, Statement> code) {
        List<TypeNode> tmp = new ArrayList<>();

        // Iterate all code files
        for (Map.Entry<Path, Statement> entry : code.entrySet()) {
            Statement statement = entry.getValue();

            if (!(statement instanceof TopLevelTypeStatement))
                continue;
            TopLevelTypeStatement topStatement = (TopLevelTypeStatement)statement;
            TypeStatement typeStatement = topStatement.getTypeStatement();

            if (typeStatement instanceof AnnotationStatement)
                continue;
            String currentPkg = topStatement.getPackageStatement().map(p -> p.getPackageName() + ".").orElse("");

            TypeResolver resolver = new TypeResolver(code, tmp, currentPkg, topStatement.getImports(),
                    topStatement.getPackageStatement());
            resolver.visit(typeStatement);
        }

        // Merge in any left over partial trees in tmp
        mergePartialTrees(root, tmp);
    }

    private static void mergePartialTrees(TypeNode root, List<TypeNode> tmp) {
        // TODO ensure no duplicates added?
        for (TypeNode node : tmp) {
            if (!insert(root, node)) { // try to insert into correct position, if not possible place it at the root for a partial hierarchy
                root.children.add(node);
            }
        }
    }

    private static boolean insert(TypeNode root, TypeNode node) {
        if (root.qualifiedName.equals(node.qualifiedName)) {
            root.children.addAll(node.children);
            return true;
        } else {
            for (TypeNode rootChildren : root.children) {
                boolean inserted = insert(rootChildren, node);

                if (inserted)
                    return true;
            }
        }
        return false;
    }

    private TypeNode getFromTmp(List<TypeNode> tmp, String qualifiedName) {
        for (int i = 0; i < tmp.size(); i++) {
            TypeNode node = tmp.get(i);

            if (node.containsQualifiedName(qualifiedName)) {
                tmp.remove(i);
                return node;
            }
        }
        return new TypeNode(qualifiedName);
    }

    private String resolveQualifiedName(Map<Path, Statement> code, Optional<PackageStatement> currentPackage,
            List<ImportStatement> imports, String name) {
        if (name.contains(".")) { // a an inner name, i.e. MyClass.InnerType or a fully qualified name
            // Check classes in same package
            String s = resolvedInCurrentPackage(code, currentPackage, name);

            if (s != null)
                return s;

            // Check imports
            String s2 = resolveInOtherPackages(code, imports, name);

            if (s2 != null)
                return s2;
            return name; // if no match, we assume its an external class
        } else { // need to resolve a simple name, i.e. MyClass
            // Check classes in same package
            String s = resolvedInCurrentPackage(code, currentPackage, name);

            if (s != null)
                return s;

            // Check imports
            String s2 = resolveInOtherPackages(code, imports, name);

            if (s2 != null)
                return s2;
        }
        throw new RuntimeException("could not resolve qualified name for  " + name);
    }

    private String resolveInOtherPackages(Map<Path, Statement> code, List<ImportStatement> imports, String name) {
        for (ImportStatement importStatement : imports) {
            if (importStatement.isStaticImport())
                continue;
            String importQualifiedName = importStatement.getQualifiedName();

            if (importQualifiedName.endsWith(".*")) { // wildcard import
                String currentPkg = importQualifiedName.substring(0, importQualifiedName.length() - 2);

                for (Map.Entry<Path, Statement> entry : code.entrySet()) {
                    Statement statement = entry.getValue();

                    if (!(statement instanceof TopLevelTypeStatement))
                        continue;
                    TopLevelTypeStatement topStatement = (TopLevelTypeStatement) statement;
                    TypeStatement typeStatement = topStatement.getTypeStatement();

                    if (topStatement.getTypeStatement() instanceof AnnotationStatement)
                        continue;
                    String otherPkg = topStatement.getPackageStatement().map(p -> p.getPackageName()).orElse("");

                    if (targetContainsName(currentPkg, otherPkg, typeStatement, name))
                        return otherPkg + "." + String.join("$", name.split("\\."));
                }
            } else if (importQualifiedName.endsWith("." + name)) { // specific import
                return importQualifiedName;
            }
        }
        return null;
    }

    private String resolvedInCurrentPackage(Map<Path, Statement> code, Optional<PackageStatement> currentPackage,
            String name) {
        if (!currentPackage.isPresent())
            return null;
        String currentPkg = currentPackage.get().getPackageName();

        for (Map.Entry<Path, Statement> entry : code.entrySet()) {
            Statement statement = entry.getValue();

            if (!(statement instanceof TopLevelTypeStatement))
                continue;
            TopLevelTypeStatement topStatement = (TopLevelTypeStatement) statement;
            TypeStatement typeStatement = topStatement.getTypeStatement();

            if (topStatement.getPackageStatement().isPresent()) {
                String otherPkg = topStatement.getPackageStatement().get().getPackageName();

                if (targetContainsName(currentPkg, otherPkg, typeStatement, name))
                    return otherPkg + "." + String.join("$", name.split("\\."));
            }
        }
        return null;
    }

    private boolean targetContainsName(String currentPkg, String otherPkg, TypeStatement target, String name) {
        // Compare packages
        if (!otherPkg.equals(currentPkg))
            return false;

        // Perform search in target
        TargetTypeSearcher searcher = new TargetTypeSearcher(name);
        searcher.visit(target);
        return searcher.isMatched();
    }

    private void placeInList(List<TypeNode> tmp, Map<Path, Statement> code, Optional<PackageStatement> packageStatement,
            List<ImportStatement> imports, String child, List<String> superClasses) {
        for (String superClass : superClasses) {
            String resolvedSuperClassName = resolveQualifiedName(code, packageStatement, imports, superClass);

            // add to tmp structure, if you cant place it then add it as a new child
            if (!placeInList(tmp, resolvedSuperClassName, child)) {
                TypeNode node = new TypeNode(resolvedSuperClassName);
                node.children.add(new TypeNode(child));
                tmp.add(node);
            }
        }
    }

    private static boolean placeInList(List<TypeNode> tmp, String parent, String child) {
        for (TypeNode node : tmp) {
            if (node.qualifiedName.equals(parent)) {
                node.children.add(new TypeNode(child));
                return true;
            }
            boolean addedToChildren = placeInList(node.children, parent, child);

            if (addedToChildren)
                return true;
        }
        return false;
    }

    /**
     * Returns if the first type is a superclass of the second type, this means it returns <tt>true</tt> if they are
     * equal.
     *
     * @param type1 a fully qualified type which may be a superclass
     * @param type2 a fully qualified type which may be a subclass
     * @return returns if the first type is a superclass of the second type
     */
    public boolean isSubtype(String type1, String type2) {
        return type1.equals(type2) || isSubtype(root, type1, type2);
    }

    private static boolean isSubtype(TypeNode root, String type1, String type2) {
        // Base node
        if (root.qualifiedName.equals(type1)) {
            return root.containsQualifiedName(type2);
        } else {
            for (TypeNode child : root.children) {
                if (isSubtype(child, type1, type2))
                    return true;
            }
        }
        return false;
    }

    private static final class TypeNode {

        private String qualifiedName;
        private final List<TypeNode> children = new ArrayList<>();

        public TypeNode(String qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        public boolean containsQualifiedName(String qualifiedName) {
            return containsQualifiedName(this, qualifiedName, false);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("qualifiedName", qualifiedName)
                    .append("children", children)
                    .toString();
        }

        private static boolean containsQualifiedName(TypeNode node, String qualifiedName, boolean checkCurrent) {
            if (node.qualifiedName.equals(qualifiedName) && checkCurrent) {
                return true;
            } else {
                for (TypeNode child : node.children) {
                    if (containsQualifiedName(child, qualifiedName, true))
                        return true;
                }
                return false;
            }
        }
    }

    private static final class TargetTypeSearcher extends StatementVisitor {

        private final String[] targetName;
        private int matches = 0;
        private int nesting = -1;
        private boolean cancelled;

        public TargetTypeSearcher(String targetName) {
            this.targetName = targetName.split("\\.");
        }

        public void visit(Statement statement) {
            if (isMatched() || cancelled) // early termination (optimization)
                return;
            super.visit(statement);
        }

        @Override
        public void visitClassStatement(ClassStatement statement) {
            preVisit(statement.getDescriptor().getIdentifierName());
            super.visitClassStatement(statement);
            nesting--;
        }

        @Override
        public void visitEnumStatement(EnumStatement statement) {
            preVisit(statement.getDescriptor().getIdentifierName());
            super.visitEnumStatement(statement);
            nesting--;
        }

        private void preVisit(IdentifierName identifierName) {
            nesting++;

            if (nesting >= targetName.length) {
                cancelled = true;
            } else if (!isMatched() && identifierName.toString().equals(targetName[nesting])) {
                matches++; // XXX this may be error prone, check some stuff w this w diff nesting interleaving
            }
        }

        public boolean isMatched() {
            return matches == targetName.length;
        }
    }

    private final class TypeResolver extends StatementVisitor {

        private final Map<Path, Statement> code;
        private final List<TypeNode> tmp;
        private final List<ImportStatement> imports;
        private final Optional<PackageStatement> packageStatement;
        private String currentIdentifierName;
        private int nesting = 0;
        private Map<Integer, String> map = new HashMap<>(); // 'nesting number' mapped to the 'prefix' (at that nesting)

        public TypeResolver(Map<Path, Statement> code, List<TypeNode> tmp, String currentPkg,
                List<ImportStatement> imports, Optional<PackageStatement> packageStatement) {
            this.code = code;
            this.tmp = tmp;
            this.imports = imports;
            this.packageStatement = packageStatement;
            this.currentIdentifierName = currentPkg;
        }

        @Override
        public void visitMethodStatement(MethodStatement statement) {
            super.visitMethodStatement(statement);
        }

        @Override
        public void visitBlockStatement(BlockStatement statement) {
            super.visitBlockStatement(statement);
        }

        @Override
        public void visitClassStatement(ClassStatement statement) {
            ClassDescriptor desc = statement.getDescriptor();
            List<String> superClasses = new ArrayList<>(desc.getImplementedInterfaces());
            desc.getExtendedClass().ifPresent(superClasses::add);
            preVisit(desc.getIdentifierName(), superClasses);
            super.visitClassStatement(statement);
            nesting--;
        }

        @Override
        public void visitEnumStatement(EnumStatement statement) {
            EnumDescriptor desc = statement.getDescriptor();
            preVisit(desc.getIdentifierName(), desc.getSuperClasses());
            super.visitEnumStatement(statement);
            nesting--;
        }

        private void preVisit(IdentifierName identifierName, List<String> superClasses) {
            if (!map.containsKey(nesting)) {
                map.put(nesting, currentIdentifierName);
            }
            currentIdentifierName = map.get (nesting) + (nesting > 0 ? "$" : "") + identifierName;

            if (superClasses.size() == 0) {
                root.children.add(getFromTmp(tmp, currentIdentifierName));
            } else {
                placeInList(tmp, code, packageStatement, imports, currentIdentifierName, superClasses);
            }
            nesting++;
        }
    }
}
