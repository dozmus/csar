package org.qmul.csar.query.domain;

import java.util.Objects;
import java.util.Optional;

public class CommentLanguageElement extends LanguageElement {

    private CommentType commentType;
    private Optional<Boolean> javadoc;
    private Optional<String> content;

    public CommentLanguageElement(CommentType commentType, Optional<Boolean> javadoc, Optional<String> content) {
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

    public Optional<String> getContent() {
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
    public int hashCode() {
        return Objects.hash(super.hashCode(), commentType, javadoc, content);
    }

    @Override
    public String toString() {
        return String.format("CommentLanguageElement{commentType=%s, javadoc=%s, content=%s} %s", commentType, javadoc,
                content, super.toString());
    }

    public static class Builder {

        private final CommentType commentType;
        private Optional<Boolean> javadoc = Optional.empty();
        private Optional<String> content = Optional.empty();

        public Builder(CommentType commentType) {
            this.commentType = commentType;
        }

        public Builder javadoc(boolean javadoc) {
            this.javadoc = Optional.of(javadoc);
            return this;
        }

        public Builder content(String content) {
            this.content = Optional.of(content);
            return this;
        }

        public CommentLanguageElement build() {
            return new CommentLanguageElement(commentType, javadoc, content);
        }
    }
}