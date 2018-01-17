package org.qmul.csar.code.java.postprocess.methodtypes;

import org.qmul.csar.code.CodePostProcessor;
import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.lang.descriptors.ParameterVariableDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MethodQualifiedTypeResolver implements CodePostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodQualifiedTypeResolver.class);
    private final QualifiedNameResolver qualifiedNameResolver;

    public MethodQualifiedTypeResolver(QualifiedNameResolver qualifiedNameResolver) {
        this.qualifiedNameResolver = qualifiedNameResolver;
    }

    /**
     * Resolves the types of each method in the argument codebase.
     *
     * @param code the code base to resolve for
     */
    public void analyze(Map<Path, Statement> code) {
        LOGGER.info("Starting...");
        long startTime = System.currentTimeMillis();

        // Iterate all code files
        for (Map.Entry<Path, Statement> entry : code.entrySet()) {
            Path path = entry.getKey();
            Statement statement = entry.getValue();

            if (!(statement instanceof CompilationUnitStatement))
                continue;
            CompilationUnitStatement topStatement = (CompilationUnitStatement) statement;
            TypeStatement typeStatement = topStatement.getTypeStatement();

            if (typeStatement instanceof AnnotationStatement)
                continue;

            // Visit file
            MethodStatementVisitor visitor = new MethodStatementVisitor(qualifiedNameResolver, code, path, topStatement);
            visitor.visitStatement(statement);
        }

        // Log completion message
        LOGGER.debug("Time Taken: {}ms", (System.currentTimeMillis() - startTime));
        LOGGER.info("Finished");
    }

    /**
     * Traverses a {@link CompilationUnitStatement} and resolves the qualified types for method return types and method
     * parameter types.
     */
    public static class MethodStatementVisitor extends StatementVisitor {

        private final QualifiedNameResolver qualifiedNameResolver;
        private Path path;
        private Map<Path, Statement> code;
        private TypeStatement topLevelParent;
        private List<ImportStatement> imports;
        private Optional<PackageStatement> currentPackage;
        private TypeStatement parent;

        public MethodStatementVisitor(QualifiedNameResolver qualifiedNameResolver, Map<Path, Statement> code, Path path,
                CompilationUnitStatement compilationUnitStatement) {
            this.qualifiedNameResolver = qualifiedNameResolver;
            this.code = code;
            this.path = path;
            this.topLevelParent = compilationUnitStatement;
            this.currentPackage = compilationUnitStatement.getPackageStatement();
            this.imports = compilationUnitStatement.getImports();
            this.parent = compilationUnitStatement;
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
            String returnType = TypeHelper.resolveGenericTypes(desc.getReturnType().get(), desc.getTypeParameters());
            String returnTypeQualifiedName = TypeHelper.removeGenericArgument(returnType);
            // TODO do properly: compare generic args qualified names

            // Resolve
            QualifiedType type = qualifiedNameResolver.resolve(code, path, parent, topLevelParent, currentPackage, imports,
                    returnTypeQualifiedName);
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
                param.setQualifiedType(type);
            }
        }
    }
}
