package org.qmul.csar.code.refactor;

import org.junit.Test;
import org.qmul.csar.code.refactor.writer.DefaultRefactorChangeWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultRefactorChangeWriterTest {

    private final DefaultRefactorChangeWriter writer = new DefaultRefactorChangeWriter();

    @Test
    public void testApply_ChangeAtBeginning() {
        // Create
        String src = " World";

        RefactorChange change = mock(RefactorChange.class);
        when(change.getReplacement()).thenReturn("Hello ");
        when(change.getStartOffset()).thenReturn(0);
        when(change.getEndOffset()).thenReturn(1);

        // Assert
        String actual = writer.apply(src, change);
        assertEquals("Hello World", actual);
    }

    @Test
    public void testApply_ChangeEntireString() {
        // Create
        String src = "Hello World";
        String expected = "My name is csar!";

        RefactorChange change = mock(RefactorChange.class);
        when(change.getReplacement()).thenReturn(expected);
        when(change.getStartOffset()).thenReturn(0);
        when(change.getEndOffset()).thenReturn(src.length());

        // Assert
        String actual = writer.apply(src, change);
        assertEquals(expected, actual);
    }

    @Test
    public void testApply() {
        // Create
        String src = "Hello";

        RefactorChange change = mock(RefactorChange.class);
        when(change.getReplacement()).thenReturn("LL");
        when(change.getStartOffset()).thenReturn(2);
        when(change.getEndOffset()).thenReturn(4);

        // Assert
        String actual = writer.apply(src, change);
        assertEquals("HeLLo", actual);
    }

    @Test
    public void testApply_StartIndexEqualsEndIndex() {
        // Create
        String src = "World";

        RefactorChange change = mock(RefactorChange.class);
        when(change.getReplacement()).thenReturn("Hello ");
        when(change.getStartOffset()).thenReturn(0);
        when(change.getEndOffset()).thenReturn(0);

        // Assert
        String actual = writer.apply(src, change);
        assertEquals("Hello World", actual);
    }
}
