package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptors.LocalVariableDescriptor;
import org.qmul.csar.util.StringUtils;

import java.util.*;

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
        return new ToStringBuilder(this)
                .append("locals", locals)
                .toString();
    }

    /**
     * Note: This assumes every element in {@link #locals} have the same annotations and final modifier (or lack of).
     * @param indentation
     * @return
     */
    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder builder = new StringBuilder()
                .append(StringUtils.indentation(indentation));

        // Annotations
        Set<Annotation> annotations = new HashSet<>();
        locals.stream().map(LocalVariableStatement::getAnnotations).forEach(annotations::addAll);
        annotations.forEach(annotation -> builder.append(annotation.toPseudoCode(indentation)).append(" "));

        // Final modifier
        for (LocalVariableStatement local : locals) {
            LocalVariableDescriptor desc = local.getDescriptor();

            if (desc.getFinalModifier().isPresent() && desc.getFinalModifier().get()) {
                builder.append("final ");
                break;
            }
        }

        // Bodies
        for (int i = 0; i < locals.size(); i++) {
            Optional<Expression> valueExpression = locals.get(i).getValueExpression();
            LocalVariableDescriptor desc = locals.get(i).getDescriptor();

            builder.append(desc.getIdentifierType().map(t -> t + " ").orElse(""))
                    .append(desc.getIdentifierName())
                    .append(valueExpression.map(expression -> " = " + expression.toPseudoCode()).orElse(""));

            if (i + 1 < locals.size())
                builder.append(", ");
        }
        return builder.append(";").toString();
    }
}
