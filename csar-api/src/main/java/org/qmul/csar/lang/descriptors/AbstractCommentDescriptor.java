package org.qmul.csar.lang.descriptors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Descriptor;
import org.qmul.csar.util.OptionalUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractCommentDescriptor implements Descriptor {

    private Optional<String> content;

    public AbstractCommentDescriptor() {
    }

    public AbstractCommentDescriptor(Optional<String> content) {
        this.content = content;
    }

    @Override
    public boolean lenientEquals(Descriptor o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCommentDescriptor that = (AbstractCommentDescriptor) o;
        return OptionalUtils.lenientEquals(content, that.content);
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
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("content", content)
                .toString();
    }
}
