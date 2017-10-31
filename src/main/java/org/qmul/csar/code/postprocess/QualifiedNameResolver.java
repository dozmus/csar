package org.qmul.csar.code.postprocess;

import org.qmul.csar.code.parse.java.statement.*;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.StatementVisitor;
import org.qmul.csar.lang.TypeStatement;

import java.nio.file.Path;
import java.util.*;

public class QualifiedNameResolver {

    /**
     * Run-time statistics for {@link QualifiedNameResolver}.
     */
    public static final class Statistics {

        private long startTime = 0;
        private long currentClassTimeTaken = 0;
        private long currentParentClassTimeTaken = 0;
        private long samePackageTimeTaken = 0;
        private long otherPackageTimeTaken = 0;
        private long defaultPackageTimeTaken = 0;

        private void prepare() {
            startTime = System.nanoTime();
        }

        private double diff() {
            return (System.nanoTime() - startTime) / 1000000;
        }

        public void reset() {
            startTime = 0;
            currentClassTimeTaken = 0;
            currentParentClassTimeTaken = 0;
            samePackageTimeTaken = 0;
            otherPackageTimeTaken = 0;
            defaultPackageTimeTaken = 0;
        }

        public String toString() {
            return String.format("CurrentClass: %dms, CurrentParentClass: %dms, SamePackage: %dms, OtherPackage: %dms, "
                            + "DefaultPackage: %dms",
                    currentClassTimeTaken, currentParentClassTimeTaken, samePackageTimeTaken, otherPackageTimeTaken,
                    defaultPackageTimeTaken);
        }
    }

    /**
     * The target type searcher to use.
     */
    private final TargetTypeSearcher searcher = new TargetTypeSearcher();
    private final TargetTypeSearcherForInnerClass innerSearcher = new TargetTypeSearcherForInnerClass();
    private final Map<CurrentPackageEntry, QualifiedType> currentPackageCache = new HashMap<>();
    private final Map<OtherPackagesEntry, QualifiedType> otherPackagesCache = new HashMap<>();
    private final Map<DefaultPackageEntry, QualifiedType> defaultPackageCache = new HashMap<>();
    private final Statistics statistics = new Statistics();

    public QualifiedType resolve(Map<Path, Statement> code, Path path, TypeStatement parent,
            TypeStatement topLevelParent, Optional<PackageStatement> currentPackage, List<ImportStatement> imports,
            String name) {
        // If the name contains generic arguments, we omit it
        int leftAngleBracketIdx = name.indexOf('<');

        if (leftAngleBracketIdx != -1)
            name = name.substring(0, leftAngleBracketIdx);

        // Resolve against inner classes in current class
        statistics.prepare();
        QualifiedType t0 = resolveInCurrentClass(parent, currentPackage, name);
        statistics.currentClassTimeTaken += statistics.diff();

        if (t0 != null)
            return t0;

        // Resolve against inner classes in top-level parent class
        statistics.prepare();
        QualifiedType t1 = resolveInCurrentClass(topLevelParent, currentPackage, name);
        statistics.currentParentClassTimeTaken += statistics.diff();

        if (t1 != null)
            return t1;

        // Resolve against classes in same package
        statistics.prepare();
        QualifiedType t2 = resolveInCurrentPackage(code, currentPackage, name);
        statistics.samePackageTimeTaken += statistics.diff();

        if (t2 != null)
            return t2;

        // Resolve against imports
        statistics.prepare();
        QualifiedType t3 = resolveInOtherPackages(code, imports, name);
        statistics.otherPackageTimeTaken += statistics.diff();

        if (t3 != null)
            return t3;

        // Resolve against default package
        statistics.prepare();
        QualifiedType t4 = resolveInDefaultPackage(code, path, currentPackage, name);
        statistics.defaultPackageTimeTaken += statistics.diff();

        if (t4 != null)
            return t4;

        // If name contains dots, we assume it is a fully qualified name
        // TODO check this properly
        if (name.contains(".")) {
            return new QualifiedType(name, null);
        }

        // Assume it exists (for external APIs sake)
        // TODO check this properly
        return new QualifiedType(name, null);
//        throw new RuntimeException("could not resolve qualified name for " + name + " in " + path.toString());
    }

