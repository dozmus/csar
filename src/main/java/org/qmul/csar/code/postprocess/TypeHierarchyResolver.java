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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * A type hierarchy resolver for a code base, represented as a mapping of {@link Path} to {@link Statement}.
 */
public class TypeHierarchyResolver {

    // TODO parse java api classes properly

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeHierarchyResolver.class);
    /**
     * The node which all others are a child of, in java this is 'java.lang.Object'.
     */
    private final TypeNode root = new TypeNode("java.lang.Object");
    /**
     * The qualified name resolver to use.
     */
    private final QualifiedNameResolver qualifiedNameResolver;
    /**
     * If benchmarking output should be printed.
     */
    private final boolean benchmarking;

    public TypeHierarchyResolver() {
        this(new QualifiedNameResolver(), false);
    }

    public TypeHierarchyResolver(QualifiedNameResolver qualifiedNameResolver, boolean benchmarking) {
        this.qualifiedNameResolver = qualifiedNameResolver;
        this.benchmarking = benchmarking;
    }

    /**
     * Resolves the type hierarchy of the argument, and stores it in {@link #root}. If a type hierarchy cannot be
     * fully resolved it will be added as a partial hierarchy anyway (a non-extensive hierarchy).
     *
     * @param code the code base to resolve for
     */
    public void resolve(Map<Path, Statement> code) {
        LOGGER.info("Starting...");
        long startTime = System.currentTimeMillis();
        List<TypeNode> partialHierarchies = new ArrayList<>();

        // Iterate all code files
        for (Map.Entry<Path, Statement> entry : code.entrySet()) {
            Path path = entry.getKey();
            Statement statement = entry.getValue();

            if (!(statement instanceof TopLevelTypeStatement))
                continue;
            TopLevelTypeStatement topStatement = (TopLevelTypeStatement)statement;
            TypeStatement typeStatement = topStatement.getTypeStatement();

            if (typeStatement instanceof AnnotationStatement)
                continue;
            String currentPkg = topStatement.getPackageStatement().map(p -> p.getPackageName() + ".").orElse("");

            TypeResolver resolver = new TypeResolver(code, partialHierarchies, path, currentPkg,
                    topStatement.getImports(), topStatement.getPackageStatement(), typeStatement);
            resolver.visit(typeStatement);
        }

        // Merge in any left over partial trees in tmp
        mergePartialTrees(root, partialHierarchies);

        // Log completion message
        if (benchmarking) {
            LOGGER.info("Finished (processed {} files in {}ms)", code.size(), (System.currentTimeMillis() - startTime));
            LOGGER.info("Statistics: " + qualifiedNameResolver.getStatistics().toString());
        } else {
            LOGGER.info("Finished");
        }
    }

    /**
     * Places all nodes in the argument list into their correct position in the argument {@link TypeNode}.
     * If a correct position is not found for an element in the list, then they are placed as a child of the
     * <tt>root</tt> element to form a partial hierarchy.
     * <p>
     * If the argument is already contained, it will be added again.
     *
     * @param root the node to merge the list into
     * @param partialHierarchies the list whose elements to merge
     * @see TypeNode#insert(TypeNode, TypeNode)
     */
    private static void mergePartialTrees(TypeNode root, List<TypeNode> partialHierarchies) {
        for (TypeNode node : partialHierarchies) {
            if (!TypeNode.insert(root, node)) {
                root.children.add(node);
            }
        }
    }

    /**
     * Resolves each super class of <tt>child</tt> in <tt>superClasses</tt> and then places them in the type hierarchy
     * list, each with a child entry of <tt>child</tt>.
     *
     * @param list the type hierarchy list
     * @param code the code base
     * @param path the path of the child
     * @param packageStatement the package statement of the child class
     * @param imports the imports of the child class
     * @param child the name of the child class
     * @param superClasses the superclasses of the child class
     */
    private void placeInList(List<TypeNode> list, Map<Path, Statement> code, Path path, TypeStatement parent,
            Optional<PackageStatement> packageStatement, List<ImportStatement> imports, String child,
            List<String> superClasses) {
        for (String superClass : superClasses) {
            QualifiedNameResolver.QualifiedType resolvedType = qualifiedNameResolver.resolve(code, path, parent, parent,
                    packageStatement, imports, superClass);
            String resolvedSuperClassName = resolvedType.getQualifiedName();

            // Add to tmp structure, if there is no place for it it then add it as a new child
            if (!placeInList(list, resolvedSuperClassName, child)) {
                TypeNode node = new TypeNode(resolvedSuperClassName);
                node.children.add(new TypeNode(child));
                list.add(node);
            }
        }
    }

    /**
     * Returns <tt>true</tt> if the argument <tt>child</tt> was added to the argument <tt>parent</tt>s children as a
     * new {@link TypeNode}.
     *
     * @param list the list to search for the parent for
     * @param parent the qualified name of the parent
     * @param child the qualified name of the child
     * @return <tt>true</tt> if the argument <tt>child</tt> was added to the argument <tt>parent</tt>
     */
    private static boolean placeInList(List<TypeNode> list, String parent, String child) {
        for (TypeNode node : list) {
            if (node.qualifiedName.equals(parent)) {
                node.children.add(new TypeNode(child));
                return true;
            }

            if (placeInList(node.children, parent, child))
                return true;
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if the first type is a superclass of, or equal to, the second type.
     *
     * @param type1 a qualified name which may be a superclass
     * @param type2 a qualified name which may be a subclass
     * @return returns if the first type is a superclass of the second type
     */
    public boolean isSubtype(String type1, String type2) {
        return type1.equals(type2) || isStrictlySubtype(root, type1, type2);
    }

    /**
     * Returns <tt>true</tt> if the first type is a superclass of the second type.
     *
     * @param root the node hierarchy to check
     * @param type1 a qualified name which may be a superclass
     * @param type2 a qualified name which may be a subclass
     * @return returns if the first type is a superclass of the second type
     */
    private static boolean isStrictlySubtype(TypeNode root, String type1, String type2) {
        if (root.qualifiedName.equals(type1)) {
            return root.containsQualifiedName(type2);
        } else {
            for (TypeNode child : root.children) {
                if (isStrictlySubtype(child, type1, type2))
                    return true;
            }
        }
        return false;
    }

    /**
     * A node representing the type hierarchy of a single type.
     */
    private static final class TypeNode {

        private final String qualifiedName;
        private final List<TypeNode> children = new ArrayList<>();

        TypeNode(String qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        boolean containsQualifiedName(String qualifiedName) {
            return containsQualifiedName(this, qualifiedName, false);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("qualifiedName", qualifiedName)
                    .append("children", children)
                    .toString();
        }

        /**
         * Returns <tt>true</tt> if the argument <tt>String</tt> is contained in any child of the argument
         * <tt>TypeNode</tt>. The argument <tt>TypeNode</tt>'s value is only checked if <tt>checkCurrent</tt> is
         * <tt>true</tt>.
         *
         * @param node the node to check
         * @param qualifiedName the name to check for
         * @param checkCurrent if the current node's value should be checked too
         * @return if the argument node contains the argument qualifiedName
         */
        private static boolean containsQualifiedName(TypeNode node, String qualifiedName, boolean checkCurrent) {
            if (checkCurrent && node.qualifiedName.equals(qualifiedName)) {
                return true;
            } else {
                for (TypeNode child : node.children) {
                    if (containsQualifiedName(child, qualifiedName, true))
                        return true;
                }
                return false;
            }
        }

        /**
         * Returns <tt>true</tt> if the attempted insertion was successful, it can only be made if a node in
         * <tt>root</tt> is found which has the same <tt>qualifiedName</tt> as <tt>node</tt>, then all of the children
         * of <tt>node</tt> are added to <tt>root</tt>. This returns after a single insertion at most.
         * <p>
         * If the argument is already contained, it will be added again.
         *
         * @param root the node to add to
         * @param node the node whose children to add
         * @return <tt>true</tt> if insertion was successful
         */
        private static boolean insert(TypeNode root, TypeNode node) {
            if (root.qualifiedName.equals(node.qualifiedName)) {
                root.children.addAll(node.children);
                return true;
            } else {
                for (TypeNode child : root.children) {
                    if (insert(child, node))
                        return true;
                }
            }
            return false;
        }
    }

    /**
     * Resolves the type hierarchy of a given statement.
     */
    private final class TypeResolver extends StatementVisitor {

        private final Map<Path, Statement> code;
        private final List<TypeNode> tmp;
        private final List<ImportStatement> imports;
        private final Optional<PackageStatement> packageStatement;
        private final Map<Integer, String> map = new HashMap<>(); // maps 'nesting number' to 'prefix' at that nesting
        private final Path path;
        private final TypeStatement parent;
        private String currentIdentifierName;
        private int nesting = 0;

        TypeResolver(Map<Path, Statement> code, List<TypeNode> tmp, Path path, String currentPkg,
                List<ImportStatement> imports, Optional<PackageStatement> packageStatement,
                TypeStatement parent) {
            this.code = code;
            this.tmp = tmp;
            this.path = path;
            this.imports = imports;
            this.packageStatement = packageStatement;
            this.currentIdentifierName = currentPkg;
            this.parent = parent;
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
            currentIdentifierName = map.get(nesting) + (nesting > 0 ? "$" : "") + identifierName;

            if (superClasses.size() == 0) {
                root.children.add(getFromListOrDefault(tmp, currentIdentifierName));
            } else {
                placeInList(tmp, code, path, parent, packageStatement, imports, currentIdentifierName, superClasses);
            }
            nesting++;
        }

        /**
         * Returns a node with the given <tt>qualifiedName</tt> after removing it from the argument list, or creates and
         * returns a new {@link TypeNode} with that qualified name.
         *
         * @param tmp the list to search
         * @param qualifiedName the qualified name to search for
         * @return a <tt>TypeNode</tt> with the given qualified name
         */
        private TypeNode getFromListOrDefault(List<TypeNode> tmp, String qualifiedName) {
            for (int i = 0; i < tmp.size(); i++) {
                TypeNode node = tmp.get(i);

                if (node.containsQualifiedName(qualifiedName)) {
                    tmp.remove(i);
                    return node;
                }
            }
            return new TypeNode(qualifiedName);
        }
    }
}
