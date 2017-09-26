package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ForStatement implements Statement {

    private final Optional<LocalVariableStatements> initVariables;
    private final List<Expression> initExpressions; // TODO document what this is
    private final Optional<Expression> condition;
    private final List<Expression> updateExpressions;
    private final Statement statement;

    public ForStatement(Optional<LocalVariableStatements> initVariables, List<Expression> initExpressions,
            Optional<Expression> condition, List<Expression> updateExpressions, Statement statement) {
        this.initVariables = initVariables;
        this.initExpressions = Collections.unmodifiableList(initExpressions);
        this.condition = condition;
        this.updateExpressions = Collections.unmodifiableList(updateExpressions);
        this.statement = statement;
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

    public List<Expression> getUpdateExpressions() {
        return updateExpressions;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForStatement that = (ForStatement) o;
        return Objects.equals(initVariables, that.initVariables)
                && Objects.equals(initExpressions, that.initExpressions)
                && Objects.equals(condition, that.condition)
                && Objects.equals(updateExpressions, that.updateExpressions)
                && Objects.equals(statement, that.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initVariables, initExpressions, condition, updateExpressions, statement);
    }

    @Override
    public String toString() {
        return String.format(
                "ForStatement{initVariables=%s, initExpressions=%s, condition=%s, updateExpressions=%s, statement=%s}",
                initVariables, initExpressions, condition, updateExpressions, statement);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return "for"; // TODO write
    }
}
