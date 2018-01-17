package org.qmul.csar.code.java.postprocess.qualifiedname;

import org.qmul.csar.lang.Statement;

import java.nio.file.Path;

/**
 * A resolved qualified type.
 */
public final class QualifiedType {

    private final String qualifiedName;
    private final Statement statement;
    private final Path path;

    public QualifiedType(String qualifiedName, Statement statement, Path path) {
        this.qualifiedName = qualifiedName;
        this.statement = statement;
        this.path = path;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public Statement getStatement() {
        return statement;
    }

    public Path getPath() {
        return path;
    }
}
