package org.qmul.csar.code.java.postprocess.typehierarchy.node;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.util.ToStringStyles;

import java.util.Map;
import java.util.Objects;

/**
 * A node representing the type hierarchy of a single primitive type.
 */
public class PrimitiveTypeNode extends TypeNode {

    private final String primitiveName;
    private final String nonQualifiedName;

    public PrimitiveTypeNode(String qualifiedName, String primitiveName) {
        super(qualifiedName);
        int dotIdx = qualifiedName.lastIndexOf('.');
        this.nonQualifiedName = (dotIdx == -1) ? qualifiedName : qualifiedName.substring(dotIdx + 1);
        this.primitiveName = primitiveName;
    }

    public boolean containsQualifiedName(String qualifiedName) {
        return containsQualifiedName(qualifiedName, true);
    }

    public boolean qualifiedNameEquals(String qualifiedName) {
        return getQualifiedName().equals(qualifiedName) || primitiveName.equals(qualifiedName)
                || nonQualifiedName.equals(qualifiedName);
    }

    public boolean nameEquals(String name) {
        return super.nameEquals(name) || primitiveName.equals(name)
                || nonQualifiedName.equals(name);
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
