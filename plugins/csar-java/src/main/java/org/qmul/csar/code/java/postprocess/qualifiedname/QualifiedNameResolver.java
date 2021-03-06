package org.qmul.csar.code.java.postprocess.qualifiedname;

import org.qmul.csar.code.java.parse.statement.AnnotationStatement;
import org.qmul.csar.code.java.parse.statement.ImportStatement;
import org.qmul.csar.code.java.parse.statement.PackageStatement;
import org.qmul.csar.code.java.parse.statement.CompilationUnitStatement;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.util.Stopwatch;

import java.nio.file.Path;
import java.util.*;

/**
 * A qualified name resolver. This is not thread-safe.
 */
public class QualifiedNameResolver {

    /**
     * Run-time statistics for {@link QualifiedNameResolver}.
     */
    public static final class Statistics {

        private Stopwatch stopwatch = new Stopwatch();
        private long currentClassTimeTaken = 0;
        private long currentParentClassTimeTaken = 0;
        private long samePackageTimeTaken = 0;
        private long otherPackageTimeTaken = 0;
        private long defaultPackageTimeTaken = 0;

        private void prepare() {
            stopwatch.reset();
        }

        private double elapsedMillis() {
            return stopwatch.elapsedMillis();
        }

        public void reset() {
            stopwatch.reset();
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

    /**
     * Resolves a qualified name with <tt>strict</tt> set to <tt>false</tt>.
     * @see #resolve(Map, Path, TypeStatement, TypeStatement, Optional, List, String, boolean)
     */
    public QualifiedType resolve(Map<Path, Statement> code, Path path, TypeStatement parent,
            TypeStatement topLevelParent, Optional<PackageStatement> currentPackage, List<ImportStatement> imports,
            String name) {
        return resolve(code, path, parent, topLevelParent, currentPackage, imports, name, false);
    }

    /**
     * Resolves a fully qualified name.
     */
    public QualifiedType resolveFullyQualifiedName(Map<Path, Statement> code, String name) {
        // TODO make faster by ignoring package names with no content
        // 1. break up repeatedly name into pkg and name
        int dotIdx = name.indexOf(".");
        String pkg = "";

        while (dotIdx != -1) {
            pkg += name.substring(0, dotIdx);
            name = name.substring(dotIdx + 1);
            dotIdx = name.indexOf(".");

            // 2. resolve against classes in target package package
            Optional<PackageStatement> currentPackage = Optional.of(new PackageStatement(pkg, new ArrayList<>()));

            statistics.prepare();
            QualifiedType t = resolveInCurrentPackage(code, currentPackage, name);
            statistics.samePackageTimeTaken += statistics.elapsedMillis();

            if (t != null)
                return t;
        }
        return null;
    }

    /**
     * Resolves a qualified name.
     */
    public QualifiedType resolve(Map<Path, Statement> code, Path path, TypeStatement parent,
            TypeStatement topLevelParent, Optional<PackageStatement> currentPackage, List<ImportStatement> imports,
            String name, boolean strict) {
        // TODO handle fully qualified names here too?
        // If the name contains generic arguments, we omit it
        int leftAngleBracketIdx = name.indexOf('<');

        if (leftAngleBracketIdx != -1)
            name = name.substring(0, leftAngleBracketIdx);

        // Resolve against inner classes in current class
        statistics.prepare();
        QualifiedType t0 = resolveInCurrentClass(topLevelParent, parent, currentPackage, name, path);
        statistics.currentClassTimeTaken += statistics.elapsedMillis();

        if (t0 != null)
            return t0;

        // Resolve against inner classes in top-level parent class
        statistics.prepare();
        QualifiedType t1 = resolveInCurrentClass(topLevelParent, topLevelParent, currentPackage, name, path);
        statistics.currentParentClassTimeTaken += statistics.elapsedMillis();

        if (t1 != null)
            return t1;

        // Resolve against classes in same package
        statistics.prepare();
        QualifiedType t2 = resolveInCurrentPackage(code, currentPackage, name);
        statistics.samePackageTimeTaken += statistics.elapsedMillis();

        if (t2 != null)
            return t2;

        // Resolve against imports
        statistics.prepare();
        QualifiedType t3 = resolveInOtherPackages(code, imports, name);
        statistics.otherPackageTimeTaken += statistics.elapsedMillis();

        if (t3 != null)
            return t3;

        // Resolve against default package
        statistics.prepare();
        QualifiedType t4 = resolveInDefaultPackage(code, path, currentPackage, name);
        statistics.defaultPackageTimeTaken += statistics.elapsedMillis();

        if (t4 != null)
            return t4;

        if (strict) // TODO remove strict mode once external libraries are handled
            return null;

        // If name contains dots, we assume it is a fully qualified name
        if (name.contains(".")) { // TODO resolve this properly
            return new QualifiedType(name);
        }

        // Assume it exists (for external APIs sake)
        return new QualifiedType(name); // TODO resolve this properly
    }

    private QualifiedType resolveInCurrentClass(TypeStatement topLevelParent, TypeStatement targetType,
            Optional<PackageStatement> pkg, String name, Path path) {
        if (targetType instanceof CompilationUnitStatement) {
            targetType = ((CompilationUnitStatement)targetType).getTypeStatement();
        }
        name = name.replace(".", "$");

        // Compute
        innerSearcher.resetState(pkg);
        innerSearcher.visitStatement(targetType);

        for (Map.Entry<String, Statement> innerType : innerSearcher.getTypes().entrySet()) {
            String qualifiedName = innerType.getKey();
            Statement type = innerType.getValue();

            if (qualifiedName.endsWith("." + name) || qualifiedName.endsWith("$" + name)) {
                return new QualifiedType(qualifiedName, type, (CompilationUnitStatement)topLevelParent, path);
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
                QualifiedType qualifiedType = otherPackagesCache.get(otherPackagesEntry);

                if (qualifiedType == null) {
                    continue;
                } else {
                    return qualifiedType;
                }
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
            Path p = entry.getKey();
            Statement statement = entry.getValue();

            if (!(statement instanceof CompilationUnitStatement))
                continue;
            CompilationUnitStatement topStatement = (CompilationUnitStatement) statement;
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
            innerSearcher.visitStatement(typeStatement);

            for (Map.Entry<String, Statement> innerType : innerSearcher.getTypes().entrySet()) {
                String qualifiedName = innerType.getKey();
                Statement type = innerType.getValue();
                String normalizedQn = qualifiedName.replace("$", ".");

                if (normalizedQn.endsWith("." + normalizedName) || normalizedQn.endsWith("$" + normalizedName)) {
                    return new QualifiedType(qualifiedName, type, topStatement, p);
                }
            }
        }

        // Fall-back: assume it exists if it looks like a fully qualified name (handles external APIs in a loose way)
        if (importQualifiedName.endsWith("." + name)) {
            return new QualifiedType(importQualifiedName);
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
            Path p = entry.getKey();
            Statement statement = entry.getValue();

            if (!(statement instanceof CompilationUnitStatement))
                continue;
            CompilationUnitStatement topLevelParent = (CompilationUnitStatement) statement;
            TypeStatement typeStatement = topLevelParent.getTypeStatement();

            if (topLevelParent.getPackageStatement().isPresent()) {
                String otherPkg = topLevelParent.getPackageStatement().get().getPackageName();
                Statement matchedStatement = typeContainsName(currentPkg, otherPkg, typeStatement, name);

                if (matchedStatement != null) {
                    String qualifiedName = otherPkg + "." + String.join("$", name.split("\\."));
                    QualifiedType type = new QualifiedType(qualifiedName, matchedStatement, topLevelParent, p);
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
                Path p = entry.getKey();
                Statement statement = entry.getValue();

                if (!(statement instanceof CompilationUnitStatement))
                    continue;
                CompilationUnitStatement topLevelParent = (CompilationUnitStatement) statement;
                TypeStatement typeStatement = topLevelParent.getTypeStatement();

                // they have to both have no package statement, and be in the same folder
                if (!topLevelParent.getPackageStatement().isPresent()
                        && path.getParent().equals(entry.getKey().getParent())) {
                    Statement matchedStatement = typeContainsName("", "", typeStatement, name);

                    if (matchedStatement != null) {
                        String qualifiedName = String.join("$", name.split("\\."));
                        QualifiedType type = new QualifiedType(qualifiedName, matchedStatement, topLevelParent, p);
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
     * Returns the statement corresponding to the given qualified name if it was in the argument type.
     * If <tt>currentPkg</tt> and <tt>otherPkg</tt> are not equal, or it is not found, then <tt>null</tt> is returned.
     *
     * @param currentPkg the package <tt>qualifiedName</tt> is defined in
     * @param otherPkg the package target is in
     * @param type the type statement to search
     * @param qualifiedName the qualified name to check is contained in the target
     * @return returns the statement corresponding to the given qualified name if it was in the argument type
     * @see TargetTypeSearcher
     */
    private Statement typeContainsName(String currentPkg, String otherPkg, TypeStatement type, String qualifiedName) {
        // Compare packages
        if (!otherPkg.equals(currentPkg))
            return null;

        // Perform search in target
        searcher.resetState(qualifiedName);
        searcher.visitStatement(type);
        return searcher.getMatchedStatement();
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
            return Objects.equals(importStatement, that.importStatement) && Objects.equals(name, that.name);
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

        public DefaultPackageEntry(Path path, Optional<PackageStatement> currentPackage, String name) {
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
}
