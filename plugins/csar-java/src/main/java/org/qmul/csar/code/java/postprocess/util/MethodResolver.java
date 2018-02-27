package org.qmul.csar.code.java.postprocess.util;

import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.java.postprocess.methodusage.TraversalHierarchy;
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
import java.util.stream.Collectors;

public class MethodResolver {

    private final Path path;
    private final Map<Path, Statement> code;
    private final QualifiedNameResolver qualifiedNameResolver;
    private final TypeHierarchyResolver typeHierarchyResolver;
    private MethodCallExpression methodCall;
    private List<TypeInstance> parameterTypeInstances;
    private TraversalHierarchy traversalHierarchy;
    private boolean onVariable;

    public MethodResolver(Path path, Map<Path, Statement> code, QualifiedNameResolver qualifiedNameResolver,
            TypeHierarchyResolver typeHierarchyResolver) {
        this.path = path;
        this.code = code;
        this.qualifiedNameResolver = qualifiedNameResolver;
        this.typeHierarchyResolver = typeHierarchyResolver;
        this.parameterTypeInstances = new ArrayList<>();
    }

    public MethodStatement resolve(MethodCallExpression e, TypeStatement baseTopLevelParent,
            TypeStatement baseTypeStatement, List<ImportStatement> baseImports,
            Optional<PackageStatement> basePackageStatement, BlockStatement baseContext,
            TraversalHierarchy traversalHierarchy) {
        // Set context
        this.traversalHierarchy = traversalHierarchy;
        methodCall = e;

        // Set argument type instances
        parameterTypeInstances.clear();
        System.out.println("resolving args");

        for (Expression arg : methodCall.getArguments()) {
            TypeInstance t = new ExpressionTypeResolver(true).resolve(path, code, baseTopLevelParent, baseTypeStatement,
                    baseImports, basePackageStatement, baseContext, qualifiedNameResolver, traversalHierarchy,
                    typeHierarchyResolver, arg);
            parameterTypeInstances.add(t);
        }
        System.out.println("ArgumentTypes=" + parameterTypeInstances.stream()
                .map(t -> t == null ? "null" : t.getType()).collect(Collectors.toList()));

        // Resolve the method
        MethodStatement m;

        System.out.println("resolving method itself: " + methodCall.getMethodSource());

        if (methodCall.getMethodSource() != null) { // Resolve it potentially in another class
            System.out.println("method source != null");
            onVariable = true;
            TypeInstance source = methodCall.getMethodSource();

            if (source.getStatement() == null) // is an unresolved class, probably a part of the java api
                return null;

            System.out.println("method has source attached of name: " + source.getQualifiedName() + " type: " + source.getType());

            if ((m = resolveInTypeStatement(source.getStatement())) != null)
                return m;
            System.out.println("not in type statement");

            if ((m = resolveInSuperClasses(source.getStatement(), source.getCompilationUnitStatement(),
                    source.getCompilationUnitStatement().getPackageStatement(),
                    source.getCompilationUnitStatement().getImports())) != null)
                return m;
            System.out.println("not in super classes");
        } else { // Resolve in current context, then in current type statement, then in superclasses
            if ((m = resolveInBlock(baseContext)) != null)
                return m;
            System.out.println("not in block");

            if ((m = resolveCurrentTypeStatement()) != null)
                return m;
            System.out.println("not in type statement");

            if ((m = resolveInSuperClasses(baseTypeStatement, baseTopLevelParent, basePackageStatement,
                    baseImports)) != null)
                return m;
            System.out.println("not in super classes");
        }
        System.out.println("not found");
        return null;
    }

    public MethodStatement resolveOnVariable(MethodCallExpression e, TypeStatement baseTopLevelParent,
            TypeStatement baseTypeStatement, List<ImportStatement> baseImports,
            Optional<PackageStatement> basePackageStatement, BlockStatement baseContext,
            TraversalHierarchy traversalHierarchy) {
        onVariable = true;
        return resolve(e, baseTopLevelParent, baseTypeStatement, baseImports, basePackageStatement, baseContext,
                traversalHierarchy);
    }

    public MethodStatement resolve(MethodCallExpression e, TraversalHierarchy th) {
        return resolve(e, th.getFirstTypeStatement(), th.getLastTypeStatement(), th.getImports(),
                th.getPackageStatement(), th.currentContext(), th);
    }

    private MethodStatement resolveCurrentTypeStatement() {
        boolean firstOne = true;

        for (TypeStatement ts : traversalHierarchy.typeStatements()) {
            if (firstOne || !PostProcessUtils.isStaticTypeStatement(ts)) {
                MethodStatement method = resolveInBlock(PostProcessUtils.getBlock(ts));
                firstOne = false;

                if (method != null)
                    return method;
            }
        }
        return null;
    }

    private MethodStatement resolveInTypeStatement(TypeStatement typeStatement) {
        return resolveInBlock(PostProcessUtils.getBlock(typeStatement));
    }

    private MethodStatement resolveInSuperClasses(TypeStatement targetType, TypeStatement topLevelParent,
            Optional<PackageStatement> packageStatement, List<ImportStatement> imports) {
        for (String superClass : PostProcessUtils.superClasses(targetType)) {
            QualifiedType resolvedType = qualifiedNameResolver.resolve(code, path, targetType, topLevelParent,
                    packageStatement, imports, superClass);
            MethodStatement m = resolveInQualifiedType(resolvedType, topLevelParent);

            if (m != null)
                return m;
        }
        return null;
    }

    private MethodStatement resolveInQualifiedType(QualifiedType resolvedType, TypeStatement topLevelParent) {
        System.out.println("resolveInQualifiedType");

        if (resolvedType != null) {
            Statement resolvedStatement = resolvedType.getStatement();

            if (resolvedStatement != null) {
                TypeStatement typeStatement = resolvedType.getTopLevelStatement().getTypeStatement();
                List<ImportStatement> imports = resolvedType.getTopLevelStatement().getImports();
                Optional<PackageStatement> pkgStatement = resolvedType.getTopLevelStatement().getPackageStatement();
                MethodStatement m = resolveInTypeStatement(typeStatement);

                if (m != null)
                    return m;

                // Check super classes
                return resolveInSuperClasses(typeStatement, topLevelParent, pkgStatement, imports);
            }
        }
        return null;
    }

    private MethodStatement resolveInBlock(BlockStatement block) {
        List<Statement> statements = block.getStatements();

        if (statements.size() == 0)
            return null;
        String methodName = methodCall.getMethodIdentifier();
        boolean currentContextIsStatic = traversalHierarchy.isCurrentLocalContextStatic();

        return statements.stream()
                .filter(s -> s instanceof MethodStatement)
                .map(s -> (MethodStatement)s)
                .filter(method -> {
                    MethodDescriptor desc = method.getDescriptor();

                    boolean methodNameEquals = methodName.equals(desc.getIdentifierName().toString());
                    boolean argsEquals = parametersSignatureEquals(method.getParameters(), desc.getTypeParameters());
                    boolean accessibilityIsValid = onVariable
                            || !(currentContextIsStatic && !desc.getStaticModifier().get());
                    // TODO check visibility modifier and make sure accessibility is correct

                    return methodNameEquals && argsEquals && accessibilityIsValid;
                })
                .findFirst().orElse(null);
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

            if (!param1.getIdentifierType().isPresent() || qtype2 == null || qtype2.getType() == null)
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
            if (qtype1 != null) {
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
