package org.qmul.csar.code.java.parse.code.cache;

import com.esotericsoftware.kryo.Kryo;
import de.javakaffee.kryoserializers.*;
import org.qmul.csar.code.java.parse.code.cache.util.PathSerializer;
import org.qmul.csar.code.java.parse.expression.*;
import org.qmul.csar.code.java.parse.statement.*;
import org.qmul.csar.lang.*;
import org.qmul.csar.lang.descriptors.*;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

public final class KryoConfigurer {

    public static void register(Kryo kryo) {
        // JDK
        kryo.addDefaultSerializer(Path.class, new PathSerializer(kryo));
        kryo.addDefaultSerializer(Pattern.class, new RegexSerializer());

        // Collections
        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
        kryo.register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
        kryo.register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
        kryo.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
        kryo.register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
        kryo.register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);

        // Descriptors
        kryo.register(Descriptor.class);
        kryo.register(Expression.class);
        kryo.register(IdentifierName.class);
        kryo.register(IdentifierName.Static.class);
        kryo.register(IdentifierName.Regex.class);
        kryo.register(SerializableCode.class);
        kryo.register(Statement.class);
        kryo.register(TypeStatement.class);

        kryo.register(AbstractCommentDescriptor.class);
        kryo.register(AbstractVariableDescriptor.class);
        kryo.register(AnnotationDescriptor.class);
        kryo.register(BlockCommentDescriptor.class);
        kryo.register(ClassDescriptor.class);
        kryo.register(ConditionalDescriptor.class);
        kryo.register(EnumDescriptor.class);
        kryo.register(InstanceVariableDescriptor.class);
        kryo.register(LineCommentDescriptor.class);
        kryo.register(LocalVariableDescriptor.class);
        kryo.register(MethodDescriptor.class);
        kryo.register(ParameterVariableDescriptor.class);
        kryo.register(VisibilityModifier.class);

        // Expressions
        kryo.register(ArrayAccessExpression.class);
        kryo.register(ArrayExpression.class);
        kryo.register(ArrayInitializationExpression.class);
        kryo.register(BinaryExpression.class);
        kryo.register(BinaryOperation.class);
        kryo.register(CastExpression.class);
        kryo.register(InstantiateClassExpression.class);
        kryo.register(LambdaExpression.class);
        kryo.register(LambdaParameter.Identifier.class);
        kryo.register(LambdaParameter.Identifiers.class);
        kryo.register(LambdaParameter.ParameterVariables.class);
        kryo.register(MethodCallExpression.class);
        kryo.register(MethodReferenceExpression.class);
        kryo.register(ParenthesisExpression.class);
        kryo.register(Postfix.class);
        kryo.register(PostfixedExpression.class);
        kryo.register(Prefix.class);
        kryo.register(PrefixedExpression.class);
        kryo.register(ParenthesisExpression.class);
        kryo.register(SquareBracketsExpression.class);
        kryo.register(TernaryExpression.class);
        kryo.register(TypeArgument.Bounds.class);
        kryo.register(TypeArgument.Type.class);
        kryo.register(UnitExpression.class);

        // Statements
        kryo.register(Annotation.class);
        kryo.register(Annotation.Value.class);
        kryo.register(Annotation.Values.class);
        kryo.register(Annotation.AnnotationValue.class);
        kryo.register(Annotation.ExpressionValue.class);
        kryo.register(AnnotationStatement.class);
        kryo.register(AnnotationStatement.AnnotationMethod.class);
        kryo.register(AssertStatement.class);
        kryo.register(BlockStatement.class);
        kryo.register(BreakStatement.class);
        kryo.register(CatchStatement.class);
        kryo.register(ClassStatement.class);
        kryo.register(CompilationUnitStatement.class);
        kryo.register(ConstructorStatement.class);
        kryo.register(ContinueStatement.class);
        kryo.register(DoWhileStatement.class);
        kryo.register(EnumConstantStatement.class);
        kryo.register(EnumStatement.class);
        kryo.register(ExpressionStatement.class);
        kryo.register(ForEachStatement.class);
        kryo.register(ForStatement.class);
        kryo.register(IfStatement.class);
        kryo.register(ImportStatement.class);
        kryo.register(InstanceVariableStatement.class);
        kryo.register(LabelStatement.class);
        kryo.register(LocalVariableStatement.class);
        kryo.register(LocalVariableStatements.class);
        kryo.register(MethodStatement.class);
        kryo.register(PackageStatement.class);
        kryo.register(ParameterVariableStatement.class);
        kryo.register(ReturnStatement.class);
        kryo.register(SemiColonStatement.class);
        kryo.register(StaticBlockStatement.class);
        kryo.register(SwitchLabelStatement.class);
        kryo.register(SwitchStatement.class);
        kryo.register(SynchronizedStatement.class);
        kryo.register(ThrowStatement.class);
        kryo.register(TryStatement.class);
        kryo.register(TryWithResourcesStatement.class);
        kryo.register(WhileStatement.class);
    }
}
