package org.qmul.csar.code.java.refactor;

import org.junit.Test;
import org.qmul.csar.code.Result;
import org.qmul.csar.code.java.JavaTestCsarPlugin;
import org.qmul.csar.code.java.TestUtils;
import org.qmul.csar.plugin.CsarPlugin;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaCodeRefactorerTest {

    /**
     * Tests refactoring renaming of methods.
     */
    @Test
    public void testRenameMethods() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/refactor/rename/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 3, "    public void anotherPrint() {"));
        expectedResults.add(new Result(Paths.get(directory, "B.java"), 3, "    public void anotherPrint() {"));
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 5, "        a.anotherPrint();"));
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 10, "        b.anotherPrint();"));

        // Actual
        List<Result> actualResults = refactor("SELECT method:def:print REFACTOR rename:anotherPrint", directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    /**
     * Tests refactoring change parameters of methods, for when the current arguments have the last one removed.
     * i.e. <tt>(int a, int b) -> (int a)</tt>.
     */
    @Test
    public void testChangeParametersOfMethods1() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/refactor/changeparams1/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 3, "    public void print(int a) {"));
        expectedResults.add(new Result(Paths.get(directory, "B.java"), 3, "    public void print(int a) {"));
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 5, "        a.print(1);"));
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 10, "        b.print(500);"));

        // Actual
        List<Result> actualResults = refactor("SELECT method:def:print REFACTOR changeparam:int a", directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    /**
     * Tests refactoring change parameters of methods, for when the current arguments are entirely changed, and one of
     * the method definitions and one of the usages to change spans two lines.
     * i.e. <tt>(int a, int b) -> (String s, String s1)</tt>.
     */
    @Test
    public void testChangeParametersOfMethods2() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/refactor/changeparams2/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 3,
                "    public void print(String s, String s1) {"));
        expectedResults.add(new Result(Paths.get(directory, "B.java"), 3,
                "    public void print(String s, String s1) {"));
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 5, "        a.print(s, s1);"));
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 10, "        b.print(s, s1);"));

        // Actual
        List<Result> actualResults = refactor("SELECT method:def:print REFACTOR changeparam:String s, String s1",
                directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    /**
     * Tests refactoring change parameters of methods, for when the current arguments are entirely changed.
     * i.e. <tt>(int a, int b) -> (int a, int b, int c)</tt>.
     */
    @Test
    public void testChangeParametersOfMethods3() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/refactor/changeparams3/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 3,
                "    public void print(int a, int b, int c) {"));
        expectedResults.add(new Result(Paths.get(directory, "B.java"), 3,
                "    public void print(int a, int b, int c) {"));
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 5, "        a.print(1, 2, c);"));
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 10, "        b.print(500"));

        // Actual
        List<Result> actualResults = refactor("SELECT method:def:print REFACTOR changeparam:int a, int b, int c",
                directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    /**
     * Tests refactoring change parameters of methods, for when a method call argument contains the initial method call.
     * i.e. <tt>(int a, int b) -> (String a, int b)</tt>.
     */
    @Test
    public void testChangeParametersOfMethods4() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/refactor/changeparams4/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 8,
                "    public void print(String a, int b) {"));
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 5, "        a.print(a.print(a, 2), 2);"));

        // Actual
        List<Result> actualResults = refactor("SELECT method:def:print REFACTOR changeparam:String a, int b",
                directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    /**
     * Tests refactoring change parameters of methods, for when a method call argument contains the initial method call.
     * i.e. <tt>(int a, int b) -> (int a, String b)</tt>.
     */
    @Test
    public void testChangeParametersOfMethods5() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/refactor/changeparams5/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 8,
                "    public void print(int a, String b) {"));
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 5, "        a.print(1, a.print(1, b));"));

        // Actual
        List<Result> actualResults = refactor("SELECT method:def:print REFACTOR changeparam:int a, String b",
                directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    /**
     * Tests refactoring change parameters of methods, for when a method call argument contains the initial method call,
     * and there is an identical method call on the same line.
     * i.e. <tt>(int a, int b) -> (String a, int b)</tt>.
     */
    @Test
    public void testChangeParametersOfMethods6() throws Exception {
        // Expected
        String directory = "src/test/resources/org/qmul/csar/refactor/changeparams6/";
        List<Result> expectedResults = new ArrayList<>();
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 8,
                "    public void print(String a, int b) {"));
        expectedResults.add(new Result(Paths.get(directory, "A.java"), 5, "        a.print(a.print(a, 2), 2); a.print(a.print(a, 2), 2);"));

        // Actual
        List<Result> actualResults = refactor("SELECT method:def:print REFACTOR changeparam:String a, int b",
                directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    /**
     * Returns the refactor results of applying the argument query onto the argument code directory.
     */
    private static List<Result> refactor(String csarQuery, String directory) throws Exception {
        CsarQuery query = CsarQueryFactory.parse(csarQuery);
        CsarPlugin csarPlugin = new JavaTestCsarPlugin();
        csarPlugin.parse(Paths.get(directory), null, false, Paths.get("."), true, 1);
        csarPlugin.postprocess(1, query);
        List<Result> searchResults = csarPlugin.search(query, 1);
        return csarPlugin.refactor(query, searchResults, 1);
    }
}
