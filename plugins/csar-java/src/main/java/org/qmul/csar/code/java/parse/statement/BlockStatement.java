package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.ToStringStyles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A container for a {@link List<Statement>}.
 */
public class BlockStatement implements Statement {

    /**
     * An empty {@link BlockStatement}.
     */
    public static final BlockStatement EMPTY = new BlockStatement(new ArrayList<>());

    private List<Statement> statements;

    public BlockStatement() {
    }

    /**
     * Constructs a new {@link BlockStatement} containing the argument list.
     * Note: {@link #statements} is assigned the result of calling {@link Collections#unmodifiableList(List)} on the
     * argument list.
     * @param statements the statements it should contain
     * @see #EMPTY
     */
    public BlockStatement(List<Statement> statements) {
        this.statements = Collections.unmodifiableList(statements);
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockStatement that = (BlockStatement) o;
        return Objects.equals(statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statements);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_MULTI_LINE_STYLE)
                .append("statements", statements)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return statements.stream()
                .map(st -> st.toPseudoCode(indentation))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
