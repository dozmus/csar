package org.qmul.csar.code.java.postprocess.methods.overridden;

import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;

import java.nio.file.Path;
import java.util.*;

final class MethodStatementVisitor extends StatementVisitor {

    private final Map<Path, Statement> code;
    private final Deque<String> traversalHierarchy = new ArrayDeque<>();
    private final Deque<TypeStatement> traversedTypeStatements = new ArrayDeque<>();
    private final OverriddenMethodsResolver overriddenMethodsResolver;
    private Path path;
    private Optional<PackageStatement> packageStatement;
    private List<ImportStatement> imports;

    public MethodStatementVisitor(OverriddenMethodsResolver overriddenMethodsResolver, Map<Path, Statement> code) {
        this.overriddenMethodsResolver = overriddenMethodsResolver;
        this.code = code;
    }

    @Override
    public void visitEnumStatement(EnumStatement statement) {
        traversalHierarchy.addLast(prefix() + statement.getDescriptor().getIdentifierName().toString());
        traversedTypeStatements.addLast(statement);
        super.visitEnumStatement(statement);
    }

    @Override
    public void exitEnumStatement(EnumStatement statement) {
        traversalHierarchy.removeLast();
        traversedTypeStatements.removeLast();
    }

    @Override
    public void visitClassStatement(ClassStatement statement) {
        traversalHierarchy.addLast(prefix() + statement.getDescriptor().getIdentifierName().toString());
        traversedTypeStatements.addLast(statement);
        super.visitClassStatement(statement);
    }

    @Override
    public void exitClassStatement(ClassStatement statement) {
        traversalHierarchy.removeLast();
        traversedTypeStatements.removeLast();
    }

    @Override
    public void visitMethodStatement(MethodStatement statement) {
        traversalHierarchy.addLast("#" + statement.getDescriptor().signature());
        mapOverridden(statement);
        super.visitMethodStatement(statement);
        traversalHierarchy.removeLast();
    }

    private void mapOverridden(MethodStatement method) {
        if (overriddenMethodsResolver.calculateOverridden(code, path, packageStatement, imports,
                traversedTypeStatements.getLast(), traversedTypeStatements.getFirst(), method)) {
            method.getDescriptor().setOverridden(Optional.of(true));
            overriddenMethodsResolver.getMap().put(createSignature(), true);
        } else {
            method.getDescriptor().setOverridden(Optional.of(false));
        }
    }

    private String prefix() {
        if (traversalHierarchy.size() == 0)
            return "";
        return (traversedTypeStatements.size() == 1) ? "." : "$";
    }

    private String createSignature() {
        return String.join("", traversalHierarchy);
    }

    public void setTopLevelTypeStatement(CompilationUnitStatement compilationUnitStatement) {
        traversedTypeStatements.clear();
        traversalHierarchy.clear();
        traversedTypeStatements.addLast(compilationUnitStatement);
        packageStatement = compilationUnitStatement.getPackageStatement();
        imports = compilationUnitStatement.getImports();

        if (compilationUnitStatement.getPackageStatement().isPresent()) {
            PackageStatement pkg = compilationUnitStatement.getPackageStatement().get();
            String[] pkgParts = pkg.getPackageName().split("\\.");

            for (int i = 0; i < pkgParts.length; i++) {
                String s = pkgParts[i];

                if (i > 0)
                    s = "." + s;
                traversalHierarchy.addLast(s);
            }
        }
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
