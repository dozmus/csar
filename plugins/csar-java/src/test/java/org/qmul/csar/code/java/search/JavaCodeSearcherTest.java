package org.qmul.csar.code.java.search;

import org.junit.Test;
import org.qmul.csar.CsarJavaPlugin;
import org.qmul.csar.code.java.TestUtils;
import org.qmul.csar.plugin.CsarPlugin;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.qmul.csar.code.Result;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaCodeSearcherTest {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Test
    public void testSearchMethodUses1() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/search/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 5, "a.print()"));
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 10, "b.print()"));

        // Actual
        List<Result> actualResults = search("SELECT method:use:print", directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    @Test
    public void testSearchMethodUses2() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/search/";
        List<Result> expectedResults = new ArrayList<>();

        // Actual
        List<Result> actualResults = search("SELECT method:use:print(String)", directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    @Test
    public void testSearchMethodDefs() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/search/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 3,
                "public void print() {" + LINE_SEPARATOR
                        + "  System.out.println(\"print in A\");" + LINE_SEPARATOR
                        + "}")
        );
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 7,
                "public void print(String s) {" + LINE_SEPARATOR
                        + "  System.out.println(s);" + LINE_SEPARATOR
                        + "}")
        );
        expectedResults.add(new Result(Paths.get(directory, "B.java"), 3,
                "public (overridden) void print() {" + LINE_SEPARATOR
                        + "  System.out.println(\"print in B\");" + LINE_SEPARATOR
                        + "}")
        );

        // Actual
        List<Result> actualResults = search("SELECT method:def:print", directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    @Test
    public void testSearchMethodDefsByArgumentType() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/search/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 7,
                "public void print(String s) {" + LINE_SEPARATOR
                        + "  System.out.println(s);" + LINE_SEPARATOR
                        + "}")
        );

        // Actual
        List<Result> actualResults = search("SELECT method:def:print(String)", directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    @Test
    public void testSearchMethodDefsByArgumentCount() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/search/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 7,
                "public void print(String s) {" + LINE_SEPARATOR
                        + "  System.out.println(s);" + System.lineSeparator()
                        + "}")
        );

        // Actual
        List<Result> actualResults = search("SELECT method:def:print(1)", directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    /**
     * Returns the search results of applying the argument query onto the argument code directory.
     */
    private List<Result> search(String csarQuery, String directory) throws Exception {
        CsarQuery query = CsarQueryFactory.parse(csarQuery);
        CsarPlugin csarPlugin = new CsarJavaPlugin();
        csarPlugin.parse(Paths.get(directory), false, Paths.get("."), 1);
        csarPlugin.postprocess(1, query);
        return csarPlugin.search(query, 1);
    }
}
