package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class ReturnStatement implements Statement {

    private final Optional<Expression> expression;

    public ReturnStatement(Optional<Expression> expression) {
        this.expression = expression;
    }

    public ReturnStatement(Expression expression) {
        this.expression = Optional.of(expression);
    }

    public Optional<Expression> getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnStatement that = (ReturnStatement) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Override
    public String toString() {
        return String.format("ReturnStatement{expression=%s}", expression);
    }

    @Override
    public String toPseudoCode(int indentation) {
        if (expression.isPresent()) {
            return String.format("%sreturn %s;", StringUtils.indentation(indentation), expression.get().toPseudoCode());
        }
        return StringUtils.indentation(indentation) + "return;";
    }
}
