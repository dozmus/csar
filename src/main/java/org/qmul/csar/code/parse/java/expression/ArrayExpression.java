package org.qmul.csar.code.parse.java.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ArrayExpression implements Expression {

    private List<Expression> expressions = new ArrayList<>();

    public ArrayExpression(List<Expression> expressions) {
        this.expressions = Collections.unmodifiableList(expressions);
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayExpression that = (ArrayExpression) o;
        return Objects.equals(expressions, that.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("expressions", expressions)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder sb = new StringBuilder().append(StringUtils.indentation(indentation))
                .append("{");

        for (int i = 0; i < expressions.size(); i++) {
            sb.append(expressions.get(i).toPseudoCode());

            if (i + 1 < expressions.size())
                sb.append(", ");
        }
        return sb.append("}").toString();
    }
}
