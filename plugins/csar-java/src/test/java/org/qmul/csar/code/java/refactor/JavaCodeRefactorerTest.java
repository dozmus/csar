package org.qmul.csar.code.java.refactor;

import org.junit.Test;
import org.qmul.csar.CsarJavaPlugin;
import org.qmul.csar.code.refactor.ProjectCodeRefactorer;
import org.qmul.csar.code.ProjectCodeSearcher;
import org.qmul.csar.code.refactor.RefactorTarget;
import org.qmul.csar.code.java.TestUtils;
import org.qmul.csar.code.java.search.JavaCodeSearcher;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.plugin.CsarPlugin;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;
import org.qmul.csar.result.Result;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        expectedResults.add(new Result(Paths.get(directory, "C.java"), 10, "        b.print(500                , 100, c);"));

        // Actual
        List<Result> actualResults = refactor("SELECT method:def:print REFACTOR changeparam:int a, int b, int c",
                directory);

        // Compare
        TestUtils.assertEquals(expectedResults, actualResults);
    }

    /**
     * Returns the refactor results of applying the argument query onto the argument code directory.
     */
    private static List<Result> refactor(String csarQuery, String directory) throws Exception {
        CsarQuery query = CsarQueryFactory.parse(csarQuery);
        CsarPlugin csarPlugin = new DummyJavaPlugin();
        csarPlugin.parse(Paths.get(directory), false, Paths.get("."), 1);
        csarPlugin.postprocess(1);
        List<Result> searchResults = csarPlugin.search(query, 1);
        return csarPlugin.refactor(query, searchResults, 1);
    }

    /**
     * A java plugin which doesn't write the refactor results to the files.
     * This is so that the tests will continue to work afterwards (if they change these tests will only work once).
     */
    private static final class DummyJavaPlugin extends CsarJavaPlugin {

        private Map<Path, Statement> code;
        private List<RefactorTarget> refactorTargets;

        @Override
        public Map<Path, Statement> parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount)
                throws Exception {
            code = super.parse(projectDirectory, narrowSearch, ignoreFile, threadCount);
            return code;
        }

        @Override
        public void postprocess(int threadCount) {
            super.postprocess(1);
        }

        @Override
        public List<Result> search(CsarQuery csarQuery, int threadCount) {
            ProjectCodeSearcher searcher = new JavaCodeSearcher(threadCount);
            searcher.setCsarQuery(csarQuery);
            searcher.setIterator(code.entrySet().iterator());
            refactorTargets = searcher.refactorTargets();
            return searcher.results();
        }

        @Override
        public List<Result> refactor(CsarQuery csarQuery, List<Result> searchResults, int threadCount) {
            ProjectCodeRefactorer refactorer = new JavaCodeRefactorer(threadCount, false);
            refactorer.setRefactorDescriptor(csarQuery.getRefactorDescriptor().
                    orElseThrow(IllegalArgumentException::new));
            refactorer.setRefactorTargets(refactorTargets);
            return refactorer.results();
        }
    }
}
