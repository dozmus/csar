package org.qmul.csar.code.java.postprocess.methods.overridden;

import com.github.dozmus.iterators.ConcurrentIterator;
import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.CodeBase;
import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.code.java.postprocess.util.PostProcessUtils;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptors.ClassDescriptor;
import org.qmul.csar.lang.descriptors.EnumDescriptor;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.util.MultiThreadedTaskProcessor;
import org.qmul.csar.util.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;


/**
 * A {@link OverriddenMethodsResolver} which supports multi-threading, and filtering methods processed by a
 * {@link Predicate}.
 */
public class DefaultOverriddenMethodsResolver extends MultiThreadedTaskProcessor implements OverriddenMethodsResolver {

    // TODO handle methods overridden from java api classes?

    private static final Logger LOGGER = LoggerFactory.getLogger(OverriddenMethodsResolver.class);
    /**
     * The qualified name resolver to use.
     */
    private final QualifiedNameResolver qnr;
    /**
     * The type hierarchy resolver to use.
     */
    private final TypeHierarchyResolver thr;
    /**
     * The method filter, this determines which methods are post-processed.
     */
    private final Predicate<MethodStatement> methodFilter;
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();
    private final AtomicInteger overriddenMethods = new AtomicInteger();
    private CodeBase code;
    private ConcurrentIterator<Map.Entry<Path, Statement>> it;

    public DefaultOverriddenMethodsResolver(QualifiedNameResolver qnr, TypeHierarchyResolver thr) {
        this(1, qnr, thr);
    }

    public DefaultOverriddenMethodsResolver(int threadCount, QualifiedNameResolver qnr, TypeHierarchyResolver thr) {
        this(threadCount, qnr, thr, m -> true);
    }

    public DefaultOverriddenMethodsResolver(int threadCount, QualifiedNameResolver qnr, TypeHierarchyResolver thr,
            Predicate<MethodStatement> methodFilter) {
        super(threadCount, "csar-omr");
        this.qnr = qnr;
        this.thr = thr;
        this.methodFilter = methodFilter;
        setRunnable(new Task());
    }

    public void postprocess(CodeBase code) {
        LOGGER.info("Starting...");

        // Execute and return results
        this.code = code;
        it = new ConcurrentIterator<>(code.iterator());
        Stopwatch stopwatch = new Stopwatch();
        run();

        // Log completion message
        LOGGER.debug("Found {} overridden methods in {}ms", overriddenMethods.get(), stopwatch.elapsedMillis());
        LOGGER.debug("Statistics: " + qnr.getStatistics().toString());
        LOGGER.info("Finished");
    }

    @Override
    public void addErrorListener(CsarErrorListener errorListener) {
        errorListeners.remove(errorListener);
    }

    @Override
    public void removeErrorListener(CsarErrorListener errorListener) {
        errorListeners.remove(errorListener);
    }

