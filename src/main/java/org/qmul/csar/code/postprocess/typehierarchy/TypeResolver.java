package org.qmul.csar.code.postprocess.typehierarchy;

import org.qmul.csar.code.parse.java.statement.ClassStatement;
import org.qmul.csar.code.parse.java.statement.EnumStatement;
import org.qmul.csar.code.parse.java.statement.ImportStatement;
import org.qmul.csar.code.parse.java.statement.PackageStatement;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.StatementVisitor;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.ClassDescriptor;
import org.qmul.csar.lang.descriptor.EnumDescriptor;

import java.nio.file.Path;
import java.util.*;

/**
 * Resolves the type hierarchy of a given statement.
 */
final class TypeResolver extends StatementVisitor {

    private final Map<Path, Statement> code;
    private final List<TypeNode> tmp;
    private final List<ImportStatement> imports;
    private final Optional<PackageStatement> packageStatement;
    private final Map<Integer, String> map = new HashMap<>(); // maps 'nesting number' to 'prefix' at that nesting
    private final Path path;
    private final TypeStatement parent;
    private TypeHierarchyResolver typeHierarchyResolver;
    private String currentIdentifierName;
    private int nesting = 0;

    TypeResolver(TypeHierarchyResolver typeHierarchyResolver, Map<Path, Statement> code, List<TypeNode> tmp,
            Path path, String currentPkg,
            List<ImportStatement> imports, Optional<PackageStatement> packageStatement,
            TypeStatement parent) {
        this.typeHierarchyResolver = typeHierarchyResolver;
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
            typeHierarchyResolver.getRoot().getChildren().add(getFromListOrDefault(tmp, currentIdentifierName));
        } else {
            typeHierarchyResolver.placeInList(tmp, code, path, parent, packageStatement, imports, currentIdentifierName,
                    superClasses);
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
