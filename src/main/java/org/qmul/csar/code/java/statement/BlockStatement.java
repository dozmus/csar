package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BlockStatement implements Statement {

    public static final BlockStatement EMPTY = new BlockStatement(new ArrayList<>());

    private final List<Statement> statements;

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
        return String.format("ExpressionList{statements=%s}", statements);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return statements.stream()
                .map(st -> st.toPseudoCode(indentation))
                .collect(Collectors.joining(StringUtils.LINE_SEPARATOR));
    }
}
