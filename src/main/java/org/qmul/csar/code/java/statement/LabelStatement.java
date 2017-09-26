package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

/**
 * A label statement.
 */
public class LabelStatement implements Statement {

    private final String identifier;
    private final Statement statement;

    public LabelStatement(String identifier, Statement statement) {
        this.identifier = identifier;
        this.statement = statement;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabelStatement that = (LabelStatement) o;
        return Objects.equals(identifier, that.identifier) && Objects.equals(statement, that.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, statement);
    }

    @Override
    public String toString() {
        return String.format("LabelStatement{identifier='%s', statement=%s}", identifier, statement);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%s%s: %s", StringUtils.indentation(indentation), identifier, statement.toPseudoCode());
    }
}
