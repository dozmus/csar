package org.qmul.csar.code.java.postprocess.util;

import org.qmul.csar.code.java.parse.expression.*;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.java.postprocess.methods.use.TraversalHierarchy;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;
import org.qmul.csar.lang.descriptors.ClassDescriptor;
import org.qmul.csar.lang.descriptors.VisibilityModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public class ExpressionTypeResolver {

    // TODO make sure path is always set as much as possible
    // TODO does this work with new Class(). ...

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionTypeResolver.class);
    private int nestedBinaryExpressionLevel = 0;
    /**
     * If this is true, the right-most value in a {@link BinaryExpression} with a type of {@link BinaryOperation#DOT}
     * will not be resolved.
     * This means {@link #resolve(Path, Map, TypeStatement, TypeStatement, List, Optional, BlockStatement,
     * QualifiedNameResolver, TraversalHierarchy, TypeHierarchyResolver, Expression)} will essentially return the type
     * the right-most value is acting upon.
     */
    private final boolean resolvingMethodIdentifierMode;
    private final Stack<MethodCallExpression> methodCallStack = new Stack<>();

    public ExpressionTypeResolver(boolean resolvingMethodIdentifierMode) {
        this.resolvingMethodIdentifierMode = resolvingMethodIdentifierMode;
    }

    public TypeInstance resolve(Path path, Map<Path, Statement> code, TypeStatement topLevelType,
            TypeStatement currentType, List<ImportStatement> imports, Optional<PackageStatement> currentPackage,
            BlockStatement currentContext, QualifiedNameResolver r, TraversalHierarchy th, TypeHierarchyResolver thr,
            Expression expression) {
        LOGGER.trace("Resolving: {} (class: {})", expression.toPseudoCode(), expression.getClass().getSimpleName());

        if (expression instanceof ArrayAccessExpression) {
            ArrayAccessExpression aaexp = (ArrayAccessExpression)expression;
            TypeInstance t = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r,
                    th, thr, aaexp.getArray());

            if (t != null)
                t.decrementDimension();
            return t;
        } else if (expression instanceof ArrayExpression) {
            ArrayExpression aexp = (ArrayExpression)expression;

            for (Expression e : aexp.getExpressions()) { // TODO does this handle nulls in the array properly? it should skip them
                TypeInstance t = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext,
                        r, th, thr, e);

                if (t != null) {
                    t.incrementDimension();
                    return t;
                }
            }
            return null; // unable to resolve
        } else if (expression instanceof ArrayInitializationExpression) {
            ArrayInitializationExpression aiexp = (ArrayInitializationExpression)expression;
            String identifierName = aiexp.getTypeName();
            int dimensions = arrayInitializerDimensions(aiexp.getExpressions());
            QualifiedType qt = r.resolve(code, path, currentType, topLevelType, currentPackage, imports, identifierName);
            return new TypeInstance(qt, dimensions);
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression bexp = (BinaryExpression)expression;
            nestedBinaryExpressionLevel++;
            TypeInstance instance = resolveBinaryExpression(path, code, topLevelType, currentType, imports,
                    currentPackage, currentContext, r, th, thr, bexp.getLeft(), bexp.getOp(), bexp.getRight());
            nestedBinaryExpressionLevel--;
            return instance;
        } else if (expression instanceof CastExpression) {
            CastExpression cexp = (CastExpression)expression;
            String apparentType = TypeHelper.removeDimensions(cexp.getApparentType());
            int dimensions = TypeHelper.dimensions(cexp.getApparentType());

            if (TypeHelper.isInbuiltType(apparentType))
                return new TypeInstance(apparentType, dimensions);

            QualifiedType qt = r.resolve(code, path, currentType, topLevelType, currentPackage, imports, apparentType);
            return new TypeInstance(qt, dimensions);
        } else if (expression instanceof InstantiateClassExpression) { // TODO check body?
            InstantiateClassExpression ins = (InstantiateClassExpression)expression;
            String qualifiedName = ins.getDescriptor().getIdentifierName().toString();
            QualifiedType qt = r.resolve(code, path, currentType, topLevelType, currentPackage, imports, qualifiedName);
            return new TypeInstance(qt, 0);
        } else if (expression instanceof LambdaExpression) {
            // TODO parse further when full java api support introduced
            return new TypeInstance("Supplier", 0);
        } else if (expression instanceof MethodCallExpression) {
            MethodCallExpression mce = (MethodCallExpression)expression;
            methodCallStack.push(mce);
            TypeInstance typeInstance = resolveMethodCallExpression(path, code, r, th, thr, mce);
            methodCallStack.pop();
            return typeInstance;
        } else if (expression instanceof ParenthesisExpression) {
            return resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r, th, thr,
                    ((ParenthesisExpression)expression).getExpression());
        } else if (expression instanceof PostfixedExpression) {
            return resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r, th, thr,
                    ((PostfixedExpression)expression).getExpression());
        } else if (expression instanceof PrefixedExpression) {
            return resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r, th, thr,
                    ((PrefixedExpression)expression).getExpression());
        } else if (expression instanceof TernaryExpression) {
            return resolveTernaryExpression(path, code, topLevelType, currentType, imports, currentPackage,
                    currentContext, r, th, thr, (TernaryExpression)expression);
        } else if (expression instanceof UnitExpression) {
            UnitExpression uexp = (UnitExpression)expression;
            QualifiedType qt;
            LOGGER.trace("Resolving: UnitExpression.Type={}", uexp.getValueType());

            switch (uexp.getValueType()) {
                case LITERAL:
                    return resolveLiteral(uexp.getValue());
                case IDENTIFIER:
                    return resolveIdentifier(path, code, topLevelType, currentType, imports, currentPackage,
                            currentContext, r, th, uexp);
                case CLASS_REFERENCE:
                    break;
                case METHOD_REFERENCE:
                    // if return void type then it's a Runnable, otherwise it may be a Predicate or a Function
                    break;
                case THIS:
                case THIS_CALL:
                    String className = PostProcessUtils.getIdentifierName(currentType);
                    qt = r.resolve(code, path, currentType, topLevelType, currentPackage, imports, className);
                    return new TypeInstance(qt, 0);
                case SUPER:
                case SUPER_CALL: // extended class
                    String superClass = null;

                    if (currentType instanceof ClassStatement) {
                        ClassStatement clazz = (ClassStatement) currentType;
                        ClassDescriptor desc = clazz.getDescriptor();

                        // 'super' in an inner non-static class is its parent class
                        if (desc.getInner().orElse(false) && !desc.getStaticModifier().orElse(false)) {
                            for (TypeStatement ts : th.typeStatements()) {
                                if (!ts.equals(clazz)) {
                                    superClass = PostProcessUtils.getIdentifierName(ts);
                                    break;
                                }
                            }
                        }
                    }

                    if (superClass == null) { // if not found, or otherwise, we just set it to its direct super class
                        superClass = PostProcessUtils.extendedClass(currentType);
                    }

                    if (superClass == null) { // XXX still not found, must be in the java api
                        return null;
                    }
                    qt = r.resolve(code, path, currentType, topLevelType, currentPackage, imports, superClass);
                    return new TypeInstance(qt, 0);
                case TYPE:
                    qt = r.resolve(code, path, topLevelType, topLevelType, currentPackage, imports, uexp.getValue());
                    return new TypeInstance(qt, 0);
                case NEW:
                    // not sure how this fits into things
                    break;
                case METHOD_CALL:
                    System.out.println("UnitExpression.ValueType=METHOD_CALL");
                    // resolve method?
                    break;
            }
        }
        return null; // fall-back: failed to resolve
    }

    private TypeInstance resolveIdentifier(Path path, Map<Path, Statement> code, TypeStatement topLevelType,
            TypeStatement currentType, List<ImportStatement> imports, Optional<PackageStatement> currentPackage,
            BlockStatement currentContext, QualifiedNameResolver r, TraversalHierarchy th, UnitExpression uexp) {
        String lIdentifierName = uexp.getValue();
        int dimensions = 0;
        String lType = null;
        LOGGER.trace("Resolving: Identifier={}", lIdentifierName);

        // ... in local context
        for (ParameterVariableStatement parameter : th.currentContextParameters()) { // parameters
            if (lType != null)
                break;

            String paramIdentifierName = parameter.getDescriptor().getIdentifierName().get().toString();
            String paramIdentifierType = parameter.getDescriptor().getIdentifierType().get();

            if (paramIdentifierName.equals(lIdentifierName)) {
                lType = paramIdentifierType;
                dimensions = TypeHelper.dimensions(lType);
            }
        }

        for (LocalVariableStatements locals : th.currentContextLocalVariables()) { // locals, includes for-loops etc.
            if (lType != null)
                break;

            for (LocalVariableStatement local : locals.getLocals()) {
                if (lType != null)
                    break;

                String localIdentifierName = local.getDescriptor().getIdentifierName().toString();
                String localIdentifierType = local.getDescriptor().getIdentifierType().get();

                if (localIdentifierName.equals(lIdentifierName)) {
                    lType = localIdentifierType;
                    dimensions = TypeHelper.dimensions(lType);
                }
            }
        }

        // ... in current class
        boolean firstOne = true;

        for (TypeStatement ts : th.typeStatements()) { // local class, and then parent ones if applicable
            if (firstOne || !PostProcessUtils.isStaticTypeStatement(ts)) {
                firstOne = false;

                for (Statement st : PostProcessUtils.getBlock(ts).getStatements()) {
                    if (lType != null)
                        break;

                    if (st instanceof InstanceVariableStatement) {
                        InstanceVariableStatement instance = (InstanceVariableStatement)st;
                        String instanceIdentifierName = instance.getDescriptor().getIdentifierName().toString();
                        String instanceIdentifierType = instance.getDescriptor().getIdentifierType().get();

                        if (instanceIdentifierName.equals(lIdentifierName)) {
                            lType = instanceIdentifierType;
                            dimensions = TypeHelper.dimensions(lType);
                        }
                    }
                }
            }
        }

        // ... in super classes of current class
        for (String superClass : PostProcessUtils.superClasses(currentType)) {
            if (lType != null)
                break;
            QualifiedType resolvedType = r.resolve(code, path, currentType, topLevelType, currentPackage, imports,
                    superClass);

            if (resolvedType.getStatement() instanceof CompilationUnitStatement) {
                CompilationUnitStatement cus = (CompilationUnitStatement)resolvedType.getStatement();

                for (Statement st : PostProcessUtils.getBlock(cus.getTypeStatement()).getStatements()) {
                    if (lType != null)
                        break;

                    if (st instanceof InstanceVariableStatement) {
                        InstanceVariableStatement instance = (InstanceVariableStatement)st;
                        String instanceIdentifierName = instance.getDescriptor().getIdentifierName().toString();
                        String instanceIdentifierType = instance.getDescriptor().getIdentifierType().get();
                        VisibilityModifier visibilityModifier = instance.getDescriptor().getVisibilityModifier().get();
                        boolean isAccessible = PostProcessUtils.isAccessible(visibilityModifier, true,
                                cus.getPackageStatement(), currentPackage);

                        if (isAccessible && instanceIdentifierName.equals(lIdentifierName)) {
                            lType = instanceIdentifierType;
                            dimensions = TypeHelper.dimensions(lType);
                        }
                    }
                }
            }
        }

        QualifiedType lQualifiedType;

        if (lType == null) { // it may be a type, let's try resolving its identifier directly
            lQualifiedType = r.resolve(code, path, currentType, topLevelType, currentPackage, imports, lIdentifierName,
                    true);
        } else {
            lQualifiedType = r.resolve(code, path, currentType, topLevelType, currentPackage, imports, lType);
        }

        if (lQualifiedType == null) // may be external - we ignore it
            return null;
        return new TypeInstance(lQualifiedType, dimensions);
    }

    private TypeInstance resolveMethodCallExpression(Path path, Map<Path, Statement> code,
            QualifiedNameResolver r, TraversalHierarchy th, TypeHierarchyResolver thr,
            MethodCallExpression expression) {
        LOGGER.trace("Resolving: MethodCallExpression");
        MethodResolver resolver = new MethodResolver(path, code, r, thr);
        MethodStatement method = resolver.resolve(expression, th);

        if (method != null) {
            int dimensions = TypeHelper.dimensions(method.getDescriptor().getReturnType().get());
            return new TypeInstance(method.getReturnQualifiedType(), dimensions);
        }
        return null;
    }

    private static int arrayInitializerDimensions(List<Expression> expressions) { // TODO is this ok?
        return (int) expressions.stream().filter(e -> e instanceof SquareBracketsExpression).count();
    }

    private TypeInstance resolveBinaryExpression(Path path, Map<Path, Statement> code,
            TypeStatement topLevelType, TypeStatement currentType, List<ImportStatement> imports,
            Optional<PackageStatement> currentPackage, BlockStatement currentContext, QualifiedNameResolver r,
            TraversalHierarchy th, TypeHierarchyResolver thr, Expression left, BinaryOperation op, Expression right) {
        LOGGER.trace("Resolving: BinaryExpression (OP={})", op);

        if (op.isArithmeticOperation()) {
            TypeInstance lhs = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext,
                    r, th, thr, left);
            TypeInstance rhs = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext,
                    r, th, thr, right);
            if (lhs == null || rhs == null)
                return null;
            String lhType = lhs.getQualifiedName();
            String rhType = rhs.getQualifiedName();

            if (lhs.getQualifiedName().equals("float") || lhs.getQualifiedName().equals("double")) {
                lhType = "double";
            } else if (lhs.getQualifiedName().equals("byte") || lhs.getQualifiedName().equals("short")
                    || lhs.getQualifiedName().equals("int")) {
                lhType = "int";
            }

            if (rhs.getQualifiedName().equals("float") || rhs.getQualifiedName().equals("double")) {
                rhType = "double";
            } else if (rhs.getQualifiedName().equals("byte") || rhs.getQualifiedName().equals("short")
                    || rhs.getQualifiedName().equals("int")) {
                rhType = "int";
            }

            if (lhType.equals(rhType)) {
                return lhs;
            } else if (lhType.equals("String")) {
                return lhs;
            } else if (rhType.equals("String")) {
                return rhs;
            } else if (lhType.equals("int") && rhType.equals("long")) {
                return rhs;
            } else if (lhType.equals("long") && rhType.equals("int")) {
                return lhs;
            }
        } else if (op.isArithmeticAssignOperation()) { // return the type of the LHS (the variable)
            return resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext,
                    r, th, thr, left);
        } else if (op.equals(BinaryOperation.DOT)) {
            TypeInstance lhs = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext,
                    r, th, thr, left);

            if (lhs != null) {
                TypeInstance out = resolveBinaryExpressionDotRhs(lhs, path, code, r, right, thr, th, true);

                if (out != null)
                    return out;
                LOGGER.trace("LHS was found: {} {}", lhs.getType(), lhs.getQualifiedName());
            }

            // fall-back: current target was not found so it might be a fully qualified name
            lhs = resolveFullyQualifiedName(code, r, left);

            if (lhs == null)
                return null;

            LOGGER.trace("LHS included a fully qualified name: {} {}", lhs.getType(), lhs.getQualifiedName());
            return resolveBinaryExpressionDotRhs(lhs, path, code, r, right, thr, th, true);
        } else if (op.isBooleanOperation()) {
            return literalType("boolean");
        }
        return null; // XXX wont get here since BinaryOperation.QUESTION is unused outside of JavaCodeParser
    }

    private TypeInstance resolveFullyQualifiedName(Map<Path, Statement> code, QualifiedNameResolver r,
            Expression left) {
        String fullyQualifiedName = binaryExpressionIdentifierDotSequenceToString(left);

        if (fullyQualifiedName == null || !fullyQualifiedName.contains("."))
            return null;
        QualifiedType qt = r.resolveFullyQualifiedName(code, fullyQualifiedName);

        if (qt != null) {
            LOGGER.trace("Resolve fully qualified name: " + fullyQualifiedName + " => " + qt.getQualifiedName());
            return new TypeInstance(qt, 0);
        }
        return null;
    }

    private String binaryExpressionIdentifierDotSequenceToString(Expression e) {
        if (e instanceof UnitExpression) {
            UnitExpression ue = (UnitExpression)e;
            return ue.getValueType() == UnitExpression.ValueType.IDENTIFIER ? ue.getValue() : null;
        } else if (e instanceof BinaryExpression) {
            BinaryExpression bexp = (BinaryExpression)e;
            return binaryExpressionIdentifierDotSequenceToString(bexp.getLeft())
                    + "." + binaryExpressionIdentifierDotSequenceToString(bexp.getRight());
        } else {
            return null;
        }
    }

    private TypeInstance resolveBinaryExpressionDotRhs(TypeInstance lhs, Path path, Map<Path, Statement> code,
            QualifiedNameResolver r, Expression right, TypeHierarchyResolver thr, TraversalHierarchy th,
            boolean onVariable) {
        LOGGER.trace("resolveBinaryExpressionDotRhs [right.class={}]", right.getClass().getSimpleName());

        if (lhs == null)
            return null;

        UnitExpression rue = (UnitExpression)right;
        CompilationUnitStatement lCompilationUnitStatement = lhs.getCompilationUnitStatement();

        if (nestedBinaryExpressionLevel == 1) {
            if (resolvingMethodIdentifierMode) {
                return lhs; // XXX rhs is the identifier of a method call, we handle it elsewhere
            } else if (methodCallStack.empty()) { // is another identifier
                LOGGER.trace("resolveBinaryExpressionDotRhs: methodCallStack.empty()");

                if (lCompilationUnitStatement == null) // may be external - we ignore it
                    return null;
                String rType = resolveBinaryExpressionDotRhsIdentifierType(lCompilationUnitStatement, rue.getValue(), r,
                        code, path, false, lCompilationUnitStatement.getPackageStatement());
                LOGGER.trace("resolveBinaryExpressionDotRhs: rType={}", rType);

                if (rType == null) // may be external - we ignore it
                    return null;
                int dimensions = TypeHelper.dimensions(rType);
                String typeName = TypeHelper.removeDimensions(rType);
                QualifiedType qt = r.resolve(code, path, lCompilationUnitStatement.getTypeStatement(),
                        lCompilationUnitStatement, lCompilationUnitStatement.getPackageStatement(),
                        lCompilationUnitStatement.getImports(), typeName);
                return new TypeInstance(qt, dimensions);
            } else { // is a method
                LOGGER.trace("resolveBinaryExpressionDotRhs: Resolving method properly: {}",
                        methodCallStack.peek().toPseudoCode());

                if (lCompilationUnitStatement == null) // may be external - we ignore it
                    return null;
                MethodResolver methodResolver = new MethodResolver(lhs.getPath(), code, r, thr);
                MethodStatement method;

                if (onVariable) {
                    method = methodResolver.resolveOnVariable(methodCallStack.peek(),
                            lCompilationUnitStatement, lCompilationUnitStatement.getTypeStatement(),
                            lCompilationUnitStatement.getImports(), lCompilationUnitStatement.getPackageStatement(),
                            th.currentContext(), th);
                } else {
                    method = methodResolver.resolve(methodCallStack.peek(),
                            lCompilationUnitStatement, lCompilationUnitStatement.getTypeStatement(),
                            lCompilationUnitStatement.getImports(), lCompilationUnitStatement.getPackageStatement(),
                            th.currentContext(), th);
                }

                if (method != null) {
                    int dimensions = TypeHelper.dimensions(method.getDescriptor().getReturnType().get());
                    return new TypeInstance(method.getReturnQualifiedType(), dimensions);
                }
                return null;
            }
        } else { // is another identifier
            if (lCompilationUnitStatement == null) // may be external - we ignore it
                return null;
            String rType = resolveBinaryExpressionDotRhsIdentifierType(lCompilationUnitStatement, rue.getValue(), r,
                    code, path, false, lCompilationUnitStatement.getPackageStatement());
            LOGGER.trace("resolveBinaryExpressionDotRhs: rType={}", rType);

            if (rType == null) // may be external - we ignore it
                return null;
            int dimensions = TypeHelper.dimensions(rType);
            String typeName = TypeHelper.removeDimensions(rType);
            QualifiedType qt = r.resolve(code, path, lCompilationUnitStatement.getTypeStatement(),
                    lCompilationUnitStatement, lCompilationUnitStatement.getPackageStatement(),
                    lCompilationUnitStatement.getImports(), typeName);
            return new TypeInstance(qt, dimensions);
        }
    }

    private String resolveBinaryExpressionDotRhsIdentifierType(CompilationUnitStatement topLevelParent,
            String identifierName, QualifiedNameResolver r, Map<Path, Statement> code, Path path,
            boolean checkingSuper, Optional<PackageStatement> baseCallPkg) {
        // Check target
        for (Statement st : PostProcessUtils.getBlock(topLevelParent.getTypeStatement()).getStatements()) {
            if (st instanceof InstanceVariableStatement) {
                InstanceVariableStatement instance = (InstanceVariableStatement)st;
                String instanceIdentifierName = instance.getDescriptor().getIdentifierName().toString();
                String instanceIdentifierType = instance.getDescriptor().getIdentifierType().get();
                VisibilityModifier visibilityModifier = instance.getDescriptor().getVisibilityModifier().get();
                boolean isAccessible = !checkingSuper || PostProcessUtils.isAccessible(visibilityModifier, true,
                        topLevelParent.getPackageStatement(), baseCallPkg);

                if (isAccessible && instanceIdentifierName.equals(identifierName))
                    return instanceIdentifierType;
            }
        }

        // Check its super classes
        for (String superClass : PostProcessUtils.superClasses(topLevelParent.getTypeStatement())) {
            QualifiedType resolvedType = r.resolve(code, path, topLevelParent.getTypeStatement(), topLevelParent,
                    topLevelParent.getPackageStatement(), topLevelParent.getImports(), superClass);

            if (resolvedType != null && resolvedType.getTopLevelStatement() != null) {
                String type = resolveBinaryExpressionDotRhsIdentifierType(resolvedType.getTopLevelStatement(),
                        identifierName, r, code, path, true, baseCallPkg);

                if (type != null)
                    return type;
            }
        }
        return null;
    }

    private TypeInstance resolveTernaryExpression(Path path, Map<Path, Statement> code, TypeStatement topLevelType,
            TypeStatement currentType, List<ImportStatement> imports, Optional<PackageStatement> currentPackage,
            BlockStatement currentContext, QualifiedNameResolver r, TraversalHierarchy th, TypeHierarchyResolver thr,
            TernaryExpression texp) {
        TypeInstance t1 = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r,
                th, thr, texp.getValueIfTrue());
        TypeInstance t2 = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r,
                th, thr, texp.getValueIfFalse());

        if (t1 != null && t2 != null) {
            if (t1.getQualifiedName().equals("null") && t2.getQualifiedName().equals("null")) {
                return literalType("java.lang.Object");
            } else if (!t1.getQualifiedName().equals("null") && t2.getQualifiedName().equals("null")) {
                return t1;
            } else if (t1.getQualifiedName().equals("null") && !t2.getQualifiedName().equals("null")) {
                return t2;
            } else {
                return t1;
            }
        } else if (t1 != null) {
            if (t1.getQualifiedName().equals("null")) {
                return literalType("java.lang.Object");
            }
            return t1;
        } else if (t2 != null) {
            if (t2.getQualifiedName().equals("null")) {
                return literalType("java.lang.Object");
            }
            return t2;
        }
        return null; // invalid t1 or t2
    }

    private static TypeInstance resolveLiteral(String value) {
        if (value.equals("null"))
            return literalType("java.lang.Object");
        if (value.startsWith("\""))
            return literalType("String");
        if (value.startsWith("\'"))
            return literalType("char");
        if (value.equals("true") || value.equals("false"))
            return literalType("boolean");

        // Double
        if (value.endsWith("d") || value.endsWith("D") || value.contains("."))
            return literalType("double");

        // Float
        if (value.endsWith("f") || value.endsWith("F"))
            return literalType("float");

        // Fall-back: has to be integer
        return literalType("int");
    }

    private static TypeInstance literalType(String type) { // TODO update when java api is fully supported
        return new TypeInstance(type, 0);
    }
}
