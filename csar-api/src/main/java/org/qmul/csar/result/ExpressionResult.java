package org.qmul.csar.result;

import org.qmul.csar.lang.Expression;

import java.nio.file.Path;

/**
 * A match from the searching performed by {@link org.qmul.csar.Csar} which yielded a {@link Expression}.
 */
public class ExpressionResult extends Result {

    private final Expression expression;

    /**
     * Creates a new ExpressionResult according to the arguments provided.
     *
     * @param path the path which contained this result
     * @param lineNumber the line at which the result was found
     * @param codeFragment the result's code fragment
     * @param expression the result's statement
     */
    public ExpressionResult(Path path, int lineNumber, String codeFragment, Expression expression) {
        super(path, lineNumber, codeFragment);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
