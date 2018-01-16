package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ForStatement implements Statement {

    /**
     * Initialization variable (empty if we have multiple initialization expressions list instead).
     */
    private final Optional<LocalVariableStatements> initVariables;
    /**
     * Multiple initialization expressions (empty if we have an initialization variable instead).
     */
    private final List<Expression> initExpressions;
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
        return new ToStringBuilder(this)
                .append("initVariables", initVariables)
                .append("initExpressions", initExpressions)
                .append("condition", condition)
                .append("updateExpressions", updateExpressions)
                .append("statement", statement)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        String initExprsStr = "";

        for (int i = 0; i < initExpressions.size(); i++) {
            initExprsStr += initExpressions.get(i).toPseudoCode();

            if (i + 1 < initExpressions.size())
                initExprsStr += ", ";
        }
        String initVars = initVariables.map(SerializableCode::toPseudoCode).orElse(initExprsStr);
        String updateStr = "";

        for (int i = 0; i < updateExpressions.size(); i++) {
            updateStr += updateExpressions.get(i).toPseudoCode();

            if (i + 1 < updateExpressions.size())
                updateStr += ", ";
        }

        String conditionStr = condition.map(SerializableCode::toPseudoCode).orElse("");
        String forHeader = String.format("%s; %s; %s", initVars, conditionStr, updateStr);

        return new StringBuilder()
                .append(StringUtils.indentation(indentation))
                .append("for (")
                .append(forHeader)
                .append(") {")
                .append(System.lineSeparator())
                .append(statement.toPseudoCode(indentation + 1))
                .append(System.lineSeparator())
                .append(StringUtils.indentation(indentation))
                .append("}")
                .toString();
    }
}
