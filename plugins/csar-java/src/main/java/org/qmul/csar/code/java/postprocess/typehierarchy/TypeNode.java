package org.qmul.csar.code.java.postprocess.typehierarchy;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * A node representing the type hierarchy of a single type.
 */
final class TypeNode {

    private final String qualifiedName;
    private final List<TypeNode> children = new ArrayList<>();

    public TypeNode(String qualifiedName) {
        this.qualifiedName = qualifiedName;
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

    public boolean containsQualifiedName(String qualifiedName) {
        return containsQualifiedName(this, qualifiedName, false);
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public List<TypeNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("qualifiedName", qualifiedName)
                .append("children", children)
                .toString();
    }
}