    public boolean calculateOverridden(CodeBase code, Path path, Optional<PackageStatement> pkg,
            List<ImportStatement> imports, TypeStatement typeStatement, TypeStatement parent, MethodStatement method) {

        // Check if @Override annotation present
        // XXX it's a compile error to specify non-overridden methods as @Override
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.getIdentifierName().equals("Override") && !annotation.getValue().isPresent()) {
                return true;
            }
        }

        // TODO check methods defined in java.lang.Object

        // Parse parent type
        if (typeStatement instanceof ClassStatement) {
            ClassStatement classStatement = (ClassStatement) typeStatement;
            ClassDescriptor descriptor = classStatement.getDescriptor();

            // TODO update once java api supported
            if (!descriptor.getExtendedClass().isPresent() && descriptor.getImplementedInterfaces().size() == 0)
                return false;
            List<String> superClasses = PostProcessUtils.superClasses(classStatement);
            return calculateOverridden(code, pkg, imports, superClasses, path, typeStatement, parent, method);
        } else if (typeStatement instanceof EnumStatement) {
            EnumStatement enumStatement = (EnumStatement) typeStatement;
            EnumDescriptor descriptor = enumStatement.getDescriptor();

            if (descriptor.getSuperClasses().size() == 0) // TODO update once java api supported
                return false;
            return calculateOverridden(code, pkg, imports, descriptor.getSuperClasses(), path, typeStatement, parent,
                    method);
        }
        // NOTE annotation types cannot have superclasses
        return false;
    }

    private boolean calculateOverridden(CodeBase code, Optional<PackageStatement> packageStatement,
            List<ImportStatement> imports, List<String> superClasses, Path path, TypeStatement parent,
            TypeStatement topLevelParent, MethodStatement method) {
        MethodDescriptor desc = method.getDescriptor();

        // Check all superclasses which are a class or an interface for a method this one might be overriding
        for (String superClass : superClasses) {
            QualifiedType resolvedType;

            synchronized (qnr) {
                resolvedType = qnr.resolve(code, path, parent, topLevelParent, packageStatement, imports, superClass);
            }
            Statement resolvedStatement = resolvedType.getTopLevelStatement();

            // NOTE we ignore (fully) un-resolved statements here
            if (resolvedStatement != null) {
                CompilationUnitStatement superTopLevel = (CompilationUnitStatement) resolvedStatement;
                TypeStatement superType = superTopLevel.getTypeStatement();
                boolean isClass = superType instanceof ClassStatement;

                // Check target class
                if (!superType.equals(parent) && isClass) {
                    BlockStatement blockStatement = ((ClassStatement) superTopLevel.getTypeStatement()).getBlock();

                    for (Statement statement : blockStatement.getStatements()) {
                        if (!(statement instanceof MethodStatement))
                            continue;
                        MethodStatement m2 = (MethodStatement) statement;
                        MethodDescriptor desc2 = m2.getDescriptor();
                        boolean signatureEquals = MethodSignatureComparator.signatureEquals(m2, method, thr);

                        if (!signatureEquals)
                            continue;

                        if (desc2.getStaticModifier().orElse(false) || desc2.getFinalModifier().orElse(false))
                            return false;

                        boolean accessible = PostProcessUtils.isAccessible(desc, desc2, packageStatement,
                                superTopLevel.getPackageStatement(), superType, path, resolvedType.getPath());
                        return accessible;
                    }
                }

                // Check super classes of super class
                if (calculateOverridden(code, superTopLevel.getPackageStatement(), superTopLevel.getImports(),
                        PostProcessUtils.superClasses(superType), resolvedType.getPath(), superType,
                        superTopLevel, method)) {
                    return true;
                }
            }
        }
        return false;
    }

    private final class Task implements Runnable {

        @Override
        public void run() {
            Map.Entry<Path, Statement> entry;
            MethodStatementProcessor processor = new MethodStatementProcessor(code,
                    DefaultOverriddenMethodsResolver.this, methodFilter, overriddenMethods);

            try {
                while (it.hasNext() && !Thread.currentThread().isInterrupted()) {
                    // Get the next entry
                    entry = it.next();
                    Path path = entry.getKey();
                    Statement statement = entry.getValue();

                    if (statement instanceof CompilationUnitStatement) {
                        CompilationUnitStatement compilationUnitStatement = (CompilationUnitStatement) statement;

                        processor.reset();
                        processor.setPath(path);
                        processor.setCompilationUnitStatement(compilationUnitStatement);
                        processor.visitStatement(compilationUnitStatement);
                    }
                }
            } catch (Exception ex) {
                errorListeners.forEach(l -> l.fatalErrorPostProcessing(ex));
                terminate();
            }
        }
    }

    private final class MethodStatementProcessor extends StatementVisitor {

        private final CodeBase code;
        private final OverriddenMethodsResolver omr;
        private final Deque<TypeStatement> traversedTypeStatements = new ArrayDeque<>();
        private final Predicate<MethodStatement> methodFilter;
        private final AtomicInteger overriddenMethods;
        private Path path;
        private Optional<PackageStatement> packageStatement;
        private List<ImportStatement> imports;

        public MethodStatementProcessor(CodeBase code, OverriddenMethodsResolver omr,
                Predicate<MethodStatement> methodFilter, AtomicInteger overriddenMethods) {
            this.omr = omr;
            this.code = code;
            this.methodFilter = methodFilter;
            this.overriddenMethods = overriddenMethods;
        }

        public void reset() {
            traversedTypeStatements.clear();
        }

        @Override
        public void visitEnumStatement(EnumStatement statement) {
            traversedTypeStatements.addLast(statement);
            super.visitEnumStatement(statement);
        }

        @Override
        public void exitEnumStatement(EnumStatement statement) {
            traversedTypeStatements.removeLast();
        }

        @Override
        public void visitClassStatement(ClassStatement statement) {
            traversedTypeStatements.addLast(statement);
            super.visitClassStatement(statement);
        }

        @Override
        public void exitClassStatement(ClassStatement statement) {
            traversedTypeStatements.removeLast();
        }

        @Override
        public void visitMethodStatement(MethodStatement statement) {
            if (methodFilter.test(statement))
                mapOverridden(statement);
            super.visitMethodStatement(statement);
        }

        private void mapOverridden(MethodStatement method) {
            if (omr.calculateOverridden(code, path, packageStatement, imports, traversedTypeStatements.getLast(),
                    traversedTypeStatements.getFirst(), method)) {
                overriddenMethods.incrementAndGet();
                method.getDescriptor().setOverridden(Optional.of(true));
            } else {
                method.getDescriptor().setOverridden(Optional.of(false));
            }
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public void setCompilationUnitStatement(CompilationUnitStatement topLevelParent) {
            traversedTypeStatements.clear();
            traversedTypeStatements.addLast(topLevelParent);
            packageStatement = topLevelParent.getPackageStatement();
            imports = topLevelParent.getImports();
        }
    }
}
