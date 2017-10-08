package org.qmul.csar.code.parse.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An enum constant.
 */
public class EnumConstantStatement implements Statement {

    private final String identifierName;
    private final List<Expression> arguments;
    private final BlockStatement block; // TODO make Optional
    private final List<Annotation> annotations;

    public EnumConstantStatement(String identifierName, List<Expression> arguments, BlockStatement block,
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

    public BlockStatement getBlock() {
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
        return String.format("EnumConstantStatement{identifierName='%s', arguments=%s, block=%s, annotations=%s}",
                identifierName, arguments, block, annotations);
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder builder = new StringBuilder();

        if (getAnnotations().size() > 0) {
            getAnnotations().forEach(annotation -> builder.append(annotation.toPseudoCode(indentation))
                    .append(StringUtils.LINE_SEPARATOR));
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

        return builder.append(" {")
                .append(StringUtils.LINE_SEPARATOR)
                .append(block.toPseudoCode(indentation + 1)) // TODO comma-delimiter constants properly
                .append(StringUtils.LINE_SEPARATOR)
                .append(StringUtils.indentation(indentation))
                .append("}")
                .toString();
    }
}
