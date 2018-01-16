package org.qmul.csar.code.java.postprocess.methodusage;

import org.qmul.csar.code.java.parse.expression.BinaryExpression;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.expression.UnitExpression;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.java.postprocess.PostProcessUtils;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptor.MethodDescriptor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MethodCallResolver {

    private final QualifiedNameResolver qualifiedNameResolver;
    private final Path path;
    private final Map<Path, Statement> code;
    private final TraversalHierarchy traversalHierarchy;
    private TypeStatement topLevelParent;
    private TypeStatement targetType;
    private List<ImportStatement> imports;
    private Optional<PackageStatement> packageStatement;
    private BlockStatement currentContext;

    public MethodCallResolver(Path path, Map<Path, Statement> code, TraversalHierarchy traversalHierarchy,
            QualifiedNameResolver qualifiedNameResolver) {
        this.path = path;
        this.code = code;
        this.traversalHierarchy = traversalHierarchy;
        this.qualifiedNameResolver = qualifiedNameResolver;
    }

    public void resolve(MethodCallExpression e, TraversalHierarchy traversalHierarchy) {
        // Set context
        topLevelParent = traversalHierarchy.getFirstTypeStatement();
        targetType = traversalHierarchy.getLastTypeStatement();
        imports = traversalHierarchy.getImports();
        packageStatement = traversalHierarchy.getPackageStatement();
        currentContext = traversalHierarchy.currentContext();

        // Resolve
        resolve(e, true);
    }

    private TopLevelTypeStatement resolve(MethodCallExpression expression, boolean addToUsages) {
        Expression methodNameExpression = expression.getMethodName();
        System.out.println("type(methodNameExpression) = " + methodNameExpression.getClass().getName());

        // Found method
        if (methodNameExpression instanceof UnitExpression) { // simple method name
            UnitExpression methodNameUnit = (UnitExpression)methodNameExpression;
            System.out.println("value-type:" + methodNameUnit.getValueType());

            if (methodNameUnit.getValueType() == UnitExpression.ValueType.IDENTIFIER) {
                String methodName = methodNameUnit.getValue();
                MethodStatement method = resolveInContext(currentContext, targetType, topLevelParent, packageStatement,
                        imports, methodName, expression, false, addToUsages);

                if (method == null) { // may be external - we ignore it
                    return null;
                }
                QualifiedType qualifiedType = qualifiedNameResolver.resolve(code, path, targetType, topLevelParent,
                        packageStatement, imports, method.getDescriptor().getReturnType().get());

                if (qualifiedType == null)
                    return null;
                return qualifiedType.getStatement() instanceof TopLevelTypeStatement
                        ? (TopLevelTypeStatement)qualifiedType.getStatement()
                        : null;
            }
        } else if (methodNameExpression instanceof BinaryExpression) { // complex method name
            BinaryExpression bexp = (BinaryExpression)methodNameExpression;
            QualifiedType qualifiedType = resolveInBinaryExpression(bexp, expression, 0, addToUsages);
            return qualifiedType == null ? null : (TopLevelTypeStatement)qualifiedType.getStatement();
        }
        // XXX unable to resolve
        return null;
    }

    private QualifiedType resolveInBinaryExpression(BinaryExpression expression,
            MethodCallExpression methodCallExpression, int k, boolean addToUsages) {
        // NOTE we always resolve the lhs first, since the java parser is left-recursive in expression-related rules
        Expression l = expression.getLeft();
        Expression r = expression.getRight();
        System.out.println("[RBE] Resolve [l=" + l.getClass().getSimpleName() + ", r=" + r.getClass().getSimpleName() + "]");

        if (l instanceof BinaryExpression && r instanceof UnitExpression) { // step case
            System.out.println("[RBE] Handled");
            QualifiedType type = resolveInBinaryExpression((BinaryExpression)l, methodCallExpression, k + 1,
                    addToUsages);

            if (type == null) { // may be external - we ignore it
                return null;
            }
            TopLevelTypeStatement topLevelTypeStatement = (TopLevelTypeStatement)type.getStatement();
            UnitExpression rue = (UnitExpression)r;

            if (rue.getValueType() == UnitExpression.ValueType.IDENTIFIER) {
                String methodName = rue.getValue();

                if (topLevelTypeStatement == null)
                    return null;

                resolveInContext(currentContext, topLevelTypeStatement.getTypeStatement(), topLevelTypeStatement,
                        topLevelTypeStatement.getPackageStatement(), topLevelTypeStatement.getImports(),
                        methodName, methodCallExpression, true, addToUsages);
            }
        } else if (l instanceof UnitExpression && r instanceof UnitExpression) { // base case (for supported BinExprs)
            System.out.println("[RBE] Handled");
            UnitExpression lue = (UnitExpression)l;
            UnitExpression rue = (UnitExpression)r;
            TopLevelTypeStatement lTopLevelTypeStatement = null;
            System.out.println("[RBE] LUnit=" + lue.getValueType() + ", RUnit=" + rue.getValueType() + " [k=" + k + "]");

            // l identifier/this/super and r identifiers
            if (lue.getValueType() == UnitExpression.ValueType.IDENTIFIER) {
                String lIdentifierName = lue.getValue();
                String lType = null;

                // Resolve types of identifiers
                // firstly l
                // ... in local context
                for (ParameterVariableStatement parameter : traversalHierarchy.currentContextParameters()) {
                    if (lType != null)
                        break;

                    String localIdentifierName = parameter.getDescriptor().getIdentifierName().get().toString();
                    String localIdentifierType = parameter.getDescriptor().getIdentifierType().get();
                    System.out.println("(param) " + localIdentifierName + " type=" + localIdentifierType);

                    if (localIdentifierName.equals(lIdentifierName))
                        lType = localIdentifierType;
                }

                for (Statement st : currentContext.getStatements()) {
                    // TODO fix correctness (only accept locals before the current line, ones in for loops, etc. too)
                    if (lType != null)
                        break;

                    if (st instanceof LocalVariableStatements) {
                        LocalVariableStatements locals = (LocalVariableStatements)st;

                        for (LocalVariableStatement local : locals.getLocals()) {
                            if (lType != null)
                                break;

                            String localIdentifierName = local.getDescriptor().getIdentifierName().toString();
                            String localIdentifierType = local.getDescriptor().getIdentifierType().get();

                            if (localIdentifierName.equals(lIdentifierName))
                                lType = localIdentifierType;
                        }
                    }
                }

                // ... in current class
                if (lType == null) {
                    for (Statement st : getTargetTypeBlock().getStatements()) {
                        if (lType != null)
                            break;

                        if (st instanceof InstanceVariableStatement) {
                            InstanceVariableStatement instance = (InstanceVariableStatement)st;
                            String instanceIdentifierName = instance.getDescriptor().getIdentifierName().toString();
                            String instanceIdentifierType = instance.getDescriptor().getIdentifierType().get();

                            if (instanceIdentifierName.equals(lIdentifierName))
                                lType = instanceIdentifierType;
                        }
                    }
                }

                // ... in super classes of current class
                if (lType == null) {
                    for (String superClass : PostProcessUtils.superClasses(targetType)) {
                        if (lType != null)
                            break;
                        QualifiedType resolvedType = qualifiedNameResolver.resolve(code, path, targetType,
                                topLevelParent, packageStatement, imports, superClass);

                        if (resolvedType.getStatement() instanceof TopLevelTypeStatement) {
                            for (Statement st
                                    : getBlock((TopLevelTypeStatement)resolvedType.getStatement()).getStatements()) {
                                if (lType != null)
                                    break;

                                // TODO check visibility
                                if (st instanceof InstanceVariableStatement) {
                                    InstanceVariableStatement instance = (InstanceVariableStatement)st;
                                    String instanceIdentifierName = instance.getDescriptor().getIdentifierName().toString();
                                    String instanceIdentifierType = instance.getDescriptor().getIdentifierType().get();

                                    if (instanceIdentifierName.equals(lIdentifierName))
                                        lType = instanceIdentifierType;
                                }
                            }
                        }
                    }
                }
                System.out.println("[RBE] lType = " + lType);

                if (lType == null) // may be external - we ignore it
                    return null;

                QualifiedType lQualifiedType = qualifiedNameResolver.resolve(code, path, targetType, topLevelParent,
                        packageStatement, imports, lType);
                if (!(lQualifiedType.getStatement() instanceof TopLevelTypeStatement)) // may be external - we ignore it
                    return null;
                lTopLevelTypeStatement = (TopLevelTypeStatement)lQualifiedType.getStatement();
                System.out.println("[RBE] Resolved lType");
            } else if (lue.getValueType() == UnitExpression.ValueType.SUPER) {
                String superClass = PostProcessUtils.extendedClass(targetType);
                String lType = superClass;
                QualifiedType lQualifiedType = qualifiedNameResolver.resolve(code, path, targetType,
                        topLevelParent, packageStatement, imports, superClass);
                System.out.println("[RBE] lType = " + lType);

                if (lType == null) // may be external - we ignore it
                    return null;
                lTopLevelTypeStatement = (TopLevelTypeStatement)lQualifiedType.getStatement();
                System.out.println("[RBE] Resolved lType");
                return resolveForRHSOfBinaryExpression(rue, lTopLevelTypeStatement, methodCallExpression, k,
                        addToUsages);
            } else if (lue.getValueType() == UnitExpression.ValueType.THIS) {
                lTopLevelTypeStatement = (TopLevelTypeStatement)topLevelParent;
            } else {
                System.out.println("[RBE] Unhandled unit expression value type");
                return null; // XXX unable to resolve
            }

            // secondly, r
            // we check the supers of l here too
            QualifiedType qt = null;

            while ((qt = resolveForRHSOfBinaryExpression(rue, lTopLevelTypeStatement, methodCallExpression, k,
                    addToUsages)) == null) {
                // find super class
                String superClass = PostProcessUtils.extendedClass(lTopLevelTypeStatement.getTypeStatement());
                String lType = superClass;
                System.out.println("[RBE] lType = " + lType);

                if (superClass == null) // may be external - we ignore it
                    return null;

                // try to resolve
                QualifiedType lQualifiedType = qualifiedNameResolver.resolve(code, path, targetType, topLevelParent,
                        packageStatement, imports, superClass);

                // check result
                lTopLevelTypeStatement = (TopLevelTypeStatement)lQualifiedType.getStatement();

                if (lTopLevelTypeStatement == null)
                    return null;
            }
            return qt;
        } else if (l instanceof MethodCallExpression && r instanceof UnitExpression) {
            System.out.println("[RBE] Handled");
            TopLevelTypeStatement lTopLevelTypeStatement = resolve((MethodCallExpression)l, false);
            UnitExpression rue = (UnitExpression)r;

            // secondly, r
            return resolveForRHSOfBinaryExpression(rue, lTopLevelTypeStatement, methodCallExpression, k, addToUsages);
        }

        // XXX unable to resolve
        return null;
    }

    private QualifiedType resolveForRHSOfBinaryExpression(UnitExpression rue,
            TopLevelTypeStatement lTopLevelTypeStatement, MethodCallExpression methodCallExpression, int k,
            boolean addToUsages) {
        System.out.println("[RBE-RHS] run");

        if (k == 0) { // is method call
            if (rue.getValueType() == UnitExpression.ValueType.IDENTIFIER) {
                String methodName = rue.getValue();
                System.out.println("[RBE-RHS] rMethod=" + methodName);

                if (lTopLevelTypeStatement == null) { // may be external - we ignore it
                    return null;
                }
                resolveInContext(currentContext, lTopLevelTypeStatement.getTypeStatement(),
                        lTopLevelTypeStatement, lTopLevelTypeStatement.getPackageStatement(),
                        lTopLevelTypeStatement.getImports(), methodName, methodCallExpression, true, addToUsages);
                return new QualifiedType(null, null); // so that resolving super or supers doesnt make duplicates adds
            }
        } else { // is identifier
            String rIdentifierName = rue.getValue();
            String rType = null;

            for (Statement st : getBlock(lTopLevelTypeStatement).getStatements()) {
                // TODO check visibility

                if (st instanceof InstanceVariableStatement) {
                    if (rType != null)
                        break;

                    InstanceVariableStatement instance = (InstanceVariableStatement)st;
                    String instanceIdentifierName = instance.getDescriptor().getIdentifierName().toString();
                    String instanceIdentifierType = instance.getDescriptor().getIdentifierType().get();

                    if (instanceIdentifierName.equals(rIdentifierName))
                        rType = instanceIdentifierType;
                }
            }
            System.out.println("[RBE-RHS] rType = " + rType);

            if (rType == null) // may be external - we ignore it
                return null;
            return qualifiedNameResolver.resolve(code, path,
                    lTopLevelTypeStatement.getTypeStatement(), lTopLevelTypeStatement,
                    lTopLevelTypeStatement.getPackageStatement(), lTopLevelTypeStatement.getImports(), rType);
        }
        return null;
    }

    public MethodStatement resolveInContext(BlockStatement currentContext, TypeStatement targetType,
            TypeStatement topLevelParent, Optional<PackageStatement> packageStatement, List<ImportStatement> imports,
            String methodName, MethodCallExpression expression, boolean onVariable, boolean addToUsages) {
        System.out.println("resolveInContext");
        MethodStatement method = null;
        List<Expression> args = expression.getArguments();

        // Resolve in current (local) context
        method = resolveInBlock(currentContext, methodName, args, expression, onVariable, addToUsages);

        if (method != null)
            return method;

        // Resolve in current type statement
        method = resolveInTypeStatement(targetType, methodName, args, expression, onVariable, addToUsages);

        if (method != null)
            return method;

        // Resolve in super classes
        return resolveInSuperClasses(targetType, topLevelParent, packageStatement, imports, methodName, args,
                expression, onVariable, addToUsages);
    }

    private MethodStatement resolveInSuperClasses(TypeStatement targetType, TypeStatement topLevelParent,
            Optional<PackageStatement> packageStatement, List<ImportStatement> imports, String methodName,
            List<Expression> args, MethodCallExpression expression, boolean onVariable, boolean addToUsages) {
        for (String superClass : PostProcessUtils.superClasses(targetType)) {
            QualifiedType resolvedType = qualifiedNameResolver.resolve(code, path, targetType,
                    topLevelParent, packageStatement, imports, superClass);

            MethodStatement method = resolveInQualifiedType(resolvedType, methodName, args, expression, topLevelParent,
                    onVariable, addToUsages);

            if (method != null)
                return method;
        }
        return null;
    }

    private MethodStatement resolveInQualifiedType(QualifiedType resolvedType, String methodName,
            List<Expression> args, MethodCallExpression expression, TypeStatement topLevelParent, boolean onVariable,
            boolean addToUsages) {
        MethodStatement method = null;
        Statement resolvedStatement = resolvedType.getStatement();

        if (resolvedStatement != null && resolvedStatement instanceof TopLevelTypeStatement) {
            TypeStatement typeStatement = ((TopLevelTypeStatement)resolvedStatement).getTypeStatement();
            List<ImportStatement> imports = ((TopLevelTypeStatement)resolvedStatement).getImports();
            Optional<PackageStatement> pkgStatement = ((TopLevelTypeStatement)resolvedStatement).getPackageStatement();

            method = resolveInTypeStatement(typeStatement, methodName, args, expression, onVariable, addToUsages);

            if (method != null)
                return method;

            // Check super classes of super
            method = resolveInSuperClasses(typeStatement, topLevelParent, pkgStatement, imports, methodName, args,
                    expression, onVariable, addToUsages);
            return method;
        }
        return null;
    }

    private MethodStatement resolveInTypeStatement(TypeStatement targetType, String methodName, List<Expression> args,
            MethodCallExpression expression, boolean onVariable, boolean addToUsages) {
        return resolveInBlock(getBlock(targetType), methodName, args, expression, onVariable, addToUsages);
    }

    private BlockStatement getTargetTypeBlock() {
        return getBlock(targetType);
    }

    private BlockStatement getBlock(TopLevelTypeStatement statement) {
        return statement == null ? new BlockStatement(new ArrayList<>()) : getBlock(statement.getTypeStatement());
    }

    private BlockStatement getBlock(TypeStatement type) {
        if (type instanceof ClassStatement) {
            return ((ClassStatement)type).getBlock();
        } else if (type instanceof EnumStatement) {
            return ((EnumStatement)type).getBlock();
        } else { // fall-back: annotation
            return ((AnnotationStatement)type).getBlock();
        }
    }

    private MethodStatement resolveInBlock(BlockStatement block, String methodName, List<Expression> args,
            MethodCallExpression methodCallExpression, boolean onVariable, boolean addToUsages) {
        System.out.println("resolveInBlock");
        List<Statement> statements = block.getStatements();

        if (statements.size() == 0)
            return null;

        boolean currentContextIsStatic = traversalHierarchy.isCurrentContextStatic();

        for (Statement statement : statements) {
            if (statement instanceof MethodStatement) {
                MethodStatement method = (MethodStatement)statement;
                MethodDescriptor desc = method.getDescriptor();

                boolean methodNameEquals = methodName.equals(desc.getIdentifierName().toString());
                boolean argsCountEquals = desc.getParameterCount().get() == args.size();
                boolean accessibilityIsValid = onVariable
                        || !(currentContextIsStatic && !desc.getStaticModifier().get());
                System.out.println("methodName vs. " + desc.getIdentifierName() + " | valid=" + accessibilityIsValid);
                // TODO check visibility modifier
                // TODO check method args types

                if (methodNameEquals && argsCountEquals && accessibilityIsValid) {
                    System.out.println("accepted.");

                    if (addToUsages)
                        addMethodUsage(method, methodCallExpression);
                    return method;
                }
            }
        }
        return null;
    }

    private void addMethodUsage(MethodStatement method, MethodCallExpression methodCallExpression) {
        method.getMethodUsages().add(methodCallExpression);
    }
}
