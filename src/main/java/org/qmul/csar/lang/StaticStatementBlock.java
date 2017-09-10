package org.qmul.csar.lang;

import java.util.*;

public class StaticStatementBlock extends LanguageElement {

    private List<Statement> statements;

    public StaticStatementBlock(List<Statement> statements) {
        super(Type.STATIC_BLOCK);
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
        StaticStatementBlock that = (StaticStatementBlock) o;
        return Objects.equals(statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), statements);
    }

    @Override
    public String toString() {
        return String.format("StaticStatementBlock{statements=%s} %s", statements, super.toString());
    }

    public static class Builder {

        private List<Statement> statements = new ArrayList<>();

        public Builder statements(List<Statement> statements) {
            this.statements = statements;
            return this;
        }

        public Builder statements(Statement... statements) {
            this.statements = Arrays.asList(statements);
            return this;
        }

        public StaticStatementBlock build() {
            return new StaticStatementBlock(statements);
        }
    }
}
