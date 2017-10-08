package org.qmul.csar.code.parse.java.statement;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * A break statement, with an optional identifier.
 */
public class BreakStatement implements Statement {

    private final Optional<String> identifier;

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
        return String.format("BreakStatement{identifier=%s}", identifier);
    }

    @Override
    public String toPseudoCode(int indentation) {
        if (identifier.isPresent()) {
            return String.format("%sbreak %s;", StringUtils.indentation(indentation), identifier.get());
        }
        return StringUtils.indentation(indentation) + "break;";
    }
}
