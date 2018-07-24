package org.qmul.csar.code.java.parse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.qmul.csar.code.parse.CodeParserFactory;
import org.qmul.csar.lang.TypeStatement;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public final class JavaCodeParserTest {

    private final TypeStatement expected;
    private final String sampleFileName;

    public JavaCodeParserTest(TypeStatement expected, String sampleFileName) {
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
    public void testParse() throws Exception {
        CodeParserFactory factory = new CodeParserFactory(JavaCodeParser.class);
        Path sampleFile = Paths.get(SampleCode.SAMPLES_DIRECTORY, sampleFileName);
        assertEquals(expected, factory.create(sampleFile).parse(sampleFile));
    }
}
