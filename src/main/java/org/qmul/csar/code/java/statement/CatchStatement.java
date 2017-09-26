package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Statement;

import java.util.Objects;

/**
 * A catch statement, used as an element within try and try-with-resources statements.
 * @see TryStatement
 * @see TryWithResourcesStatement
 */
public class CatchStatement implements Statement {

    private final LocalVariableStatements variable;
    private final BlockStatement block;

    public CatchStatement(LocalVariableStatements variable, BlockStatement block) {
        this.variable = variable;
        this.block = block;
    }

    public LocalVariableStatements getVariable() {
        return variable;
    }

    public BlockStatement getBlock() {
        return block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatchStatement that = (CatchStatement) o;
        return Objects.equals(variable, that.variable) && Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, block);
    }

    @Override
    public String toString() {
        return String.format("CatchStatement{variable=%s, block=%s}", variable, block);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return "catch"; // TODO write
    }
}
