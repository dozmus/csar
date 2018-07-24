package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CompilationUnitStatement implements TypeStatement {

    private Optional<PackageStatement> packageStatement;
    private List<ImportStatement> imports;
    private TypeStatement typeStatement;

    public CompilationUnitStatement() {
    }

    public CompilationUnitStatement(Optional<PackageStatement> packageStatement, List<ImportStatement> imports,
                                    TypeStatement typeStatement) {
        this.packageStatement = packageStatement;
        this.imports = imports;
        this.typeStatement = typeStatement;
    }

    public Optional<PackageStatement> getPackageStatement() {
        return packageStatement;
    }

    public List<ImportStatement> getImports() {
        return imports;
    }

    public TypeStatement getTypeStatement() {
        return typeStatement;
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder builder = new StringBuilder();
        packageStatement.ifPresent(p -> builder.append(StringUtils.indentation(indentation)).append(p.toPseudoCode())
                .append(System.lineSeparator()).append(System.lineSeparator()));
        imports.forEach(i -> builder.append(StringUtils.indentation(indentation)).append(i.toPseudoCode())
                .append(System.lineSeparator()));

        if (imports.size() > 0) {
            builder.append(System.lineSeparator());
        }
        return builder.append(typeStatement.toPseudoCode()).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompilationUnitStatement that = (CompilationUnitStatement) o;
        return Objects.equals(packageStatement, that.packageStatement)
                && Objects.equals(imports, that.imports)
                && Objects.equals(typeStatement, that.typeStatement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageStatement, imports, typeStatement);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("packageStatement", packageStatement)
                .append("imports", imports)
                .append("typeStatement", typeStatement)
                .toString();
    }
}
