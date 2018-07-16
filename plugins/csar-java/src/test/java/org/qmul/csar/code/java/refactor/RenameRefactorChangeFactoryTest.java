package org.qmul.csar.code.java.refactor;

import org.junit.Test;
import org.qmul.csar.code.java.parse.expression.MethodCallExpression;
import org.qmul.csar.code.java.parse.statement.ConstructorStatement;
import org.qmul.csar.code.java.parse.statement.MethodStatement;
import org.qmul.csar.code.refactor.RefactorChange;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.util.FilePosition;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RenameRefactorChangeFactoryTest {

    private final RenameRefactorChangeFactory factory = new RenameRefactorChangeFactory();

    @Test
    public void testMethodStatement() {
        // Configure MethodStatement instance
        String newName = "sum";
        Path path = Paths.get(".");
        int lineNumber = 40;
        int startOffset = 100;

        MethodStatement ms = mock(MethodStatement.class);
        when(ms.getPath()).thenReturn(path);
        when(ms.getDescriptor()).thenReturn(new MethodDescriptor.Builder(newName).build());
        when(ms.getIdentifierFilePosition()).thenReturn(new FilePosition(lineNumber, startOffset));

        // Create change
        RefactorChange change = factory.change(ms, newName);

        // Assert
        assertEquals(path, change.getPath());
        assertEquals(lineNumber, change.getLineNumber());
        assertEquals(newName, change.getReplacement());
        assertEquals(startOffset, change.getStartOffset());
        assertEquals(startOffset + newName.length(), change.getEndOffset());
    }

    @Test
    public void testMethodCallExpression() {
        // Configure MethodCallExpression instance
        String newName = "sum";
        Path path = Paths.get(".");
        int lineNumber = 34;
        int startOffset = 10;

        MethodCallExpression ms = mock(MethodCallExpression.class);
        when(ms.getPath()).thenReturn(path);
        when(ms.getMethodIdentifier()).thenReturn(newName);
        when(ms.getIdentifierFilePosition()).thenReturn(new FilePosition(lineNumber, startOffset));

        // Create change
        RefactorChange change = factory.change(ms, newName);

        // Assert
        assertEquals(path, change.getPath());
        assertEquals(lineNumber, change.getLineNumber());
        assertEquals(newName, change.getReplacement());
        assertEquals(startOffset, change.getStartOffset());
        assertEquals(startOffset + newName.length(), change.getEndOffset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidArgument() {
        factory.change(mock(ConstructorStatement.class), "sum");
    }
}
