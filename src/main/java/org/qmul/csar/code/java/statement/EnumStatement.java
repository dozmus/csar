package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.descriptor.EnumDescriptor;
import org.qmul.csar.lang.TypeStatement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An enum type declaration.
 */
public class EnumStatement implements TypeStatement {

    private final EnumDescriptor descriptor;
    private final BlockStatement block;
    private final List<Annotation> annotations;

    public EnumStatement(EnumDescriptor descriptor, BlockStatement block, List<Annotation> annotations) {
        this.descriptor = descriptor;
        this.block = block;
        this.annotations = Collections.unmodifiableList(annotations);
    }

    public EnumDescriptor getDescriptor() {
        return descriptor;
    }

    public BlockStatement getBlock() {
        return block;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnumStatement that = (EnumStatement) o;
        return Objects.equals(descriptor, that.descriptor)
                && Objects.equals(block, that.block)
                && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor, block, annotations);
    }

    @Override
    public String toString() {
        return String.format("EnumStatement{descriptor=%s, block=%s, annotations=%s}", descriptor, block, annotations);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return "enum"; // TODO write
    }
}
