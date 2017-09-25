package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A container for a {@link List<LocalVariableStatement>}.
 */
public class LocalVariableStatements implements Statement {

    private final List<LocalVariableStatement> locals;

    /**
     * Constructs a new {@link LocalVariableStatements} containing the argument list.
     * Note: {@link #locals} is assigned the result of calling {@link Collections#unmodifiableList(List)} on the
     * argument list.
     * @param locals the locals it should contain
     */
    public LocalVariableStatements(List<LocalVariableStatement> locals) {
        this.locals = Collections.unmodifiableList(locals);
    }

    public List<LocalVariableStatement> getLocals() {
        return locals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalVariableStatements that = (LocalVariableStatements) o;
        return Objects.equals(locals, that.locals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locals);
    }

    @Override
    public String toString() {
        return String.format("LocalVariableStatements{locals=%s}", locals);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation) + "locals"; // TODO write
    }
}
