package org.qmul.csar.code.java.parse.statement;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

/**
 * A semi-colon statement.
 */
public class SemiColonStatement implements Statement {

    @Override
    public String toString() {
        return "SemiColonStatement";
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation) + ";";
    }
}
