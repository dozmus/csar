package org.qmul.csar.lang;

import org.qmul.csar.code.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface Expression extends Statement {

    // TODO finish

    enum BinaryOperation {
        MUL("*"), DIV("/"), MOD("%"), ADD("+"), SUB("-"),
        LE("<="), GE(">="), GT(">"), LT("<"),
        INSTANCE_OF("instanceof"),
        EQUAL("=="), NOTEQUAL("!="),
        BITAND("&"), BITOR("|"), BITXOR("^"),
        AND("&&"), OR("||"),
        ASSIGN("="),
        ADD_ASSIGN("+="), SUB_ASSIGN("-="), MUL_ASSIGN("*="), DIV_ASSIGN("/="), AND_ASSIGN("&="), OR_ASSIGN("|="),
        XOR_ASSIGN("^="), RSHIFT_ASSIGN(">>="), LSHIFT_ASSIGN("<<="), MOD_ASSIGN("%="),
        LSHIFT("<<"), RSHIFT(">>"), UNSIGNED_RSHIFT(">>>"),
        DOT("."),
        QUESTION("?");

        private final String symbol;

        BinaryOperation(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public static BinaryOperation forSymbol(String symbol) {
            for (BinaryOperation op : values()) {
                if (op.getSymbol().equals(symbol)) {
                    return op;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    enum Prefix {
        ADD("+"), SUB("-"), INC("++"), DEC("--"), TILDE("~"), BANG("!");

        private final String symbol;

        Prefix(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public static Prefix forSymbol(String symbol) {
            for (Prefix op : values()) {
                if (op.getSymbol().equals(symbol)) {
                    return op;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    enum Postfix {
        INC("++"), DEC("--");

        private final String symbol;

        Postfix(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public static Postfix forSymbol(String symbol) {
            for (Postfix op : values()) {
                if (op.getSymbol().equals(symbol)) {
                    return op;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    class ArrayExpression implements Expression {

        private List<Expression> expressions = new ArrayList<>();

        public ArrayExpression(List<Expression> expressions) {
            this.expressions = expressions;
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
            return String.format("ArrayExpression{expressions=%s}", expressions);
        }
    }

    class SemiColonTerminatedExpression implements Expression {

        private final Optional<Expression> expression;

        public SemiColonTerminatedExpression(Optional<Expression> expression) {
            this.expression = expression;
        }

        public Optional<Expression> getExpression() {
            return expression;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SemiColonTerminatedExpression that = (SemiColonTerminatedExpression) o;
            return Objects.equals(expression, that.expression);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expression);
        }

        @Override
        public String toString() {
            return String.format("SemiColonTerminatedExpression{expression=%s}", expression);
        }
    }

    class SemiColonExpression implements Expression {

    }

    class UnitExpression implements Expression {

        private final Type type;
        private final String value;

        public UnitExpression(Type type, String value) {
            this.type = type;
            this.value = value;
        }

        public Type getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UnitExpression that = (UnitExpression) o;
            return type == that.type && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, value);
        }

        @Override
        public String toString() {
            return String.format("UnitExpression{type=%s, value='%s'}", type, value);
        }

        public enum Type {
            LITERAL, IDENTIFIER, CLASS_REFERENCE, METHOD_REFERENCE, SUPER, THIS, THIS_CALL, SUPER_CALL, TYPE, NEW,
            METHOD_CALL
        }
    }

    class BinaryExpression implements Expression {

        private final Expression left;
        private final BinaryOperation op;
        private final Expression right;

        public BinaryExpression(Expression left, BinaryOperation op, Expression right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        public Expression getLeft() {
            return left;
        }

        public BinaryOperation getOp() {
            return op;
        }

        public Expression getRight() {
            return right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BinaryExpression that = (BinaryExpression) o;
            return Objects.equals(left, that.left) && Objects.equals(op, that.op) && Objects.equals(right, that.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, op, right);
        }

        @Override
        public String toString() {
            return String.format("BinaryExpression{left=%s, op=%s, right=%s}", left, op, right);
        }
    }

    class PrefixedExpression implements Expression {

        private final Expression expr;
        private final Prefix prefix;

        public PrefixedExpression(Expression expr, Prefix prefix) {
            this.expr = expr;
            this.prefix = prefix;
        }

        public Expression getExpr() {
            return expr;
        }

        public Prefix getPrefix() {
            return prefix;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PrefixedExpression that = (PrefixedExpression) o;
            return Objects.equals(expr, that.expr) && prefix == that.prefix;
        }

        @Override
        public int hashCode() {
            return Objects.hash(expr, prefix);
        }

        @Override
        public String toString() {
            return String.format("PrefixedExpression{expr=%s, prefix=%s}", expr, prefix);
        }
    }

    class PostfixedExpression implements Expression {

        private final Expression expr;
        private final Postfix postfix;

        public PostfixedExpression(Expression expr, Postfix postfix) {
            this.expr = expr;
            this.postfix = postfix;
        }

        public Expression getExpr() {
            return expr;
        }

        public Postfix getPostfix() {
            return postfix;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PostfixedExpression that = (PostfixedExpression) o;
            return Objects.equals(expr, that.expr) && postfix == that.postfix;
        }

        @Override
        public int hashCode() {
            return Objects.hash(expr, postfix);
        }

        @Override
        public String toString() {
            return String.format("PostfixedExpression{expr=%s, postfix=%s}", expr, postfix);
        }
    }

    class MethodCallExpression implements Expression {

        private final Expression methodName;
        private final List<Expression> arguments;

        public MethodCallExpression(Expression methodName, List<Expression> arguments) {
            this.methodName = methodName;
            this.arguments = arguments;
        }

        public Expression getMethodName() {
            return methodName;
        }

        public List<Expression> getArguments() {
            return arguments;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodCallExpression that = (MethodCallExpression) o;
            return Objects.equals(methodName, that.methodName) && Objects.equals(arguments, that.arguments);
        }

        @Override
        public int hashCode() {
            return Objects.hash(methodName, arguments);
        }

        @Override
        public String toString() {
            return String.format("MethodCallExpression{methodName=%s, arguments=%s}", methodName, arguments);
        }
    }

    class InstantiateClass implements Expression {

        private final List<Expression> arguments;
        private final Node classDefinition;
        private final List<String> typeArguments;
        private final boolean hasTypeArguments;

        public InstantiateClass(List<Expression> arguments, Node classDefinition,
                List<String> typeArguments, boolean hasTypeArguments) {
            this.arguments = arguments;
            this.classDefinition = classDefinition;
            this.typeArguments = typeArguments;
            this.hasTypeArguments = hasTypeArguments;
        }

        public List<Expression> getArguments() {
            return arguments;
        }

        public Node getClassDefinition() {
            return classDefinition;
        }

        public List<String> getTypeArguments() {
            return typeArguments;
        }

        public boolean isHasTypeArguments() {
            return hasTypeArguments;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InstantiateClass that = (InstantiateClass) o;
            return hasTypeArguments == that.hasTypeArguments &&
                    Objects.equals(arguments, that.arguments) &&
                    Objects.equals(classDefinition, that.classDefinition) &&
                    Objects.equals(typeArguments, that.typeArguments);
        }

        @Override
        public int hashCode() {
            return Objects.hash(arguments, classDefinition, typeArguments, hasTypeArguments);
        }

        @Override
        public String toString() {
            return String.format(
                    "InstantiateClass{arguments=%s, classDefinition=%s, typeArguments=%s, hasTypeArguments=%s}",
                    arguments, classDefinition, typeArguments, hasTypeArguments);
        }
    }

    class ArrayDefinitionExpression implements Expression {

        private final List<Expression> expr;

        public ArrayDefinitionExpression(List<Expression> expr) {
            this.expr = expr;
        }

        public List<Expression> getExpr() {
            return expr;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArrayDefinitionExpression that = (ArrayDefinitionExpression) o;
            return Objects.equals(expr, that.expr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expr);
        }

        @Override
        public String toString() {
            return String.format("ArrayDefinitionExpression{expr=%s}", expr);
        }
    }

    class SquareBracketsExpression implements Expression { // [] or [$expr]

        private final Optional<Expression> expression;

        public SquareBracketsExpression(Optional<Expression> expression) {
            this.expression = expression;
        }

        public Optional<Expression> getExpression() {
            return expression;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SquareBracketsExpression that = (SquareBracketsExpression) o;
            return Objects.equals(expression, that.expression);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expression);
        }

        @Override
        public String toString() {
            return String.format("SquareBracketsExpression{expression=%s}", expression);
        }
    }

    class ParenthesisExpression implements Expression {

        private final Expression expression;

        public ParenthesisExpression(Expression expression) {
            this.expression = expression;
        }

        public Expression getExpression() {
            return expression;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParenthesisExpression that = (ParenthesisExpression) o;
            return Objects.equals(expression, that.expression);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expression);
        }

        @Override
        public String toString() {
            return String.format("ParenthesisExpression{expression=%s}", expression);
        }
    }

    class CastExpression implements Expression {

        private final String apparentType;
        private final Expression expression;

        public CastExpression(String apparentType, Expression expression) {
            this.apparentType = apparentType;
            this.expression = expression;
        }

        public String getApparentType() {
            return apparentType;
        }

        public Expression getExpression() {
            return expression;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CastExpression that = (CastExpression) o;
            return Objects.equals(apparentType, that.apparentType) && Objects.equals(expression, that.expression);
        }

        @Override
        public int hashCode() {
            return Objects.hash(apparentType, expression);
        }

        @Override
        public String toString() {
            return String.format("CastExpression{apparentType='%s', expression=%s}", apparentType, expression);
        }
    }

    class LambdaExpression implements Expression { // TODO clean up

        private final Object parameter;
        private final Object value;

        public LambdaExpression(Object parameter, Object value) {
            this.parameter = parameter;
            this.value = value;
        }

        public Object getParameter() {
            return parameter;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LambdaExpression that = (LambdaExpression) o;
            return Objects.equals(parameter, that.parameter) && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parameter, value);
        }

        @Override
        public String toString() {
            return String.format("LambdaExpression{parameter=%s, value=%s}", parameter, value);
        }
    }

    class ArrayAccessExpression implements Expression {

        private final Expression array;
        private final Expression index;

        public ArrayAccessExpression(Expression array, Expression index) {
            this.array = array;
            this.index = index;
        }

        public Expression getArray() {
            return array;
        }

        public Expression getIndex() {
            return index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArrayAccessExpression that = (ArrayAccessExpression) o;
            return Objects.equals(array, that.array) && Objects.equals(index, that.index);
        }

        @Override
        public int hashCode() {
            return Objects.hash(array, index);
        }

        @Override
        public String toString() {
            return String.format("ArrayAccessExpression{array=%s, index=%s}", array, index);
        }
    }

    class TernaryExpression implements Expression {

        private final Expression condition;
        private final Expression valueIfTrue;
        private final Expression valueIfFalse;

        public TernaryExpression(Expression condition, Expression valueIfTrue, Expression valueIfFalse) {
            this.condition = condition;
            this.valueIfTrue = valueIfTrue;
            this.valueIfFalse = valueIfFalse;
        }

        public Expression getCondition() {
            return condition;
        }

        public Expression getValueIfTrue() {
            return valueIfTrue;
        }

        public Expression getValueIfFalse() {
            return valueIfFalse;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TernaryExpression that = (TernaryExpression) o;
            return Objects.equals(condition, that.condition) && Objects.equals(valueIfTrue, that.valueIfTrue)
                    && Objects.equals(valueIfFalse, that.valueIfFalse);
        }

        @Override
        public int hashCode() {
            return Objects.hash(condition, valueIfTrue, valueIfFalse);
        }

        @Override
        public String toString() {
            return String.format("TernaryExpression{condition=%s, valueIfTrue=%s, valueIfFalse=%s}", condition,
                    valueIfTrue, valueIfFalse);
        }
    }
}
