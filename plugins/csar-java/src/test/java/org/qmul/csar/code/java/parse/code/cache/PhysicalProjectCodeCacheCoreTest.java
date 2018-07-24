package org.qmul.csar.code.java.parse.code.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.qmul.csar.code.java.parse.SampleCode;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.TypeStatement;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public final class PhysicalProjectCodeCacheCoreTest {

    private final PhysicalProjectCodeCache sut = new DummyPhysicalProjectCodeCache(Paths.get(".csar"), Paths.get("."));
    private final TypeStatement expected;
    private final String sampleFileName;

    public PhysicalProjectCodeCacheCoreTest(TypeStatement expected, String sampleFileName) {
        this.expected = expected;
        this.sampleFileName = sampleFileName;
    }

    @Parameterized.Parameters(name = "{index}: \"{1}\"")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {SampleCode.sample1(), "Sample1.java"},
                {SampleCode.sample2(), "Sample2.java"},
                {SampleCode.sample3(), "Sample3.java"},
                {SampleCode.sample4(), "Sample4.java"},
                {SampleCode.sample5(), "Sample5.java"},
                {SampleCode.sample6(), "Sample6.java"},
                {SampleCode.sample7(), "Sample7.java"},
                {SampleCode.sample8(), "Sample8.java"},
                {SampleCode.sample9(), "Sample9.java"},
                {SampleCode.sample10(), "Sample10.java"},
                {SampleCode.sample11(), "Sample11.java"},
                {SampleCode.sample12(), "Sample12.java"},
                {SampleCode.sample13(), "Sample13.java"}
        });
    }

    /**
     * Tests the current sample file parameter, this is set by {@link #data()}.
     */
    @Test
    public void testCache() throws Exception {
        Path sampleFile = Paths.get(SampleCode.SAMPLES_DIRECTORY, sampleFileName);

        sut.put(sampleFile, expected);
        assertTrue(sut.cached(sampleFile));

        Statement actual = sut.get(sampleFile);
        assertEquals(expected, actual);
    }
}
