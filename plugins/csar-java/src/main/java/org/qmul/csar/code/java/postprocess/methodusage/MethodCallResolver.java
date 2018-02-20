package org.qmul.csar.code.java.postprocess.methodusage;

import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.java.postprocess.PostProcessUtils;
import org.qmul.csar.code.java.postprocess.TypeHelper;
import org.qmul.csar.code.java.postprocess.methodproc.ExpressionTypeResolver;
import org.qmul.csar.code.java.postprocess.methodproc.TypeInstance;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.lang.descriptors.ParameterVariableDescriptor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MethodCallResolver {

    private final Path path;
    private final Map<Path, Statement> code;
    private final QualifiedNameResolver qualifiedNameResolver;
    private final TypeHierarchyResolver typeHierarchyResolver;
    private TypeStatement baseTopLevelParent;
    private TypeStatement baseTypeStatement;
    private List<ImportStatement> baseImports;
    private Optional<PackageStatement> basePackageStatement;
    private BlockStatement baseContext;
    private MethodCallExpression methodCall;
    private List<TypeInstance> parameterTypeInstances;

    public MethodCallResolver(Path path, Map<Path, Statement> code, QualifiedNameResolver qualifiedNameResolver,
            TypeHierarchyResolver typeHierarchyResolver) {
        this.path = path;
        this.code = code;
        this.qualifiedNameResolver = qualifiedNameResolver;
        this.typeHierarchyResolver = typeHierarchyResolver;
        this.parameterTypeInstances = new ArrayList<>();
    }

    public boolean resolve(MethodCallExpression e, TraversalHierarchy traversalHierarchy) {
        // Set context
        baseTopLevelParent = traversalHierarchy.getFirstTypeStatement();
        baseTypeStatement = traversalHierarchy.getLastTypeStatement();
        baseImports = traversalHierarchy.getImports();
        basePackageStatement = traversalHierarchy.getPackageStatement();
        baseContext = traversalHierarchy.currentContext();
        methodCall = e;

        // Set argument type instances
        parameterTypeInstances.clear();

        for (Expression arg : methodCall.getArguments()) {
            TypeInstance t = ExpressionTypeResolver.resolve(path, code, baseTopLevelParent, baseTypeStatement,
                    baseImports, basePackageStatement, baseContext, qualifiedNameResolver, traversalHierarchy, arg);
            parameterTypeInstances.add(t);
        }

        // Resolve in current context, then in current type statement, then in superclasses
        return resolveInBlock(baseContext) || resolveInTypeStatement(baseTypeStatement)
                || resolveInSuperClasses(baseTypeStatement, baseTopLevelParent, basePackageStatement, baseImports);
    }

    private boolean resolveInTypeStatement(TypeStatement typeStatement) {
        return resolveInBlock(PostProcessUtils.getBlock(typeStatement));
    }

    private boolean resolveInSuperClasses(TypeStatement targetType, TypeStatement topLevelParent,
            Optional<PackageStatement> packageStatement, List<ImportStatement> imports) {
        for (String superClass : PostProcessUtils.superClasses(targetType)) {
            QualifiedType resolvedType = qualifiedNameResolver.resolve(code, path, targetType, topLevelParent,
                    packageStatement, imports, superClass);

            boolean found = resolveInQualifiedType(resolvedType, topLevelParent);

            if (found)
                return true;
        }
        return false;
    }

    private boolean resolveInQualifiedType(QualifiedType resolvedType, TypeStatement topLevelParent) {
        Statement resolvedStatement = resolvedType.getStatement();

        if (resolvedStatement != null && resolvedStatement instanceof CompilationUnitStatement) {
            TypeStatement typeStatement = ((CompilationUnitStatement)resolvedStatement).getTypeStatement();
            List<ImportStatement> imports = ((CompilationUnitStatement)resolvedStatement).getImports();
            Optional<PackageStatement> pkgStatement = ((CompilationUnitStatement)resolvedStatement).getPackageStatement();

            boolean found = resolveInTypeStatement(typeStatement);

            if (found)
                return true;

            // Check super classes
            return resolveInSuperClasses(typeStatement, topLevelParent, pkgStatement, imports);
        }
        return false;
    }

    private boolean resolveInBlock(BlockStatement block) {
        List<Statement> statements = block.getStatements();

        if (statements.size() == 0)
            return false;
        String methodName = methodCall.getMethodIdentifier();
        List<Expression> args = methodCall.getArguments();

        return statements.stream()
                .filter(s -> s instanceof MethodStatement)
                .map(s -> (MethodStatement)s)
                .anyMatch(method -> {
            MethodDescriptor desc = method.getDescriptor();

            boolean methodNameEquals = methodName.equals(desc.getIdentifierName().toString());
            boolean argsEquals = parametersSignatureEquals(method.getParameters(), desc.getTypeParameters());
            // TODO check visibility modifier and accessibility

            if (methodNameEquals && argsEquals) {
                method.getMethodUsages().add(methodCall);
                return true;
            }
            return false;
        });
    }

    private boolean parametersSignatureEquals(List<ParameterVariableStatement> parameters,
            List<String> typeParameters1) {

        // TODO ensure correctness: may breakdown
        // XXX list1 is the method's parameters, arguments are from a method call
        if (parameters.size() != methodCall.getArguments().size())
            return false;

        for (int i = 0; i < parameters.size(); i++) {
            ParameterVariableDescriptor param1 = parameters.get(i).getDescriptor();
            TypeInstance qtype1 = parameters.get(i).getTypeInstance();
            TypeInstance qtype2 = parameterTypeInstances.get(i);

            if (!param1.getIdentifierType().isPresent())
                return false;

            // Names
            String type1 = param1.getIdentifierType().get();
            String type2 = qtype2.getType();
            type1 = TypeHelper.resolveGenericTypes(TypeHelper.normalizeVarArgs(type1), typeParameters1);
            type2 = TypeHelper.normalizeVarArgs(type2);
            boolean namesEqual = type1.equals(type2);
            boolean dimensionEquals = TypeHelper.dimensionsEquals(type1, type2);

            // Generic argument
            String genericArgument1 = TypeHelper.extractGenericArgument(type1);
            String genericArgument2 = TypeHelper.extractGenericArgument(type2);

            boolean genericTypesEqual = genericArgument1.equals(genericArgument2)
                    || !genericArgument1.isEmpty() && genericArgument2.isEmpty();

            // Check base types
            if (qtype1 != null && qtype2 != null) {
                if (!typeHierarchyResolver.isSubtype(qtype1.getQualifiedName(), qtype2.getQualifiedName())
                        || !genericTypesEqual || !dimensionEquals) {
                    return false;
                }
            } else if (!namesEqual || !genericTypesEqual || !dimensionEquals) { // fall-back comparison, to support java api
                return false;
            }
        }
        return true;
    }
}
