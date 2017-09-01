package org.qmul.csar.query.domain;

import java.util.Objects;

public class CommentLanguageElement extends LanguageElement {

    private CommentType commentType;
    private boolean javadoc;
    private String content;

    public CommentLanguageElement(CommentType commentType, boolean javadoc, String content) {
        this.commentType = commentType;
        this.javadoc = javadoc;
        this.content = content;
    }

    public CommentType getCommentType() {
        return commentType;
    }

    public boolean isJavadoc() {
        return javadoc;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentLanguageElement that = (CommentLanguageElement) o;
        return javadoc == that.javadoc &&
                commentType == that.commentType &&
                Objects.equals(content, that.content);
    }

    public enum CommentType {
        SINGLE, MULTI
    }
}
