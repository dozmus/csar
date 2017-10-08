package org.qmul.csar.lang.descriptor;

import org.qmul.csar.lang.Descriptor;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractCommentDescriptor implements Descriptor {

    private final Optional<String> content;

    public AbstractCommentDescriptor(Optional<String> content) {
        this.content = content;
    }

    @Override
    public boolean lenientEquals(Descriptor other) {
        return false; // TODO impl
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCommentDescriptor that = (AbstractCommentDescriptor) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return String.format("AbstractCommentDescriptor{content=%s}", content);
    }
}
