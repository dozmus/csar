package org.qmul.csar.code.java.postprocess.methods;

import org.qmul.csar.code.CodeBase;
import org.qmul.csar.code.java.StatementVisitor;
import org.qmul.csar.code.java.parse.statement.ClassStatement;
import org.qmul.csar.code.java.parse.statement.CompilationUnitStatement;
import org.qmul.csar.code.java.parse.statement.EnumStatement;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.lang.Statement;

import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MethodTestUtils {

    public static void assertIsOverridden(CodeBase code, String baseDirectory, String fileName,
            String signature) {
        MethodStatement m = findMethod(code, baseDirectory, fileName, signature);
        assertTrue(m.getDescriptor().getOverridden().orElse(false));
    }

    public static void assertIsNotOverridden(CodeBase code, String baseDirectory, String fileName,
            String signature) {
        MethodStatement m = findMethod(code, baseDirectory, fileName, signature);
        assertFalse(m.getDescriptor().getOverridden().orElse(false));
    }

    /**
     * Returns the method if found, or null otherwise.
     */
    public static MethodStatement findMethod(CodeBase code, String baseDirectory, String path,
            String signature) throws RuntimeException {
        Statement statement = code.get(Paths.get(baseDirectory + path));
        CompilationUnitStatement cus = (CompilationUnitStatement) statement;

        // Visit contents
        MethodFinder visitor = new MethodFinder(signature);
        visitor.visitStatement(cus);
        return visitor.getResult();
    }

    private static final class MethodFinder extends StatementVisitor {

        private final Deque<String> traversalHierarchy = new ArrayDeque<>();
        private final String signature;
        private MethodStatement result;

        MethodFinder(String signature) {
            this.signature = signature;
        }

        @Override
        public void visitEnumStatement(EnumStatement statement) {
            traversalHierarchy.addLast(typePrefix() + statement.getDescriptor().getIdentifierName().toString());
            super.visitEnumStatement(statement);
        }

        @Override
        public void exitEnumStatement(EnumStatement statement) {
            traversalHierarchy.removeLast();
        }

        @Override
        public void visitClassStatement(ClassStatement statement) {
            traversalHierarchy.addLast(typePrefix() + statement.getDescriptor().getIdentifierName().toString());
            super.visitClassStatement(statement);
        }

        @Override
        public void exitClassStatement(ClassStatement statement) {
            traversalHierarchy.removeLast();
        }

        @Override
        public void visitMethodStatement(MethodStatement statement) {
            traversalHierarchy.addLast(methodPrefix() + statement.getDescriptor().signature());

            if (signature().equals(signature)) { // Stop if they match
                result = statement;
            } else {
                super.visitMethodStatement(statement);
            }
            traversalHierarchy.removeLast();
        }

        MethodStatement getResult() {
            return result;
        }

        /**
         * Returns the prefix for the current type statement.
         */
        private String typePrefix() {
            return traversalHierarchy.size() <= 1 ? "" : "$";
        }

        /**
         * Returns the prefix for the current method statement.
         */
        private String methodPrefix() {
            return traversalHierarchy.size() <= 1 ? "" : "#";
        }

        /**
         * Returns the signature of the current statement in the traversal hierarchy.
         */
        private String signature() {
            // Ignore the top-level class
            StringBuilder sb = new StringBuilder();
            boolean first = true;

            for (String item : traversalHierarchy) {
                if (!first) {
                    sb.append(item);
                }
                first = false;
            }
            return sb.toString();
        }
    }
}
