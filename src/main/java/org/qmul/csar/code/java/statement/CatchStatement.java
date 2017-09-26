package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * A catch statement, used as an element within try and try-with-resources statements.
 * @see TryStatement
 * @see TryWithResourcesStatement
 */
public class CatchStatement implements Statement {

    private final LocalVariableStatements variable; // TODO replace with custom variable
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
        StringBuilder builder = new StringBuilder();
        List<LocalVariableStatement> locals = variable.getLocals();
        String catchVariableString = "";

        if (locals.get(0).getDescriptor().getFinalModifier().isPresent()
                && locals.get(0).getDescriptor().getFinalModifier().get()) {
            catchVariableString += "final ";
        }

        for (int i = 0; i < locals.size(); i++) {
            LocalVariableStatement local = locals.get(i);
            catchVariableString += local.getDescriptor().getIdentifierType().get();

            if (i + 1 < locals.size())
                catchVariableString += " | ";

            if (i + 1 == locals.size())
                catchVariableString += " " + local.getDescriptor().getIdentifierName();
        }

        builder.append(" catch(").append(catchVariableString).append(") {")
                .append(StringUtils.LINE_SEPARATOR)
                .append(block.toPseudoCode(indentation + 1))
                .append(StringUtils.LINE_SEPARATOR)
                .append(StringUtils.indentation(indentation))
                .append("}");
        return builder.toString();
    }
}
