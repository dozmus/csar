package org.qmul.csar.code.java.parse.statement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.util.StringUtils;
import org.qmul.csar.util.ToStringStyles;

import java.util.List;
import java.util.Objects;

public class PackageStatement implements Statement {

    private String packageName;
    private List<Annotation> annotations;

    public PackageStatement() {
    }

    public PackageStatement(String packageName, List<Annotation> annotations) {
        this.packageName = packageName;
        this.annotations = annotations;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageStatement that = (PackageStatement) o;
        return Objects.equals(packageName, that.packageName) && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, annotations);
    }

    @Override
    public String toPseudoCode(int indentation) {
        StringBuilder builder = new StringBuilder();

        if (getAnnotations().size() > 0) {
            getAnnotations().forEach(annotation -> builder.append(annotation.toPseudoCode(indentation))
                    .append(System.lineSeparator()));
        }
        builder.append(StringUtils.indentation(indentation));
        builder.append("package ").append(packageName).append(";");
        return builder.toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyles.SHORT_DEFAULT_STYLE)
                .append("packageName", packageName)
                .append("annotations", annotations)
                .toString();
    }
}
