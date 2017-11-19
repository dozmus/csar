package org.qmul.csar.lang;

import org.qmul.csar.code.parse.java.expression.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExpressionVisitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionVisitor.class);

    public void visitExpression(Expression expression) {
        LOGGER.trace("Visit: {}", expression.getClass().toString());

        if (expression instanceof ArrayAccessExpression) {
            visitArrayAccessExpression((ArrayAccessExpression)expression);
            exitArrayAccessExpression((ArrayAccessExpression)expression);
        } else if (expression instanceof ArrayExpression) {
            visitArrayExpression((ArrayExpression)expression);
            exitArrayExpression((ArrayExpression)expression);
        } else if (expression instanceof ArrayInitializationExpression) {
            visitArrayInitializationExpression((ArrayInitializationExpression)expression);
            exitArrayInitializationExpression((ArrayInitializationExpression)expression);
        } else if (expression instanceof BinaryExpression) {
            visitBinaryExpression((BinaryExpression)expression);
            exitBinaryExpression((BinaryExpression)expression);
        } else if (expression instanceof CastExpression) {
            visitCastExpression((CastExpression)expression);
            exitCastExpression((CastExpression)expression);
        } else if (expression instanceof InstantiateClassExpression) {
            visitInstantiateClassExpression((InstantiateClassExpression)expression);
            exitInstantiateClassExpression((InstantiateClassExpression)expression);
        } else if (expression instanceof LambdaExpression) {
            visitLambdaExpression((LambdaExpression)expression);
            exitLambdaExpression((LambdaExpression)expression);
        } else if (expression instanceof MethodCallExpression) {
            visitMethodCallExpression((MethodCallExpression)expression);
            exitMethodCallExpression((MethodCallExpression)expression);
        } else if (expression instanceof ParenthesisExpression) {
            visitParenthesisExpression((ParenthesisExpression)expression);
            exitParenthesisExpression((ParenthesisExpression)expression);
        } else if (expression instanceof PostfixedExpression) {
            visitPostfixedExpression((PostfixedExpression)expression);
            exitPostfixedExpression((PostfixedExpression)expression);
        } else if (expression instanceof PrefixedExpression) {
            visitPrefixedExpression((PrefixedExpression)expression);
            exitPrefixedExpression((PrefixedExpression)expression);
        } else if (expression instanceof SquareBracketsExpression) {
            visitSquareBracketsExpression((SquareBracketsExpression)expression);
            exitSquareBracketsExpression((SquareBracketsExpression)expression);
        } else if (expression instanceof TernaryExpression) {
            visitTernaryExpression((TernaryExpression)expression);
            exitTernaryExpression((TernaryExpression)expression);
        } else if (expression instanceof UnitExpression) {
            visitUnitExpression((UnitExpression)expression);
            exitUnitExpression((UnitExpression)expression);
        }
    }

    public void visitArrayAccessExpression(ArrayAccessExpression expression) {
        visitExpression(expression.getArray());
        visitExpression(expression.getIndex());
    }

    public void exitArrayAccessExpression(ArrayAccessExpression expression) {
    }

    public void visitArrayExpression(ArrayExpression expression) {
        for (Expression expr : expression.getExpressions()) {
            visitExpression(expr);
        }
    }

    public void exitArrayExpression(ArrayExpression expression) {
    }

    public void visitArrayInitializationExpression(ArrayInitializationExpression expression) {
        for (Expression expr : expression.getExpressions()) {
            visitExpression(expr);
        }
    }

    public void exitArrayInitializationExpression(ArrayInitializationExpression expression) {
    }

    public void visitBinaryExpression(BinaryExpression expression) {
        visitExpression(expression.getLeft());
        visitExpression(expression.getRight());
    }

    public void exitBinaryExpression(BinaryExpression expression) {
    }

    public void visitCastExpression(CastExpression expression) {
        visitExpression(expression.getExpression());
    }

    public void exitCastExpression(CastExpression expression) {
    }

    public void visitInstantiateClassExpression(InstantiateClassExpression expression) {
        // TODO visit block statement

        for (Expression expr : expression.getArguments()) {
            visitExpression(expr);
        }
    }

    public void exitInstantiateClassExpression(InstantiateClassExpression expression) {
    }

    public void visitLambdaExpression(LambdaExpression expression) {
        // TODO visit parameter and value
    }

    public void exitLambdaExpression(LambdaExpression expression) {
    }

    public void visitPostfixedExpression(PostfixedExpression expression) {
        visitExpression(expression.getExpr());
    }

    public void exitPostfixedExpression(PostfixedExpression expression) {
    }

    public void visitParenthesisExpression(ParenthesisExpression expression) {
        visitExpression(expression.getExpression());
    }

    public void exitParenthesisExpression(ParenthesisExpression expression) {
    }

    public void visitMethodCallExpression(MethodCallExpression expression) {
        visitExpression(expression.getMethodName());

        for (Expression expr : expression.getArguments()) {
            visitExpression(expr);
        }
    }

    public void exitMethodCallExpression(MethodCallExpression expression) {
    }

    public void visitPrefixedExpression(PrefixedExpression expression) {
        visitExpression(expression.getExpr());
    }

    public void exitPrefixedExpression(PrefixedExpression expression) {
    }

    public void visitSquareBracketsExpression(SquareBracketsExpression expression) {
        expression.getExpression().ifPresent(this::visitExpression);
    }

    public void exitSquareBracketsExpression(SquareBracketsExpression expression) {
    }

    public void visitTernaryExpression(TernaryExpression expression) {
        visitExpression(expression.getCondition());
        visitExpression(expression.getValueIfTrue());
        visitExpression(expression.getValueIfFalse());
    }

    public void exitTernaryExpression(TernaryExpression expression) {
    }

    public void visitUnitExpression(UnitExpression expression) {
    }

    public void exitUnitExpression(UnitExpression expression) {
    }
}
