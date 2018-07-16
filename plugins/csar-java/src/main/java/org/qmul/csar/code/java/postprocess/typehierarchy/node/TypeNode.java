package org.qmul.csar.code.java.postprocess.typehierarchy.node;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.util.ToStringStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A node representing the type hierarchy of a single type.
 */
public class TypeNode {

    private final String qualifiedName;
    private final List<TypeNode> children = new ArrayList<>();

    public TypeNode(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    /**
     * Returns <tt>true</tt> if the attempted insertion was successful, it can only be made if a node in <tt>root</tt>
     * is found which has the same <tt>qualifiedName</tt> as <tt>node</tt>, then all of the children of <tt>node</tt>
     * are added to <tt>root</tt>. This returns after a single insertion at most.
     * <p>
     * If the argument is already contained, it will be added again.
     *
     * @param root the node to add to
     * @param node the node whose children to add
     * @return <tt>true</tt> if insertion was successful
     */
    public static boolean insert(TypeNode root, TypeNode node) {
        return node.insert(root);
    }

    public boolean insert(TypeNode root) {
        if (qualifiedNameEquals(root.getQualifiedName())) {
            root.children.addAll(children);
            return true;
        } else {
            for (TypeNode child : root.children) {
                if (insert(child, this))
                    return true;
            }
        }
        return false;
    }

    public boolean containsName(String name) {
        return containsName(name, false);
    }

    public boolean containsName(String name, boolean checkCurrent) {
        if (checkCurrent && nameEquals(name)) {
            return true;
        } else {
            for (TypeNode child : children) {
                if (child.containsName(name, true))
                    return true;
            }
            return false;
        }
    }

    public boolean containsQualifiedName(String qualifiedName) {
        return containsQualifiedName(qualifiedName, false);
    }

    /**
     * Returns <tt>true</tt> if the argument <tt>String</tt> is contained in any child of the argument
     * <tt>TypeNode</tt>. The argument <tt>TypeNode</tt>'s value is only checked if <tt>checkCurrent</tt> is
     * <tt>true</tt>.
     *
     * @param qualifiedName the name to check for
     * @param checkCurrent if the current node's value should be checked too
     * @return if the argument node contains the argument qualifiedName
     */
    public boolean containsQualifiedName(String qualifiedName, boolean checkCurrent) {
        if (checkCurrent && qualifiedNameEquals(qualifiedName)) {
            return true;
        } else {
            for (TypeNode child : children) {
                if (child.containsQualifiedName(qualifiedName, true))
                    return true;
            }
            return false;
        }
    }

    public boolean qualifiedNameEquals(String qualifiedName) {
        return getQualifiedName().equals(qualifiedName);
    }

    public boolean nameEquals(String name) {
        return qualifiedNameEquals(name) || getQualifiedName().endsWith("." + name);
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public List<TypeNode> getChildren() {
        return children;
    }

    public void cache(Map<String, TypeNode> cache) {
        cache.putIfAbsent(qualifiedName, this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("qualifiedName", qualifiedName)
                .append("children", children)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeNode typeNode = (TypeNode) o;
        return Objects.equals(qualifiedName, typeNode.qualifiedName) && Objects.equals(children, typeNode.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, children);
    }
}
