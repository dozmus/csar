package org.qmul.csar.code.parse;

import org.junit.Test;

import java.nio.file.Path;
import java.util.Iterator;

import static org.mockito.Mockito.*;

public final class DefaultProjectCodeParserTest {

    @Test(expected = IllegalArgumentException.class)
    public void createWithZeroThreadCount() {
        CodeParserFactory factory = mock(CodeParserFactory.class);
        Iterator<Path> it = mock(Iterator.class);
        new DefaultProjectCodeParser(factory, it, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNegativeThreadCount() {
        CodeParserFactory factory = mock(CodeParserFactory.class);
        Iterator<Path> it = mock(Iterator.class);
        new DefaultProjectCodeParser(factory, it, -5);
    }

    @Test(expected = NullPointerException.class)
    public void createWithNullIterator() {
        CodeParserFactory factory = mock(CodeParserFactory.class);
        new DefaultProjectCodeParser(factory, null);
    }

    @Test(expected = NullPointerException.class)
    public void createWithNullFactory() {
        Iterator<Path> it = mock(Iterator.class);
        new DefaultProjectCodeParser(null, it);
    }
}
