package org.qmul.csar.code.java.postprocess.qualifiedname;

import org.qmul.csar.code.java.parse.statement.CompilationUnitStatement;
import org.qmul.csar.lang.Statement;

import java.nio.file.Path;

/**
 * A resolved qualified type.
 */
public final class QualifiedType {

    private final String qualifiedName;
    private final Statement statement;
    private final CompilationUnitStatement topLevelStatement;
    private final Path path;

    public QualifiedType(String qualifiedName) {
        this(qualifiedName, null, null, null);
    }

    public QualifiedType(String qualifiedName, Statement statement, CompilationUnitStatement topLevelStatement,
            Path path) {
        this.qualifiedName = qualifiedName;
        this.statement = statement;
        this.topLevelStatement = topLevelStatement;
        this.path = path;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public Statement getStatement() {
        return statement;
    }

    public CompilationUnitStatement getTopLevelStatement() {
        return topLevelStatement;
    }

    public Path getPath() {
        return path;
    }
}
