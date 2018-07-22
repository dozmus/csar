package org.qmul.csar.code.java.postprocess.methods.overridden;

import com.github.dozmus.iterators.ConcurrentIterator;
import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.postprocess.CodePostProcessor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A post-processor which assigns to each {@link MethodStatement} whether its overridden or not, to its
 * {@link MethodDescriptor#getOverridden()}.
 */
public class OverriddenMethodsResolver extends MultiThreadedTaskProcessor implements CodePostProcessor {

    // TODO handle methods overridden from java api classes?

    private static final Logger LOGGER = LoggerFactory.getLogger(OverriddenMethodsResolver.class);
    /**
     * Maps a method's full signature to whether it's overridden or not. e.g. 'com.example.MyClass#int add(int,int)' ->
     * 'true'.
     */
    private final ConcurrentHashMap<String, Boolean> map = new ConcurrentHashMap<>();
    /**
     * The qualified name resolver to use.
     */
    private final QualifiedNameResolver qnr;
    /**
     * The type hierarchy resolver to use.
     */
    private final TypeHierarchyResolver thr;
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();
    private Map<Path, Statement> code;
    private ConcurrentIterator<Map.Entry<Path, Statement>> it;

    public OverriddenMethodsResolver(QualifiedNameResolver qnr, TypeHierarchyResolver thr) {
        this(1, qnr, thr);
    }

    public OverriddenMethodsResolver(int threadCount, QualifiedNameResolver qnr, TypeHierarchyResolver thr) {
        super(threadCount, "csar-omr");
        this.qnr = qnr;
        this.thr = thr;
        setRunnable(new Task());
    }

    public void postprocess(Map<Path, Statement> code) {
        LOGGER.info("Starting...");

        // Execute and return results
        this.code = code;
        it = new ConcurrentIterator<>(code.entrySet().iterator());
        Stopwatch stopwatch = new Stopwatch();
        run();

        // Log completion message
        LOGGER.debug("Found {} overridden methods in {}ms", map.size(), stopwatch.elapsedMillis());
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

    public boolean calculateOverridden(Map<Path, Statement> code, Path path, Optional<PackageStatement> pkg,
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

            if (!descriptor.getExtendedClass().isPresent() && descriptor.getImplementedInterfaces().size() == 0) // TODO update once java api supported
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

    private boolean calculateOverridden(Map<Path, Statement> code, Optional<PackageStatement> packageStatement,
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

    public boolean isOverridden(String methodSignature) {
        return map.getOrDefault(methodSignature, false);
    }

    public Map<String, Boolean> getMap() {
        return map;
    }

    private final class Task implements Runnable {

        @Override
        public void run() {
            Map.Entry<Path, Statement> entry;
            MethodStatementVisitor visitor = new MethodStatementVisitor(code, OverriddenMethodsResolver.this);

            try {
                while (it.hasNext() && !Thread.currentThread().isInterrupted()) {
                    // Get the next entry
                    entry = it.next();
                    Path path = entry.getKey();
                    Statement statement = entry.getValue();

                    if (statement instanceof CompilationUnitStatement) {
                        CompilationUnitStatement compilationUnitStatement = (CompilationUnitStatement) statement;

                        visitor.reset();
                        visitor.setPath(path);
                        visitor.setCompilationUnitStatement(compilationUnitStatement);
                        visitor.visitStatement(compilationUnitStatement);
                    }
                }
            } catch (Exception ex) {
                errorListeners.forEach(l -> l.fatalErrorPostProcessing(ex));
                terminate();
            }
        }
    }
}