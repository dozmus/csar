package org.qmul.csar.code.parse.java.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An enum constant.
 */
public class EnumConstantStatement implements Statement {

    private final String identifierName;
    private final List<Expression> arguments;
    private final Optional<BlockStatement> block;
    private final List<Annotation> annotations;

    public EnumConstantStatement(String identifierName, List<Expression> arguments, Optional<BlockStatement> block,
            List<Annotation> annotations) {
        this.identifierName = identifierName;
        this.arguments = Collections.unmodifiableList(arguments);
        this.block = block;
        this.annotations = Collections.unmodifiableList(annotations);
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public Optional<BlockStatement> getBlock() {
        return block;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnumConstantStatement that = (EnumConstantStatement) o;
        return Objects.equals(identifierName, that.identifierName)
                && Objects.equals(arguments, that.arguments)
                && Objects.equals(block, that.block)
                && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierName, arguments, block, annotations);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("identifierName", identifierName)
                .append("arguments", arguments)
                .append("block", block)
                .append("annotations", annotations)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder builder = new StringBuilder();

        if (getAnnotations().size() > 0) {
            getAnnotations().forEach(annotation -> builder.append(annotation.toPseudoCode(indentation))
                    .append(System.lineSeparator()));
        }
        builder.append(StringUtils.indentation(indentation));
        builder.append(identifierName);

        if (arguments.size() > 0) {
            String args = "(";

            for (int i = 0; i < arguments.size(); i++) {
                args += arguments.get(i).toPseudoCode();

                if (i + 1 < arguments.size())
                    args += ", ";
            }
            builder.append(args).append(")");
        }
        return block.map(blockStatement -> builder.append(" {")
                .append(System.lineSeparator())
                .append(blockStatement.toPseudoCode(indentation + 1))
                .append(System.lineSeparator())
                .append(StringUtils.indentation(indentation))
                .append("}")
                .toString())
                .orElseGet(builder::toString);
    }
}
