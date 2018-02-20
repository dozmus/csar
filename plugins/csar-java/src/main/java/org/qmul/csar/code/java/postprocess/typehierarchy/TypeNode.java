package org.qmul.csar.code.java.postprocess.typehierarchy;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * A node representing the type hierarchy of a single type.
 */
class TypeNode {

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
        if (root.qualifiedName.equals(qualifiedName)) {
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
        if (checkCurrent && this.qualifiedName.equals(qualifiedName)) {
            return true;
        } else {
            for (TypeNode child : children) {
                if (child.containsQualifiedName(qualifiedName, true))
                    return true;
            }
            return false;
        }
    }

    public boolean isStrictlySubtype(String type1, String type2) {
        if (qualifiedName.equals(type1)) {
            return containsQualifiedName(type2);
        } else {
            for (TypeNode child : children) {
                if (child.isStrictlySubtype(type1, type2))
                    return true;
            }
        }
        return false;
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
