package org.qmul.csar.code.java.postprocess.methodproc;

import org.qmul.csar.code.java.parse.expression.BinaryExpression;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.BlockStatement;
import org.qmul.csar.code.java.parse.statement.ImportStatement;
import org.qmul.csar.code.java.parse.statement.PackageStatement;
import org.qmul.csar.code.java.postprocess.ExpressionTypeResolver;
import org.qmul.csar.code.java.postprocess.TypeInstance;
import org.qmul.csar.code.java.postprocess.methodusage.TraversalHierarchy;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.TypeHierarchyResolver;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MethodCallProcessor {

    private final Path path;
    private final Map<Path, Statement> code;
    private final TraversalHierarchy th;
    private final QualifiedNameResolver qn;
    private final TypeHierarchyResolver thr;

    public MethodCallProcessor(Path path, Map<Path, Statement> code, TraversalHierarchy th, QualifiedNameResolver qn,
            TypeHierarchyResolver thr) {
        this.path = path;
        this.code = code;
        this.th = th;
        this.qn = qn;
        this.thr = thr;
    }

    public void resolve(MethodCallExpression expression) {
        Expression name = expression.getMethodName();
        List<Expression> args = expression.getArguments();

        // Set method source if applicable
        if (name instanceof BinaryExpression) {
            System.out.println("Found binary expression method name");
            BinaryExpression exp = (BinaryExpression)name;
            expression.setMethodSource(resolve(exp));
//            System.out.println("Set source to: " + expression.getMethodSource().getType() + " "
//                    + expression.getMethodSource().getQualifiedName());
        }

        // Set argument types
        List<TypeInstance> argsTypes = args.stream().map(this::resolve).collect(Collectors.toList());
        expression.setArgumentTypes(Collections.unmodifiableList(argsTypes));
    }

    private TypeInstance resolve(Expression expr) {
        // Set context
        TypeStatement topLevelType = th.getFirstTypeStatement();
        TypeStatement currentType = th.getLastTypeStatement();
        List<ImportStatement> imports = th.getImports();
        Optional<PackageStatement> currentPackage = th.getPackageStatement();
        BlockStatement currentContext = th.currentContext();
        return new ExpressionTypeResolver().resolve(path, code, topLevelType, currentType, imports, currentPackage,
                currentContext, qn, th, thr, expr);
    }
}
