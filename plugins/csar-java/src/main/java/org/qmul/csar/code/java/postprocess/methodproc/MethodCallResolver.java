package org.qmul.csar.code.java.postprocess.methodproc;

import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.BlockStatement;
import org.qmul.csar.code.java.parse.statement.ImportStatement;
import org.qmul.csar.code.java.parse.statement.PackageStatement;
import org.qmul.csar.code.java.postprocess.methodusage.TraversalHierarchy;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.lang.Expression;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MethodCallResolver {

    private final Path path;
    private final Map<Path, Statement> code;
    private final TraversalHierarchy th;
    private final QualifiedNameResolver qn;

    public MethodCallResolver(Path path, Map<Path, Statement> code, TraversalHierarchy th, QualifiedNameResolver qn) {
        this.path = path;
        this.code = code;
        this.th = th;
        this.qn = qn;
    }

    public void resolve(MethodCallExpression expression) {
        Expression name = expression.getMethodName();
        List<Expression> args = expression.getArguments();

        expression.setMethodNameType(resolve(name));
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
        return ExpressionTypeResolver.resolve(path, code, topLevelType, currentType, imports, currentPackage,
                currentContext, qn, th, expr);
    }
}