    private QualifiedType resolveInCurrentClass(TypeStatement parent, Optional<PackageStatement> pkg, String name) {
        // NOTE need to test this thoroughly
        // results look good though
        if (parent instanceof TopLevelTypeStatement) {
            parent = ((TopLevelTypeStatement)parent).getTypeStatement();
        }
        name = name.replace(".", "$");

        // Compute
        innerSearcher.resetState(pkg);
        innerSearcher.visit(parent);

        for (String foundQualifiedName : innerSearcher.types) {
            if (foundQualifiedName.endsWith("." + name) || foundQualifiedName.endsWith("$" + name)) {
                return new QualifiedType(foundQualifiedName, parent);
            }
        }
        return null;
    }

    private QualifiedType resolveInOtherPackages(Map<Path, Statement> code, List<ImportStatement> imports,
            String name) {
        String normalizedName = name.replace("$", ".");
        String nameWithoutSubIdentifiers = name.indexOf('.') == -1 ? name : name.substring(0, name.indexOf('.'));

        for (ImportStatement importStatement : imports) {
            OtherPackagesEntry otherPackagesEntry = new OtherPackagesEntry(importStatement, name);

            // Check cache
            if (otherPackagesCache.containsKey(otherPackagesEntry)) {
                return otherPackagesCache.get(otherPackagesEntry);
            }

            // Compute
            QualifiedType type = resolveInOtherPackages(code, importStatement, name, normalizedName,
                    nameWithoutSubIdentifiers);
            otherPackagesCache.put(otherPackagesEntry, type);

            if (type != null) {
                return type;
            }
        }
        return null;
    }

    private QualifiedType resolveInOtherPackages(Map<Path, Statement> code, ImportStatement importStatement,
            String name, String normalizedName, String nameWithoutSubIdentifiers) {
        if (importStatement.isStaticImport())
            return null;

        // Compute
        final String importQualifiedName = importStatement.getQualifiedName();
        String currentPkg;

        if (importQualifiedName.endsWith(".*")) { // wildcard import
            currentPkg = importQualifiedName.substring(0, importQualifiedName.length() - 2);
        } else if (importQualifiedName.endsWith("." + name)) { // specific import
            currentPkg = importQualifiedName.substring(0, importQualifiedName.lastIndexOf("." + name));
        } else if (!nameWithoutSubIdentifiers.equals(name)
                && importQualifiedName.endsWith("." + nameWithoutSubIdentifiers)) { // import inner class
            int endIdx = importQualifiedName.lastIndexOf("." + nameWithoutSubIdentifiers);
            currentPkg = importQualifiedName.substring(0, endIdx);
        } else { // fall-back: cannot be the import we are looking for
            return null;
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

            // Compare packages
            if (!otherPkg.equals(currentPkg))
                continue;

            // Perform search in target
            innerSearcher.resetStatePkgName(currentPkg);
            innerSearcher.visit(typeStatement);

            for (String foundQualifiedName : innerSearcher.types) {
                String normalizedQn = foundQualifiedName.replace("$", ".");

                if (normalizedQn.endsWith("." + normalizedName) || normalizedQn.endsWith("$" + normalizedName)) {
                    return new QualifiedType(foundQualifiedName, statement);
                }
            }
        }

        // Fall-back: assume it exists if it looks like a fully qualified name (handles external APIs in a loose way)
        if (importQualifiedName.endsWith("." + name)) {
            return new QualifiedType(importQualifiedName, null);
        }
        return null;
    }

