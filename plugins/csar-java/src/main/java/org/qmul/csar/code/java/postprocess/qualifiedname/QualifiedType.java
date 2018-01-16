package org.qmul.csar.code.java.postprocess.qualifiedname;

import org.qmul.csar.lang.Statement;

/**
 * A resolved qualified type.
 */
public final class QualifiedType {

    private final String qualifiedName;
    private final Statement statement;

    public QualifiedType(String qualifiedName, Statement statement) {
        this.qualifiedName = qualifiedName;
        this.statement = statement;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public Statement getStatement() {
        return statement;
    }
}
