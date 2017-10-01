package org.qmul.csar.lang.descriptor;

import org.qmul.csar.lang.Descriptor;

import java.util.Objects;
import java.util.Optional;

public class BlockCommentDescriptor extends AbstractCommentDescriptor {

    private final Optional<Boolean> javadoc;

    public BlockCommentDescriptor(Optional<String> content, Optional<Boolean> javadoc) {
        super(content);
        this.javadoc = javadoc;
    }

    public Optional<Boolean> getJavadoc() {
        return javadoc;
    }

    @Override
    public boolean lenientEquals(Descriptor other) {
        return false; // TODO impl
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BlockCommentDescriptor that = (BlockCommentDescriptor) o;
        return Objects.equals(javadoc, that.javadoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), javadoc);
    }

    @Override
    public String toString() {
        return String.format("BlockCommentDescriptor{javadoc=%s} %s", javadoc, super.toString());
    }
}
