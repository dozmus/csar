package org.qmul.csar.code.postprocess;

import org.qmul.csar.code.parse.java.statement.*;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.StatementVisitor;
import org.qmul.csar.lang.TypeStatement;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QualifiedNameResolver {

    /**
     * The target type searcher to use.
     */
    private final TargetTypeSearcher searcher = new TargetTypeSearcher();

    public QualifiedType resolve(Map<Path, Statement> code, Path path, Optional<PackageStatement> currentPackage,
            List<ImportStatement> imports, String name) {
        // Resolve against classes in same package
        QualifiedType t1 = resolveInCurrentPackage(code, currentPackage, name);

        if (t1 != null)
            return t1;

        // Resolve against imports
        QualifiedType t2 = resolveInOtherPackages(code, imports, name);

        if (t2 != null)
            return t2;

        // Resolve against default package
        QualifiedType t3 = resolveInDefaultPackage(code, path, currentPackage, name);

        if (t3 != null)
            return t3;

        // If name contains dots, we assume it is a fully qualified name
        // TODO check this properly
        if (name.contains(".")) {
            return new QualifiedType(name, null);
        }
        throw new RuntimeException("could not resolve qualified name for " + name);
    }

    private QualifiedType resolveInOtherPackages(Map<Path, Statement> code, List<ImportStatement> imports, String name) {
        for (ImportStatement importStatement : imports) {
            if (importStatement.isStaticImport())
                continue;
            String importQualifiedName = importStatement.getQualifiedName();

            String currentPkg = "";

            if (importQualifiedName.endsWith(".*")) { // wildcard import
                currentPkg = importQualifiedName.substring(0, importQualifiedName.length() - 2);
            } else if (importQualifiedName.endsWith("." + name)) { // specific import
                currentPkg = importQualifiedName.substring(0, importQualifiedName.lastIndexOf("." + name));
            }

            for (Map.Entry<Path, Statement> entry : code.entrySet()) {
                Statement statement = entry.getValue();

                if (!(statement instanceof TopLevelTypeStatement))
                    continue;
                TopLevelTypeStatement topStatement = (TopLevelTypeStatement) statement;
                TypeStatement typeStatement = topStatement.getTypeStatement();

                if (topStatement.getTypeStatement() instanceof AnnotationStatement)
                    continue;
                String otherPkg = topStatement.getPackageStatement()
                        .map(PackageStatement::getPackageName).orElse("");

                if (targetContainsName(currentPkg, otherPkg, typeStatement, name)) {
                    String qualifiedName = otherPkg + "." + String.join("$", name.split("\\."));
                    return new QualifiedType(qualifiedName, statement);
                }
            }

            // Fall-back, assume it exists (for java api sake)
            if (importQualifiedName.endsWith("." + name)) {
                return new QualifiedType(importQualifiedName, null);
            }
        }
        return null;
    }

    private QualifiedType resolveInCurrentPackage(Map<Path, Statement> code, Optional<PackageStatement> currentPackage,
            String name) {
        if (!currentPackage.isPresent())
            return null;
        String currentPkg = currentPackage.get().getPackageName();

        for (Map.Entry<Path, Statement> entry : code.entrySet()) {
            Statement statement = entry.getValue();

            if (!(statement instanceof TopLevelTypeStatement))
                continue;
            TopLevelTypeStatement topStatement = (TopLevelTypeStatement) statement;
            TypeStatement typeStatement = topStatement.getTypeStatement();

            if (topStatement.getPackageStatement().isPresent()) {
                String otherPkg = topStatement.getPackageStatement().get().getPackageName();

                if (targetContainsName(currentPkg, otherPkg, typeStatement, name)) {
                    String qualifiedName = otherPkg + "." + String.join("$", name.split("\\."));
                    return new QualifiedType(qualifiedName, statement);
                }
            }
        }
        return null;
    }

    private QualifiedType resolveInDefaultPackage(Map<Path, Statement> code, Path path,
            Optional<PackageStatement> currentPackage, String name) {
        if (!currentPackage.isPresent()) {
            for (Map.Entry<Path, Statement> entry : code.entrySet()) {
                Statement statement = entry.getValue();

                if (!(statement instanceof TopLevelTypeStatement))
                    continue;
                TopLevelTypeStatement topStatement = (TopLevelTypeStatement) statement;
                TypeStatement typeStatement = topStatement.getTypeStatement();

                // they have to both have no package statement, and be in the same folder
                if (!topStatement.getPackageStatement().isPresent()
                        && path.getParent().equals(entry.getKey().getParent())) {
                    // Perform search in target
                    searcher.resetState(name);
                    searcher.visit(typeStatement);

                    if (searcher.isMatched()) {
                        String qualifiedName = String.join("$", name.split("\\."));
                        return new QualifiedType(qualifiedName, statement);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns <tt>true</tt> if the target type statement contains the given qualified name. If <tt>currentPkg</tt>
     * and <tt>otherPkg</tt> are not equal, then <tt>false</tt> is returned.
     *
     * @param currentPkg the package <tt>qualifiedName</tt> is defined in
     * @param otherPkg the package target is in
     * @param target the target type statement
     * @param qualifiedName the qualified name to check is contained in the target
     * @return returns <tt>true</tt> if the target type statement contains the given qualified name
     * @see TargetTypeSearcher
     */
    private boolean targetContainsName(String currentPkg, String otherPkg, TypeStatement target, String qualifiedName) {
        // Compare packages
        if (!otherPkg.equals(currentPkg))
            return false;

        // Perform search in target
        searcher.resetState(qualifiedName);
        searcher.visit(target);
        return searcher.isMatched();
    }

    /**
     * A resolved qualified type.
     */
    public static final class QualifiedType {

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

    private static final class TargetTypeSearcher extends StatementVisitor {

        private String[] targetName;
        private int matches = 0;
        private int nesting = -1;
        private boolean cancelled;

        public void resetState(String targetName) {
            this.targetName = targetName.split("\\.");
            matches = 0;
            nesting = -1;
            cancelled = false;
        }

        public void visit(Statement statement) {
            if (isMatched() || cancelled) // early termination (optimization)
                return;
            super.visit(statement);
        }

        @Override
        public void visitClassStatement(ClassStatement statement) {
            nesting++;
            attemptMatch(statement.getDescriptor().getIdentifierName());
            super.visitClassStatement(statement);
            nesting--;
        }

        @Override
        public void visitEnumStatement(EnumStatement statement) {
            nesting++;
            attemptMatch(statement.getDescriptor().getIdentifierName());
            super.visitEnumStatement(statement);
            nesting--;
        }

        private void attemptMatch(IdentifierName identifierName) {
            if (nesting >= targetName.length) {
                cancelled = true;
            } else if (!isMatched() && identifierName.toString().equals(targetName[nesting])) {
                matches++; // XXX this may be error prone, check some stuff w this w diff nesting interleaving
            }
        }

        boolean isMatched() {
            return matches == targetName.length;
        }
    }
}
