package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;

import java.util.Objects;

/**
 * A for-each statement.
 */
public class ForEachStatement implements Statement {

    private final LocalVariableStatement variable;
    private final Expression collection;
    private final Statement statement;

    public ForEachStatement(LocalVariableStatement variable, Expression collection, Statement statement) {
        this.variable = variable;
        this.collection = collection;
        this.statement = statement;
    }

    public LocalVariableStatement getVariable() {
        return variable;
    }

    public Expression getCollection() {
        return collection;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForEachStatement that = (ForEachStatement) o;
        return Objects.equals(variable, that.variable)
                && Objects.equals(collection, that.collection)
                && Objects.equals(statement, that.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, collection, statement);
    }

    @Override
    public String toString() {
        return String.format("ForEachStatement{variable=%s, collection=%s, statement=%s}", variable, collection,
                statement);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return "foreach"; // TODO write
    }
}
