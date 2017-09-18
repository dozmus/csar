package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ForStatement implements Statement {

    private final Optional<LocalVariableStatements> initVariables;
    private final List<Expression> initExpressions;
    private final Optional<Expression> condition;
    private final List<Expression> exprList;

    public ForStatement(Optional<LocalVariableStatements> initVariables,
            List<Expression> initExpressions, Optional<Expression> condition, List<Expression> exprList) {
        this.initVariables = initVariables;
        this.initExpressions = Collections.unmodifiableList(initExpressions);
        this.condition = condition;
        this.exprList = Collections.unmodifiableList(exprList);
    }

    public Optional<LocalVariableStatements> getInitVariables() {
        return initVariables;
    }

    public List<Expression> getInitExpressions() {
        return initExpressions;
    }

    public Optional<Expression> getCondition() {
        return condition;
    }

    public List<Expression> getExprList() {
        return exprList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForStatement that = (ForStatement) o;
        return Objects.equals(initVariables, that.initVariables)
                && Objects.equals(initExpressions, that.initExpressions)
                && Objects.equals(condition, that.condition)
                && Objects.equals(exprList, that.exprList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initVariables, initExpressions, condition, exprList);
    }

    @Override
    public String toString() {
        return String.format("ForStatement{initVariables=%s, initExpressions=%s, condition=%s, exprList=%s} %s",
                initVariables, initExpressions, condition, exprList, super.toString());
    }

    @Override
    public String toPseudoCode(int indentation) {
        return "for"; // TODO write
    }
}
