package org.qmul.csar.lang;

import java.util.List;
import java.util.Objects;

public class BlockLanguageElement extends LanguageElement {

    private final List<Statement> statements;

    public BlockLanguageElement(List<Statement> statements) {
        super(Type.BLOCK);
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BlockLanguageElement that = (BlockLanguageElement) o;
        return Objects.equals(statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), statements);
    }

    @Override
    public String toString() {
        return String.format("BlockLanguageElement{statements=%s} %s", statements, super.toString());
    }
}
