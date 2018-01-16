package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;

import java.util.Objects;

public class ImportStatement implements Statement {

    private final String qualifiedName;
    private final boolean staticImport;

    public ImportStatement(String qualifiedName, boolean staticImport) {
        this.qualifiedName = qualifiedName;
        this.staticImport = staticImport;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public boolean isStaticImport() {
        return staticImport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportStatement that = (ImportStatement) o;
        return staticImport == that.staticImport && Objects.equals(qualifiedName, that.qualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, staticImport);
    }

    @Override
    public String toPseudoCode(int indentation) {
        return StringUtils.indentation(indentation) + "import " + (staticImport ? "static " : "") + qualifiedName + ";";
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("qualifiedName", qualifiedName)
                .append("staticImport", staticImport)
                .toString();
    }
}
