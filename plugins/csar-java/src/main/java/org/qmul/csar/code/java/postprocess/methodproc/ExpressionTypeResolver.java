package org.qmul.csar.code.java.postprocess.methodproc;

import org.qmul.csar.code.java.parse.expression.*;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.code.java.postprocess.TypeHelper;
import org.qmul.csar.code.java.postprocess.methodusage.TraversalHierarchy;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedType;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExpressionTypeResolver {

    // TODO make sure path is always set as much as possible

    public static TypeInstance resolve(Path path, Map<Path, Statement> code, TypeStatement topLevelType,
            TypeStatement currentType, List<ImportStatement> imports, Optional<PackageStatement> currentPackage,
            BlockStatement currentContext, QualifiedNameResolver r, TraversalHierarchy th, Expression expression) {
        if (expression instanceof ArrayAccessExpression) {
            ArrayAccessExpression aaexp = (ArrayAccessExpression)expression;
            TypeInstance t = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r,
                    th, aaexp);

            if (t != null)
                t.incrementDimension();
            return t;
        } else if (expression instanceof ArrayExpression) {
            ArrayExpression aexp = (ArrayExpression)expression;
            TypeInstance t = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r,
                    th, aexp.getExpressions().get(0)); // might break if type is unable to be resolved from idx=0, try incrementing

            if (t != null)
                t.decrementDimension();
            return t;
        } else if (expression instanceof ArrayInitializationExpression) {
            ArrayInitializationExpression aiexp = (ArrayInitializationExpression)expression;
            String identifierName = aiexp.getTypeName();
            int dimensions = arrayInitializerDimensions(aiexp.getExpressions());
            QualifiedType qt = r.resolve(code, path, currentType, topLevelType, currentPackage, imports, identifierName);
            return new TypeInstance(qt, dimensions);
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression bexp = (BinaryExpression)expression;
            TypeInstance lhs = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext,
                    r, th, bexp.getLeft());
            BinaryOperation op = bexp.getOp();
            TypeInstance rhs = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext,
                    r, th, bexp.getRight());
            return resolveBinaryExpression(topLevelType, currentType, imports, currentPackage, currentContext, r, th,
                    lhs, op, rhs);
        } else if (expression instanceof CastExpression) {
            CastExpression cexp = (CastExpression)expression;
            String apparentType = TypeHelper.removeDimensions(cexp.getApparentType());
            int dimensions = TypeHelper.dimensions(cexp.getApparentType());

            if (TypeHelper.isInbuiltType(apparentType))
                return new TypeInstance(apparentType, null, null, dimensions);

            QualifiedType qt = r.resolve(code, path, currentType, topLevelType, currentPackage, imports, apparentType);
            return new TypeInstance(qt, dimensions);
        } else if (expression instanceof InstantiateClassExpression) {
            InstantiateClassExpression ins = (InstantiateClassExpression)expression;
            String qualifiedName = ins.getDescriptor().getIdentifierName().toString(); // TODO generate fully qualified name

            // Create statement - TODO is this ok
            BlockStatement block = ins.getBlock().orElse(BlockStatement.EMPTY);
            ClassStatement statement = new ClassStatement(ins.getDescriptor(), block, new ArrayList<>());
            return new TypeInstance(qualifiedName, statement, null, 0);
        } else if (expression instanceof LambdaExpression) {
            // TODO parser further when full java api support introduced
            return new TypeInstance("Supplier", null, null, 0);
        } else if (expression instanceof MethodCallExpression) {
            MethodCallExpression mexp = (MethodCallExpression)expression;
            return resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r, th, mexp);
        } else if (expression instanceof ParenthesisExpression) {
            return resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r, th,
                    ((ParenthesisExpression)expression).getExpression());
        } else if (expression instanceof PostfixedExpression) {
            return resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r, th,
                    ((PostfixedExpression)expression).getExpression());
        } else if (expression instanceof PrefixedExpression) {
            return resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r, th,
                    ((PrefixedExpression)expression).getExpression());
        } else if (expression instanceof TernaryExpression) {
            return resolveTernaryExpression(path, code, topLevelType, currentType, imports, currentPackage,
                    currentContext, r, th, (TernaryExpression)expression);
        } else if (expression instanceof UnitExpression) {
            UnitExpression uexp = (UnitExpression)expression;

            switch (uexp.getValueType()) {
                case LITERAL:
                    return resolveLiteral(uexp.getValue());
                case IDENTIFIER:
                    // return the type of identifier once its found
                    break;
                case CLASS_REFERENCE:
                    break;
                case METHOD_REFERENCE:
                    break;
                case SUPER:
                    break;
                case THIS:
                    return new TypeInstance("", topLevelType, path, 0); // TODO set qualifiedName
                case THIS_CALL:
                    break;
                case SUPER_CALL:
                    break;
                case TYPE:
                    QualifiedType qt = r.resolve(code, path, topLevelType, topLevelType, currentPackage, imports,
                            uexp.getValue());
                    return new TypeInstance(qt, 0);
                case NEW:
                    break;
                case METHOD_CALL:
                    break;
            }
        }
        return null; // fall-back: failed to resolve
    }

    private static int arrayInitializerDimensions(List<Expression> expressions) { // TODO is this ok?
        return (int) expressions.stream().filter(e -> e instanceof SquareBracketsExpression).count();
    }

    private static TypeInstance resolveBinaryExpression(TypeStatement topLevelType,
            TypeStatement currentType, List<ImportStatement> imports, Optional<PackageStatement> currentPackage,
            BlockStatement currentContext, QualifiedNameResolver r, TraversalHierarchy th,
            TypeInstance lhs, BinaryOperation op, TypeInstance rhs) {
        if (op.isArithmeticOperation()) {
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
        } else if (op.isArithmeticAssignOperation()) {
            return lhs;
        } else if (op.equals(BinaryOperation.DOT)) {
            // TODO impl
        } else if (op.equals(BinaryOperation.INSTANCE_OF) || op.isBoolean()) {
            return literalType("boolean");
        }
        return null; // TODO impl
    }

    private static TypeInstance resolveTernaryExpression(Path path, Map<Path, Statement> code, TypeStatement topLevelType,
            TypeStatement currentType, List<ImportStatement> imports, Optional<PackageStatement> currentPackage,
            BlockStatement currentContext, QualifiedNameResolver r, TraversalHierarchy th, TernaryExpression texp) {
        TypeInstance t1 = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r,
                th, texp.getValueIfTrue());
        TypeInstance t2 = resolve(path, code, topLevelType, currentType, imports, currentPackage, currentContext, r,
                th, texp.getValueIfFalse());

        if (t1 != null && t2 != null) {
            if (t1.getQualifiedName().equals("null") && t2.getQualifiedName().equals("null")) {
                return new TypeInstance("java.lang.Object", null, null, 0);
            } else if (!t1.getQualifiedName().equals("null") && t2.getQualifiedName().equals("null")) {
                return t1;
            } else if (t1.getQualifiedName().equals("null") && !t2.getQualifiedName().equals("null")) {
                return t2;
            } else {
                return t1;
            }
        } else if (t1 != null) {
            if (t1.getQualifiedName().equals("null")) {
                return new TypeInstance("java.lang.Object", null, null, 0);
            }
            return t1;
        } else if (t2 != null) {
            if (t2.getQualifiedName().equals("null")) {
                return new TypeInstance("java.lang.Object", null, null, 0);
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
        return new TypeInstance(type, null, null, 0);
    }
}
