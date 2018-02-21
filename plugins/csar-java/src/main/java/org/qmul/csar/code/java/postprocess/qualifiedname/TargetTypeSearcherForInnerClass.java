package org.qmul.csar.code.java.postprocess.qualifiedname;

import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.statement.AnnotationStatement;
import org.qmul.csar.code.java.parse.statement.ClassStatement;
import org.qmul.csar.code.java.parse.statement.EnumStatement;
import org.qmul.csar.code.java.parse.statement.PackageStatement;
import org.qmul.csar.lang.IdentifierName;
import org.qmul.csar.lang.Statement;

import java.util.*;

final class TargetTypeSearcherForInnerClass extends StatementVisitor {

    private final Deque<String> traversalHierarchy = new ArrayDeque<>();
    private final Map<String, Statement> types = new HashMap<>();
    private boolean inner = false;

    public void resetState(Optional<PackageStatement> packageStatement) {
        traversalHierarchy.clear();
        types.clear();
        inner = false;

        if (packageStatement.isPresent()) {
            resetStatePkgName(packageStatement.get().getPackageName());
        } else {
            resetStatePkgName(null);
        }
    }

    public void resetStatePkgName(String pkg) {
        traversalHierarchy.clear();
        types.clear();
        inner = false;

        if (pkg != null && !pkg.isEmpty()) {
            String[] pkgParts = pkg.split("\\.");

            for (int i = 0; i < pkgParts.length; i++) {
                String s = pkgParts[i];

                if (i > 0)
                    s = "." + s;
                traversalHierarchy.addLast(s);
            }
        }
    }

    @Override
    public void visitClassStatement(ClassStatement statement) {
        updateTypes(statement.getDescriptor().getIdentifierName(), statement);
        super.visitClassStatement(statement);
        traversalHierarchy.removeLast();
    }

    @Override
    public void visitEnumStatement(EnumStatement statement) {
        updateTypes(statement.getDescriptor().getIdentifierName(), statement);
        super.visitEnumStatement(statement);
        traversalHierarchy.removeLast();
    }

    @Override
    public void visitAnnotationStatement(AnnotationStatement statement) {
        updateTypes(statement.getDescriptor().getIdentifierName(), statement);
        super.visitAnnotationStatement(statement);
        traversalHierarchy.removeLast();
    }

    private void updateTypes(IdentifierName identifierName, Statement statement) {
        traversalHierarchy.addLast(prefix() + identifierName.toString());
        types.put(String.join("", traversalHierarchy), statement);
        inner = true;
    }

    private String prefix() {
        if (traversalHierarchy.size() == 0)
            return "";
        return inner ? "$" : ".";
    }

    public Map<String, Statement> getTypes() {
        return types;
    }
}
