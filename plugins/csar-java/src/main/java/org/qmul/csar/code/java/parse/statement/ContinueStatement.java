package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.Objects;
import java.util.Optional;

/**
 * A continue statement, with an optional identifier.
 */
public class ContinueStatement implements Statement {

    private Optional<String> identifier;

    public ContinueStatement() {
    }

    public ContinueStatement(Optional<String> identifier) {
        this.identifier = identifier;
    }

    public Optional<String> getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContinueStatement that = (ContinueStatement) o;
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
        if (identifier.isPresent()) {
            return String.format("%scontinue %s;", StringUtils.indentation(indentation), identifier);
        }
        return  StringUtils.indentation(indentation) + "continue;";
    }
}
