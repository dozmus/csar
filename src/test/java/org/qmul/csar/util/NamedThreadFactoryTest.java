package org.qmul.csar.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ThreadFactory;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public final class NamedThreadFactoryTest {

    private ThreadFactory threadFactory;
    private String expected1;
    private String expected2;

    public NamedThreadFactoryTest(String nameFormat, String expected1, String expected2) {
        this.threadFactory = new NamedThreadFactory(nameFormat);
        this.expected1 = expected1;
        this.expected2 = expected2;
    }

    @Parameterized.Parameters(name = "{index}: \"{0}\"")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"thread-%d", "thread-1", "thread-2"},
                {"thread-%d-%dX", "thread-1-1X", "thread-2-2X"},
                {null, "thread-1", "thread-2"}
        });
    }

    @Test
    public void testValidThreadNameSet() throws Exception {
        assertEquals(expected1, threadFactory.newThread(() -> {}).getName());
        assertEquals(expected2, threadFactory.newThread(() -> {}).getName());
    }
}
