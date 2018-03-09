package org.qmul.csar.code.java.postprocess.util;

import org.qmul.csar.code.java.parse.statement.CompilationUnitStatement;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.lang.TypeStatement;

import java.nio.file.Path;

/**
 * A resolved type instance.
 */
public final class TypeInstance {

    private final String qualifiedName;
    private final TypeStatement statement;
    private final Path path;
    private int dimensions;
    private CompilationUnitStatement compilationUnitStatement;

    public TypeInstance(String qualifiedName, TypeStatement statement,
            CompilationUnitStatement compilationUnitStatement, Path path, int dimensions) {
        this.qualifiedName = qualifiedName;
        this.statement = statement;
        this.compilationUnitStatement = compilationUnitStatement;
        this.path = path;
        this.dimensions = dimensions;
    }

    public TypeInstance(String qualifiedName, int dimensions) {
        this(qualifiedName, null, null, null, dimensions);
    }

    public TypeInstance(QualifiedType type, int dimensions) {
        this(type.getQualifiedName(), (TypeStatement)type.getStatement(), type.getTopLevelStatement(), type.getPath(),
                dimensions);
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public TypeStatement getStatement() {
        return statement;
    }

    public CompilationUnitStatement getCompilationUnitStatement() {
        return compilationUnitStatement;
    }

    public Path getPath() {
        return path;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void incrementDimension() {
        dimensions++;
    }

    public void decrementDimension() {
        dimensions--;
    }

    public String getType() {
        int dollarIdx = qualifiedName.lastIndexOf('$');
        int dotIdx = qualifiedName.lastIndexOf('.');

        if (dollarIdx == -1 && dotIdx == -1) {
            return qualifiedName;
        } else if (dollarIdx > dotIdx) {
            return qualifiedName.substring(dollarIdx + 1);
        } else {
            return qualifiedName.substring(dotIdx + 1);
        }
    }
}
