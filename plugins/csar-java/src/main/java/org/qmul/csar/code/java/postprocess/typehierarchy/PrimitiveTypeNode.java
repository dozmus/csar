package org.qmul.csar.code.java.postprocess.typehierarchy;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.util.ToStringStyles;

import java.util.Map;
import java.util.Objects;

/**
 * A node representing the type hierarchy of a single primitive type.
 */
class PrimitiveTypeNode extends TypeNode {

    private final String primitiveName;
    private final String nonQualifiedName;

    public PrimitiveTypeNode(String qualifiedName, String primitiveName) {
        super(qualifiedName);
        int dotIdx = qualifiedName.lastIndexOf('.');
        this.nonQualifiedName = (dotIdx == -1) ? qualifiedName : qualifiedName.substring(dotIdx + 1);
        this.primitiveName = primitiveName;
    }

    public boolean insert(TypeNode root) {
        if (qualifiedNameEquals(root.getQualifiedName())) {
            root.getChildren().addAll(getChildren());
            return true;
        } else {
            for (TypeNode child : root.getChildren()) {
                if (insert(child, this))
                    return true;
            }
        }
        return false;
    }

    public boolean containsQualifiedName(String qualifiedName) {
        return containsQualifiedName(qualifiedName, true);
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
            for (TypeNode child : getChildren()) {
                if (child.containsQualifiedName(qualifiedName, true))
                    return true;
            }
            return false;
        }
    }

    private boolean qualifiedNameEquals(String qualifiedName) {
        return getQualifiedName().equals(qualifiedName) || primitiveName.equals(qualifiedName)
                || nonQualifiedName.equals(qualifiedName);
    }

    @Override
    public void cache(Map<String, TypeNode> cache) {
        cache.putIfAbsent(super.getQualifiedName(), this);
        cache.putIfAbsent(nonQualifiedName, this);
        cache.putIfAbsent(primitiveName, this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("qualifiedName", getQualifiedName())
                .append("primitiveName", primitiveName)
                .append("nonQualifiedName", nonQualifiedName)
                .append("children", getChildren())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PrimitiveTypeNode that = (PrimitiveTypeNode) o;
        return Objects.equals(primitiveName, that.primitiveName)
                && Objects.equals(nonQualifiedName, that.nonQualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), primitiveName, nonQualifiedName);
    }
}
