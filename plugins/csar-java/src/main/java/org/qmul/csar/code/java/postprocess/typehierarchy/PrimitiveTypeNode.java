package org.qmul.csar.code.java.postprocess.typehierarchy;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
        System.out.println(qualifiedName + " called");
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

    public boolean isStrictlySubtype(String type1, String type2) {
        if (qualifiedNameEquals(type1)) {
            return containsQualifiedName(type2, true);
        } else {
            for (TypeNode child : getChildren()) {
                if (child.isStrictlySubtype(type1, type2))
                    return true;
            }
        }
        return false;
    }

    private boolean qualifiedNameEquals(String qualifiedName) {
        return getQualifiedName().equals(qualifiedName) || primitiveName.equals(qualifiedName)
                || nonQualifiedName.equals(qualifiedName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("qualifiedName", getQualifiedName())
                .append("primitiveName", primitiveName)
                .append("nonQualifiedName", nonQualifiedName)
                .append("children", getChildren())
                .toString();
    }
}
