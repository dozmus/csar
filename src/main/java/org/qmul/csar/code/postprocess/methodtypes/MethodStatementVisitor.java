package org.qmul.csar.code.postprocess.methodtypes;

import org.qmul.csar.code.parse.java.statement.ImportStatement;
import org.qmul.csar.code.parse.java.statement.MethodStatement;
import org.qmul.csar.code.parse.java.statement.PackageStatement;
import org.qmul.csar.code.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.StatementVisitor;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.MethodDescriptor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MethodStatementVisitor extends StatementVisitor {

    private final QualifiedNameResolver qualifiedNameResolver;
    private Path path;
    private Map<Path, Statement> code;
    private TypeStatement topLevelParent;
    private List<ImportStatement> imports;
    private Optional<PackageStatement> currentPackage;
    private TypeStatement parent;

    public MethodStatementVisitor(QualifiedNameResolver qualifiedNameResolver) {
        this.qualifiedNameResolver = qualifiedNameResolver;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setCode(Map<Path, Statement> code) {
        this.code = code;
    }

    public void setTopLevelParent(TypeStatement topLevelParent) {
        this.topLevelParent = topLevelParent;
    }

    public void setPackage(Optional<PackageStatement> currentPackage) {
        this.currentPackage = currentPackage;
    }

    public void setImports(List<ImportStatement> imports) {
        this.imports = imports;
    }

    public void setParent(TypeStatement parent) {
        this.parent = parent;
    }

    @Override
    public void visitMethodStatement(MethodStatement statement) {
        // Resolve return type
        resolveReturnType(statement);

        // Resolve parameter types
        resolveParameterTypes(statement);
    }

    private void resolveReturnType(MethodStatement statement) {
        MethodDescriptor desc = statement.getDescriptor();
        String returnType = desc.getReturnType().get();

        // Resolve
        QualifiedType type = qualifiedNameResolver.resolve(code, path, parent, topLevelParent, currentPackage, imports,
                returnType);
        statement.setReturnQualifiedType(type);

        // TODO test
    }

    private void resolveParameterTypes(MethodStatement statement) {
        MethodDescriptor desc = statement.getDescriptor();

        // TODO impl
    }
}
