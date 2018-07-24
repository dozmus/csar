package org.qmul.csar.code.java.postprocess.methods.types;

import org.qmul.csar.CsarErrorListener;
import org.qmul.csar.code.CodeBase;
import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.code.java.postprocess.util.TypeHelper;
import org.qmul.csar.code.java.postprocess.util.TypeInstance;
import org.qmul.csar.code.postprocess.CodePostProcessor;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.lang.descriptors.ParameterVariableDescriptor;
import org.qmul.csar.util.MultiThreadedTaskProcessor;
import org.qmul.csar.util.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A post-processor which attaches to each {@link MethodStatement} a {@link QualifiedType} for its return type and
 * all of its parameters.
 */
public class MethodQualifiedTypeResolver extends MultiThreadedTaskProcessor implements CodePostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodQualifiedTypeResolver.class);
    private final List<CsarErrorListener> errorListeners = new ArrayList<>();
    private final AtomicInteger methodsProcessed = new AtomicInteger();
    private CodeBase code;
    private Iterator<Map.Entry<Path, Statement>> it;

    public MethodQualifiedTypeResolver(int threadCount) {
        super(threadCount, "csar-mqtr");
        setRunnable(new Task());
    }

    public MethodQualifiedTypeResolver() {
        this(1);
    }

    /**
     * Resolves the types of each method in the argument codebase.
     *
     * @param code the code base to resolve for
     */
    public void postprocess(CodeBase code) {
        LOGGER.info("Starting...");

        // Execute and return results
        this.code = code;
        it = code.threadSafeIterator();
        Stopwatch stopwatch = new Stopwatch();
        run();

        // Log completion message
        LOGGER.debug("Processed {} methods in: {}ms", methodsProcessed, stopwatch.elapsedMillis());
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

    private final class Task implements Runnable {

        @Override
        public void run() {
            Map.Entry<Path, Statement> entry;
            MethodStatementProcessor processor = new MethodStatementProcessor(code);

            try {
                while (it.hasNext() && !Thread.currentThread().isInterrupted()) {
                    // Get the next entry
                    entry = it.next();
                    Path path = entry.getKey();
                    Statement statement = entry.getValue();

                    if (statement instanceof CompilationUnitStatement) {
                        CompilationUnitStatement cus = (CompilationUnitStatement) statement;
                        TypeStatement typeStatement = cus.getTypeStatement();

                        if (typeStatement instanceof AnnotationStatement)
                            continue;

                        // Visit file
                        processor.prepare(path, cus);
                        processor.visitStatement(statement);
                        methodsProcessed.addAndGet(processor.methodsProcessed);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorListeners.forEach(l -> l.fatalErrorPostProcessing(ex));
                terminate();
            }
        }
    }

    /**
     * Traverses a {@link CompilationUnitStatement} and resolves the qualified types for method return types and method
     * parameter types.
     */
    public static class MethodStatementProcessor extends StatementVisitor {

        private final QualifiedNameResolver qualifiedNameResolver = new QualifiedNameResolver();
        private final CodeBase code;
        private Path path;
        private TypeStatement topLevelParent;
        private List<ImportStatement> imports;
        private Optional<PackageStatement> currentPackage;
        private TypeStatement parent;
        private int methodsProcessed;

        public MethodStatementProcessor(CodeBase code) {
            this.code = code;
        }

        public void prepare(Path path, CompilationUnitStatement topLevelParent) {
            methodsProcessed = 0;
            this.path = path;
            this.topLevelParent = topLevelParent;
            this.currentPackage = topLevelParent.getPackageStatement();
            this.imports = topLevelParent.getImports();
            this.parent = topLevelParent;
        }

        @Override
        public void visitMethodStatement(MethodStatement statement) {
            methodsProcessed++;

            // Resolve return type
            resolveReturnType(statement);

            // Resolve parameter types
            resolveParameterTypes(statement);
        }

        private void resolveReturnType(MethodStatement statement) {
            MethodDescriptor desc = statement.getDescriptor();
            String returnType = TypeHelper.resolveGenericTypes(desc.getReturnType().get(), desc.getTypeParameters());
            String returnTypeQualifiedName = TypeHelper.removeGenericArgument(returnType);
            // TODO do properly: compare generic args qualified names

            // Resolve
            QualifiedType type = qualifiedNameResolver.resolve(code, path, parent, topLevelParent, currentPackage,
                    imports, returnTypeQualifiedName);
            statement.setReturnQualifiedType(type);
        }

        private void resolveParameterTypes(MethodStatement statement) {
            MethodDescriptor desc = statement.getDescriptor();

            for (ParameterVariableStatement param : statement.getParameters()) {
                ParameterVariableDescriptor paramDescriptor = param.getDescriptor();
                String parameterType = TypeHelper.resolveGenericTypes(paramDescriptor.getIdentifierType().get(),
                        desc.getTypeParameters());
                String parameterTypeQualifiedName = TypeHelper.removeGenericArgument(parameterType);
                // TODO do properly: compare generic args qualified names

                // Resolve
                QualifiedType type = qualifiedNameResolver.resolve(code, path, parent, topLevelParent, currentPackage,
                        imports, parameterTypeQualifiedName);
                int dimensions = TypeHelper.dimensions(paramDescriptor.getIdentifierType().get());
                TypeInstance typeInstance = new TypeInstance(type, dimensions);
                param.setTypeInstance(typeInstance);
            }
        }
    }
}
