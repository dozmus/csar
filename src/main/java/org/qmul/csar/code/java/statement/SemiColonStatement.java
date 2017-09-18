package org.qmul.csar.code.java.statement;

import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

public class SemiColonStatement implements Statement {

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation) + ";";
    }
}
