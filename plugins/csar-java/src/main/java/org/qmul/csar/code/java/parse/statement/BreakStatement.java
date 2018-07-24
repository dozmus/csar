package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;
import java.util.Optional;

/**
 * A break statement, with an optional identifier.
 */
public class BreakStatement implements Statement {

    private Optional<String> identifier;

    public BreakStatement() {
    }

    public BreakStatement(Optional<String> identifier) {
        this.identifier = identifier;
    }

    public Optional<String> getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BreakStatement that = (BreakStatement) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("identifier", identifier)
                .toString();
    }

    @Override
    public String toPseudoCode(int indentation) {
        return identifier.map(s -> String.format("%sbreak %s;", StringUtils.indentation(indentation), s))
                .orElseGet(() -> StringUtils.indentation(indentation) + "break;");
    }
}
