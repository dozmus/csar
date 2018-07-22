package org.qmul.csar.code.java.postprocess.methods.overridden;

import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

final class MethodStatementVisitor extends StatementVisitor {

    private final Map<Path, Statement> code;
    private final OverriddenMethodsResolver omr;
    private final Deque<String> traversalHierarchy = new ArrayDeque<>();
    private final Deque<TypeStatement> traversedTypeStatements = new ArrayDeque<>();
    private final Predicate<MethodStatement> methodFilter;
    private Path path;
    private Optional<PackageStatement> packageStatement;
    private List<ImportStatement> imports;

    public MethodStatementVisitor(Map<Path, Statement> code, OverriddenMethodsResolver omr,
            Predicate<MethodStatement> methodFilter) {
        this.omr = omr;
        this.code = code;
        this.methodFilter = methodFilter;
    }

    public void reset() {
        traversalHierarchy.clear();
        traversedTypeStatements.clear();
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

        if (methodFilter.test(statement))
            mapOverridden(statement);
        super.visitMethodStatement(statement);
        traversalHierarchy.removeLast();
    }

    private void mapOverridden(MethodStatement method) {
        if (omr.calculateOverridden(code, path, packageStatement, imports, traversedTypeStatements.getLast(),
                traversedTypeStatements.getFirst(), method)) {
            method.getDescriptor().setOverridden(Optional.of(true));
            omr.setOverridden(createSignature(), true);
        } else {
            method.getDescriptor().setOverridden(Optional.of(false));
        }
    }

    private String prefix() {
        if (traversalHierarchy.size() == 0)
            return "";
        return (traversedTypeStatements.size() == 1) ? "." : "$";
    }

    /**
     * Returns the signature of the current statement in the traversal hierarchy.
     */
    private String createSignature() {
        return String.join("", traversalHierarchy);
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setCompilationUnitStatement(CompilationUnitStatement topLevelParent) {
        traversedTypeStatements.clear();
        traversalHierarchy.clear();
        traversedTypeStatements.addLast(topLevelParent);
        packageStatement = topLevelParent.getPackageStatement();
        imports = topLevelParent.getImports();

        if (topLevelParent.getPackageStatement().isPresent()) {
            PackageStatement pkg = topLevelParent.getPackageStatement().get();
            String[] pkgParts = pkg.getPackageName().split("\\.");

            for (int i = 0; i < pkgParts.length; i++) {
                String s = pkgParts[i];

                if (i > 0)
                    s = "." + s;
                traversalHierarchy.addLast(s);
            }
        }
    }
}
