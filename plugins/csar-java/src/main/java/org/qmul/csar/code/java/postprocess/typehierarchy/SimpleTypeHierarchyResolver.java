package org.qmul.csar.code.java.postprocess.typehierarchy;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.java.parse.statement.AnnotationStatement;
import org.qmul.csar.code.java.parse.statement.CompilationUnitStatement;
import org.qmul.csar.code.java.parse.statement.ImportStatement;
import org.qmul.csar.code.java.parse.statement.PackageStatement;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.code.java.postprocess.typehierarchy.node.PrimitiveTypeNode;
import org.qmul.csar.code.java.postprocess.typehierarchy.node.TypeNode;
import org.qmul.csar.code.java.postprocess.util.TypeHelper;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

/**
 * A type hierarchy resolver for a code base, represented as a mapping of {@link Path} to {@link Statement}.
 * This only considers classes, interfaces, and enums (i.e. it ignores annotation types).
 */
public class SimpleTypeHierarchyResolver implements TypeHierarchyResolver {

    // TODO parse java api classes properly

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTypeHierarchyResolver.class);
    /**
     * A mapping of names to type nodes.
     * Make sure {@link Map#putIfAbsent(Object, Object)} is used, this is such that if duplicate nodes are created
     * elsewhere, once they are merged into {@link #root} the node they merge into remains in the cache.
     * i.e. the first node entered into the cache.
     */
    private final Map<String, TypeNode> cache = new HashMap<>();
    /**
     * The node which all others are a child of, in java this is 'java.lang.Object' aka 'Object'.
     */
    private final TypeNode root = createPrimitiveTypeNode("java.lang.Object", "Object");
    /**
     * The qualified name resolver to use.
     */
    private final QualifiedNameResolver qualifiedNameResolver;
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();

    public SimpleTypeHierarchyResolver() {
        this(new QualifiedNameResolver());
    }

    public SimpleTypeHierarchyResolver(QualifiedNameResolver qualifiedNameResolver) {
        this.qualifiedNameResolver = qualifiedNameResolver;
        addPrimitives();
    }

    /**
     * Places all nodes in the argument list into their correct position in the argument {@link TypeNode}. If a correct
     * position is not found for an element in the list, then they are placed as a child of the <tt>root</tt> element
     * to form a partial hierarchy.
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
     * Adds in-built Java primitive types to the {@link #root} element.
     */
    private void addPrimitives() {
        // TODO update once java api is fully supported
        TypeNode doublen = createPrimitiveTypeNode("java.lang.Double", "double");
        TypeNode floatn = createPrimitiveTypeNode("java.lang.Float", "float");
        doublen.getChildren().add(floatn);

        TypeNode longn = createPrimitiveTypeNode("java.lang.Long", "long");
        floatn.getChildren().add(longn);
        TypeNode intn = createPrimitiveTypeNode("java.lang.Integer", "int");
        longn.getChildren().add(intn);
        TypeNode shortn = createPrimitiveTypeNode("java.lang.Short", "short");
        intn.getChildren().add(shortn);
        TypeNode byten = createPrimitiveTypeNode("java.lang.Byte", "byte");
        shortn.getChildren().add(byten);

        TypeNode stringn = createPrimitiveTypeNode("java.lang.String", "String");
        TypeNode charn = createPrimitiveTypeNode("java.lang.Character", "char");
        TypeNode booleann = createPrimitiveTypeNode("java.lang.Boolean", "boolean");

        // Add to root
        addToRoot(doublen);
        addToRoot(stringn);
        addToRoot(charn);
        addToRoot(booleann);
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
    private boolean placeInList(List<TypeNode> list, String parent, String child) {
        for (TypeNode node : list) {
            if (node.getQualifiedName().equals(parent)) {
                node.getChildren().add(createTypeNode(child));
                return true;
            }

            if (placeInList(node.getChildren(), parent, child))
                return true;
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

        try {
            List<TypeNode> partialHierarchies = new ArrayList<>();

            // Iterate all code files
            code.forEach((path, statement) -> {
                if (!(statement instanceof CompilationUnitStatement))
                    return;

                CompilationUnitStatement topStatement = (CompilationUnitStatement) statement;
                TypeStatement typeStatement = topStatement.getTypeStatement();

                if (typeStatement instanceof AnnotationStatement)
                    return;
                String currentPkg = topStatement.getPackageStatement().map(p -> p.getPackageName() + ".").orElse("");

                TypeStatementHierarchyResolver resolver = new TypeStatementHierarchyResolver(this, code,
                        partialHierarchies, path, currentPkg, topStatement.getImports(),
                        topStatement.getPackageStatement(), typeStatement, topStatement);
                resolver.visitStatement(typeStatement);
            });

            // Merge in any left over (unresolved fully) partial hierarchy trees in tmp
            mergePartialTrees(root, partialHierarchies);
        } catch (Exception ex) {
            errorListeners.forEach(l -> l.fatalErrorPostProcessing(ex));
        }

        // Log completion message
        LOGGER.debug("Processed {} files in {}ms", code.size(), (System.currentTimeMillis() - startTime));
        LOGGER.debug("Statistics: " + qualifiedNameResolver.getStatistics().toString());
        LOGGER.info("Finished");
    }

    @Override
    public void addErrorListener(CsarErrorListener errorListener) {
        errorListeners.add(errorListener);
    }

    @Override
    public void removeErrorListener(CsarErrorListener errorListener) {
        errorListeners.remove(errorListener);
    }

    /**
     * Resolves each super class of <tt>child</tt> in <tt>superClasses</tt> and then places them in the type hierarchy
     * list, each with a child entry of <tt>child</tt>.
     *
     * @param list the type hierarchy list
     * @param code the code base
     * @param path the path of the child
     * @param parent the type which contains the child
     * @param packageStatement the package statement of the child class
     * @param imports the imports of the child class
     * @param child the name of the child class
     * @param superClasses the superclasses of the child class
     * @param topLevelParent the top-level parent of the parent
     */
    void placeInList(List<TypeNode> list, Map<Path, Statement> code, Path path, TypeStatement parent,
            Optional<PackageStatement> packageStatement, List<ImportStatement> imports, String child,
            List<String> superClasses, TypeStatement topLevelParent) {
        for (String superClass : superClasses) {
            QualifiedType resolvedType = qualifiedNameResolver.resolve(code, path, parent, topLevelParent,
                    packageStatement, imports, superClass);
            String resolvedSuperClassName = resolvedType.getQualifiedName();

            // Add to tmp structure, if there is no place for it it then add it as a new child
            if (!placeInList(list, resolvedSuperClassName, child)) {
                TypeNode superNode = createTypeNode(resolvedSuperClassName);
                TypeNode childNode = createTypeNode(child);
                superNode.getChildren().add(childNode);
                list.add(superNode);
            }
        }
    }

    /**
     * Returns <tt>true</tt> if the first type is a superclass of, or equal to, the second type. This is thread-safe.
     *
     * @param type1 a qualified name which may be a superclass
     * @param type2 a qualified name which may be a subclass
     * @return returns if the first type is a superclass of the second type
     */
    public boolean isSubtype(String type1, String type2) {
        // TODO sanitize arrays etc?
        // Normalize varargs
        type1 = TypeHelper.normalizeVarArgs(type1);
        type2 = TypeHelper.normalizeVarArgs(type2);
        return type1.equals(type2) || isStrictlySubtype(type1, type2);
    }

    /**
     * Returns <tt>true</tt> if the first type is a superclass of the second type, it looks in {@link #cache} for the
     * first type argument.
     *
     * @param type1 a qualified name which may be a superclass
     * @param type2 a qualified name which may be a subclass
     * @return returns if the first type is a superclass of the second type
     */
    public boolean isStrictlySubtype(String type1, String type2) {
        TypeNode subRoot = cache.get(type1);
        return subRoot != null && subRoot.containsQualifiedName(type2);
    }

    void addToRoot(TypeNode node) {
        root.getChildren().add(node);
    }

    private TypeNode createTypeNode(String qualifiedName) {
        TypeNode node = new TypeNode(qualifiedName);
        node.cache(cache);
        return node;
    }

    private TypeNode createPrimitiveTypeNode(String qualifiedName, String primitiveName) {
        PrimitiveTypeNode node = new PrimitiveTypeNode(qualifiedName, primitiveName);
        node.cache(cache);
        return node;
    }
}