    private QualifiedType resolveInCurrentPackage(Map<Path, Statement> code, Optional<PackageStatement> currentPackage,
            String name) {
        if (!currentPackage.isPresent())
            return null;

        // Check cache
        CurrentPackageEntry currentPackageEntry = new CurrentPackageEntry(currentPackage, name);

        if (currentPackageCache.containsKey(currentPackageEntry)) {
            return currentPackageCache.get(currentPackageEntry);
        }

        // Compute
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
                    QualifiedType type = new QualifiedType(qualifiedName, statement);
                    currentPackageCache.put(currentPackageEntry, type);
                    return type;
                }
            }
        }

        // Fall-through: no match
        currentPackageCache.put(currentPackageEntry, null);
        return null;
    }

    private QualifiedType resolveInDefaultPackage(Map<Path, Statement> code, Path path,
            Optional<PackageStatement> currentPackage, String name) {
        // Check cache
        DefaultPackageEntry defaultPackageEntry = new DefaultPackageEntry(path, currentPackage, name);

        if (defaultPackageCache.containsKey(defaultPackageEntry)) {
            return defaultPackageCache.get(defaultPackageEntry);
        }

        // Compute
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
                    if (targetContainsName("", "", typeStatement, name)) {
                        String qualifiedName = String.join("$", name.split("\\."));
                        QualifiedType type = new QualifiedType(qualifiedName, statement);
                        defaultPackageCache.put(defaultPackageEntry, type);
                        return type;
                    }
                }
            }
        }

        // Fall-through: no match
        defaultPackageCache.put(defaultPackageEntry, null);
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

    public Statistics getStatistics() {
        return statistics;
    }

    private static final class CurrentPackageEntry {

        private final Optional<PackageStatement> currentPackage;
        private final String name;

        public CurrentPackageEntry(Optional<PackageStatement> currentPackage, String name) {
            this.currentPackage = currentPackage;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CurrentPackageEntry that = (CurrentPackageEntry) o;
            return Objects.equals(currentPackage, that.currentPackage) && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(currentPackage, name);
        }
    }

    private static final class OtherPackagesEntry {

        private final ImportStatement importStatement;
        private final String name;

        public OtherPackagesEntry(ImportStatement importStatement, String name) {
            this.importStatement = importStatement;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OtherPackagesEntry that = (OtherPackagesEntry) o;
            return Objects.equals(importStatement, that.importStatement)
                    && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(importStatement, name);
        }
    }

    private static final class DefaultPackageEntry {

        private final Path path;
        private final Optional<PackageStatement> currentPackage;
        private final String name;

        public DefaultPackageEntry(Path path,
                Optional<PackageStatement> currentPackage, String name) {
            this.path = path;
            this.currentPackage = currentPackage;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DefaultPackageEntry that = (DefaultPackageEntry) o;
            return Objects.equals(path, that.path)
                    && Objects.equals(currentPackage, that.currentPackage)
                    && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, currentPackage, name);
        }
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
            if (isMatched() || cancelled) // TODO is early termination erroneous (optimization)
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
                matches++; // XXX is error prone, check some stuff w this w diff nesting interleaving
            }
        }

        boolean isMatched() {
            return matches == targetName.length;
        }
    }

    private static final class TargetTypeSearcherForInnerClass extends StatementVisitor {

        private final Deque<String> traversalHierarchy = new ArrayDeque<>();
        private final List<String> types = new ArrayList<>();
        private boolean inner = false;

        public void resetState(Optional<PackageStatement> packageStatement) {
            traversalHierarchy.clear();
            types.clear();
            inner = false;

            if (packageStatement.isPresent()) {
                resetStatePkgName(packageStatement.get().getPackageName());
            } else {
                resetStatePkgName(null);
            }
        }

        public void resetStatePkgName(String pkg) {
            traversalHierarchy.clear();
            types.clear();
            inner = false;

            if (pkg != null && !pkg.isEmpty()) {
                String[] pkgParts = pkg.split("\\.");

                for (int i = 0; i < pkgParts.length; i++) {
                    String s = pkgParts[i];

                    if (i > 0)
                        s = "." + s;
                    traversalHierarchy.addLast(s);
                }
            }
        }

        @Override
        public void visitClassStatement(ClassStatement statement) {
            appendCurrentIdentifier(statement.getDescriptor().getIdentifierName());
            super.visitClassStatement(statement);
            traversalHierarchy.removeLast();
        }

        @Override
        public void visitEnumStatement(EnumStatement statement) {
            appendCurrentIdentifier(statement.getDescriptor().getIdentifierName());
            super.visitEnumStatement(statement);
            traversalHierarchy.removeLast();
        }

        private void appendCurrentIdentifier(IdentifierName identifierName) {
            traversalHierarchy.addLast(prefix() + identifierName.toString());
            types.add(String.join("", traversalHierarchy));
            inner = true;
        }

        private String prefix() {
            if (traversalHierarchy.size() == 0)
                return "";
            return inner ? "$" : ".";
        }
    }
}
