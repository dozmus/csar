package org.qmul.csar.code.java.postprocess.typehierarchy;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.parse.statement.AnnotationStatement;
import org.qmul.csar.code.java.parse.statement.ImportStatement;
import org.qmul.csar.code.java.parse.statement.PackageStatement;
import org.qmul.csar.code.java.parse.statement.CompilationUnitStatement;
import org.qmul.csar.code.java.postprocess.TypeHelper;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A type hierarchy resolver for a code base, represented as a mapping of {@link Path} to {@link Statement}.
 */
public class TypeHierarchyResolver implements CodePostProcessor {

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

    public TypeHierarchyResolver() {
        this(new QualifiedNameResolver());
    }

    public TypeHierarchyResolver(QualifiedNameResolver qualifiedNameResolver) {
        this.qualifiedNameResolver = qualifiedNameResolver;
    }

    /**
     * Places all nodes in the argument list into their correct position in the argument {@link TypeNode}. If a correct
     * position is not found for an element in the list, then they are placed as a child of the <tt>root</tt> element to
     * form a partial hierarchy.
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
                root.getChildren().add(node);
            }
        }
    }

    /**
     * Returns <tt>true</tt> if the argument <tt>child</tt> was added to the argument <tt>parent</tt>s children as a new
     * {@link TypeNode}.
     *
     * @param list the list to search for the parent for
     * @param parent the qualified name of the parent
     * @param child the qualified name of the child
     * @return <tt>true</tt> if the argument <tt>child</tt> was added to the argument <tt>parent</tt>
     */
    private static boolean placeInList(List<TypeNode> list, String parent, String child) {
        for (TypeNode node : list) {
            if (node.getQualifiedName().equals(parent)) {
                node.getChildren().add(new TypeNode(child));
                return true;
            }

            if (placeInList(node.getChildren(), parent, child))
                return true;
        }
        return false;
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
        if (root.getQualifiedName().equals(type1)) {
            return root.containsQualifiedName(type2);
        } else {
            for (TypeNode child : root.getChildren()) {
                if (isStrictlySubtype(child, type1, type2))
                    return true;
            }
        }
        return false;
    }

    /**
     * Resolves the type hierarchy of the argument, and stores it in {@link #root}. If a type hierarchy cannot be fully
     * resolved it will be added as a partial hierarchy anyway (a non-extensive hierarchy).
     *
     * @param code the code base to resolve for
     */
    public void postprocess(Map<Path, Statement> code) {
        LOGGER.info("Starting...");
        long startTime = System.currentTimeMillis();
        List<TypeNode> partialHierarchies = new ArrayList<>();

        // Iterate all code files
        for (Map.Entry<Path, Statement> entry : code.entrySet()) {
            Path path = entry.getKey();
            Statement statement = entry.getValue();

            if (!(statement instanceof CompilationUnitStatement))
                continue;
            CompilationUnitStatement topStatement = (CompilationUnitStatement) statement;
            TypeStatement typeStatement = topStatement.getTypeStatement();

            if (typeStatement instanceof AnnotationStatement)
                continue;
            String currentPkg = topStatement.getPackageStatement().map(p -> p.getPackageName() + ".").orElse("");

            TypeStatementHierarchyResolver resolver = new TypeStatementHierarchyResolver(this, code, partialHierarchies, path, currentPkg,
                    topStatement.getImports(), topStatement.getPackageStatement(), typeStatement);
            resolver.visitStatement(typeStatement);
        }

        // Merge in any left over partial trees in tmp
        mergePartialTrees(root, partialHierarchies);

        // Log completion message
        LOGGER.debug("Processed {} files in {}ms)", code.size(), (System.currentTimeMillis() - startTime));
        LOGGER.debug("Statistics: " + qualifiedNameResolver.getStatistics().toString());
        LOGGER.info("Finished");
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
    public void placeInList(List<TypeNode> list, Map<Path, Statement> code, Path path, TypeStatement parent,
            Optional<PackageStatement> packageStatement, List<ImportStatement> imports, String child,
            List<String> superClasses) {
        for (String superClass : superClasses) {
            QualifiedType resolvedType = qualifiedNameResolver.resolve(code, path, parent, parent,
                    packageStatement, imports, superClass);
            String resolvedSuperClassName = resolvedType.getQualifiedName();

            // Add to tmp structure, if there is no place for it it then add it as a new child
            if (!placeInList(list, resolvedSuperClassName, child)) {
                TypeNode node = new TypeNode(resolvedSuperClassName);
                node.getChildren().add(new TypeNode(child));
                list.add(node);
            }
        }
    }

    /**
     * Returns <tt>true</tt> if the first type is a superclass of, or equal to, the second type.
     *
     * @param type1 a qualified name which may be a superclass
     * @param type2 a qualified name which may be a subclass
     * @return returns if the first type is a superclass of the second type
     */
    public boolean isSubtype(String type1, String type2) {
        // TODO sanitize arrays etc?
        // TODO hard code support for primitives
        // Normalize varargs
        type1 = TypeHelper.normalizeVarArgs(type1);
        type2 = TypeHelper.normalizeVarArgs(type2);
        return type1.equals(type2) || isStrictlySubtype(root, type1, type2);
    }

    public TypeNode getRoot() {
        return root;
    }
}
