package org.qmul.csar.code.java.parse.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.util.StringUtils;

/**
 * A throw statement.
 */
public class ThrowStatement extends ExpressionStatement {

    public ThrowStatement() {
    }

    public ThrowStatement(Expression expression) {
        super(expression);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return String.format("%sthrow %s", StringUtils.indentation(indentation), getExpression().toPseudoCode());
    }
}
