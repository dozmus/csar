package org.qmul.csar.lang;

import org.qmul.csar.code.parse.java.expression.*;

public class ExpressionVisitor {

    public void visitExpression(Expression expression) {
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

    private void visitArrayAccessExpression(ArrayAccessExpression expression) {
    }

    private void exitArrayAccessExpression(ArrayAccessExpression expression) {
    }

    private void visitArrayExpression(ArrayExpression expression) {
    }

    private void exitArrayExpression(ArrayExpression expression) {
    }

    private void visitArrayInitializationExpression(ArrayInitializationExpression expression) {
    }

    private void exitArrayInitializationExpression(ArrayInitializationExpression expression) {
    }

    private void visitBinaryExpression(BinaryExpression expression) {
    }

    private void exitBinaryExpression(BinaryExpression expression) {
    }

    private void visitCastExpression(CastExpression expression) {
    }

    private void exitCastExpression(CastExpression expression) {
    }

    private void visitInstantiateClassExpression(InstantiateClassExpression expression) {
    }

    private void exitInstantiateClassExpression(InstantiateClassExpression expression) {
    }

    private void visitLambdaExpression(LambdaExpression expression) {
    }

    private void exitLambdaExpression(LambdaExpression expression) {
    }

    private void visitPostfixedExpression(PostfixedExpression expression) {
    }

    private void exitPostfixedExpression(PostfixedExpression expression) {
    }

    private void visitParenthesisExpression(ParenthesisExpression expression) {
    }

    private void exitParenthesisExpression(ParenthesisExpression expression) {
    }

    private void visitMethodCallExpression(MethodCallExpression expression) {
    }

    private void exitMethodCallExpression(MethodCallExpression expression) {
    }

    private void visitPrefixedExpression(PrefixedExpression expression) {
    }

    private void exitPrefixedExpression(PrefixedExpression expression) {
    }

    private void visitSquareBracketsExpression(SquareBracketsExpression expression) {
    }

    private void exitSquareBracketsExpression(SquareBracketsExpression expression) {
    }

    private void visitTernaryExpression(TernaryExpression expression) {
    }

    private void exitTernaryExpression(TernaryExpression expression) {
    }

    private void visitUnitExpression(UnitExpression expression) {
    }

    private void exitUnitExpression(UnitExpression expression) {
    }
}
