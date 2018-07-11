package org.qmul.csar.code.java.parse.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MethodReferenceExpression implements Expression {

    private Expression qualifier;
    private Optional<List<TypeArgument>> typeArguments;
    /**
     * This may also be the new keyword.
     */
    private String identifier;

    public MethodReferenceExpression(Expression qualifier, String identifier,
            Optional<List<TypeArgument>> typeArguments) {
        this.qualifier = qualifier;
        this.typeArguments = typeArguments;
        this.identifier = identifier;
    }

    public Expression getQualifier() {
        return qualifier;
    }

    public Optional<List<TypeArgument>> getTypeArguments() {
        return typeArguments;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toPseudoCode(int indentation) {
        String typeArgs = typeArguments.map(types -> {
            StringBuilder args = new StringBuilder();

            for (int i = 0; i < types.size(); i++) {
                args.append(types.get(i).toPseudoCode(0));

                if (i + 1 < types.size())
                    args.append(",");
            }
            return "<" + args + ">";
        }).orElse("");
        return StringUtils.indentation(indentation) + qualifier.toPseudoCode(0) + typeArgs + "::" + identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodReferenceExpression that = (MethodReferenceExpression) o;
        return Objects.equals(qualifier, that.qualifier)
                && Objects.equals(typeArguments, that.typeArguments)
                && Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifier, typeArguments, identifier);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("qualifier", qualifier)
                .append("typeArguments", typeArguments)
                .append("identifier", identifier)
                .toString();
    }
}
