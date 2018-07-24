package org.qmul.csar.code.parse;

import org.junit.Test;
import org.mockito.Mock;
import org.qmul.csar.code.parse.cache.ProjectCodeCache;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Supplier;

public final class DefaultProjectCodeParserTest {

    @Mock
    private Iterator<Path> it;
    @Mock
    private CodeParserFactory factory;
    @Mock
    private Supplier<ProjectCodeCache> cache;

    @Test(expected = IllegalArgumentException.class)
    public void createWithZeroThreadCount() {
        new DefaultProjectCodeParser(factory, it, cache, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNegativeThreadCount() {
        new DefaultProjectCodeParser(factory, it, cache, -5);
    }

    @Test(expected = NullPointerException.class)
    public void createWithNullIterator() {
        new DefaultProjectCodeParser(factory, null);
    }

    @Test(expected = NullPointerException.class)
    public void createWithNullFactory() {
        new DefaultProjectCodeParser(null, it);
    }

    @Test(expected = NullPointerException.class)
    public void createWithNullCache() {
        new DefaultProjectCodeParser(factory, it, null, 1);
    }
}
