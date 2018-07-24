package org.qmul.csar.code.java.parse.code.cache;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class PhysicalProjectCodeCacheTest {

    private DummyPhysicalProjectCodeCache sut;

    @Before
    public void setUp() {
        sut = new DummyPhysicalProjectCodeCache(Paths.get(".csar"), Paths.get("."));
    }

    @Test
    public void testSourceFileToCacheFile() {
        assertSourceFileToCacheFile(Paths.get(".csar/src/org/qmul/csar/Csar.java.bin").toAbsolutePath(),
                Paths.get("src/org/qmul/csar/Csar.java"));
        assertSourceFileToCacheFile(
                Paths.get(".csar/src/main/java/org/qmul/csar/CliCsarErrorListener.java.bin").toAbsolutePath(),
                Paths.get("./src/main/java/org/qmul/csar/CliCsarErrorListener.java"));
    }

    private void assertSourceFileToCacheFile(Path expected, Path actual) {
        assertEquals(expected, sut.sourceFileToCacheFile(actual));
    }
}
