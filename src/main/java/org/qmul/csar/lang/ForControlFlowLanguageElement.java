package org.qmul.csar.lang;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ForControlFlowLanguageElement extends ControlFlowLanguageElement {

    private final Optional<VariableLanguageElement.VariableLanguageElements> initVariables;
    private final List<Expression> initExpressions;
    private final Optional<Expression> condition;
    private final List<Expression> exprList;

    public ForControlFlowLanguageElement(Optional<VariableLanguageElement.VariableLanguageElements> initVariables,
            List<Expression> initExpressions, Optional<Expression> condition, List<Expression> exprList) {
        super(ControlFlowType.FOR);
        this.initVariables = initVariables;
        this.initExpressions = initExpressions;
        this.condition = condition;
        this.exprList = exprList;
    }

    public Optional<VariableLanguageElement.VariableLanguageElements> getInitVariables() {
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
        if (!super.equals(o)) return false;
        ForControlFlowLanguageElement that = (ForControlFlowLanguageElement) o;
        return Objects.equals(initVariables, that.initVariables) &&
                Objects.equals(initExpressions, that.initExpressions) &&
                Objects.equals(condition, that.condition) &&
                Objects.equals(exprList, that.exprList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), initVariables, initExpressions, condition, exprList);
    }

    @Override
    public String toString() {
        return String.format(
                "ForControlFlowLanguageElement{initVariables=%s, initExpressions=%s, condition=%s, exprList=%s} %s",
                initVariables, initExpressions, condition, exprList, super.toString());
    }
}
