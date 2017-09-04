package org.qmul.csar.query.domain;

import java.util.Objects;
import java.util.Optional;

public class CommentLanguageElement extends LanguageElement {

    private CommentType commentType;
    private Optional<Boolean> javadoc;
    private String content;

    public CommentLanguageElement(CommentType commentType, Optional<Boolean> javadoc, String content) {
        super(Type.COMMENT);
        this.commentType = commentType;
        this.javadoc = javadoc;
        this.content = content;
    }

    public CommentType getCommentType() {
        return commentType;
    }

    public Optional<Boolean> getJavadoc() {
        return javadoc;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommentLanguageElement that = (CommentLanguageElement) o;
        return commentType == that.commentType &&
                Objects.equals(javadoc, that.javadoc) &&
                Objects.equals(content, that.content);
    }

    @Override
    public String toString() {
        return String.format("CommentLanguageElement{commentType=%s, javadoc=%s, content='%s'}", commentType, javadoc,
                content);
    }

    public enum CommentType {
        SINGLE, MULTI
    }
}
