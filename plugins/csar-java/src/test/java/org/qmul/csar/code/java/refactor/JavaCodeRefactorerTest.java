package org.qmul.csar.code.java.refactor;

import org.junit.Test;
import org.qmul.csar.CsarJavaPlugin;
import org.qmul.csar.code.Result;
import org.qmul.csar.code.java.TestUtils;
import org.qmul.csar.code.java.postprocess.JavaPostProcessor;
import org.qmul.csar.code.java.postprocess.methodcalls.typeinstances.MethodCallTypeInstanceResolver;
import org.qmul.csar.code.java.postprocess.methods.overridden.OverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.methods.overridden.SelectiveOverriddenMethodsResolver;
import org.qmul.csar.code.java.postprocess.methods.types.MethodQualifiedTypeResolver;
import org.qmul.csar.code.java.postprocess.methods.use.MethodUseResolver;
import org.qmul.csar.code.java.postprocess.qualifiedname.QualifiedNameResolver;
import org.qmul.csar.code.java.postprocess.typehierarchy.DefaultTypeHierarchyResolver;
import org.qmul.csar.code.java.search.JavaCodeSearcher;
import org.qmul.csar.code.postprocess.CodePostProcessor;
import org.qmul.csar.code.refactor.ProjectCodeRefactorer;
import org.qmul.csar.code.refactor.writer.DummyRefactorChangeWriter;
import org.qmul.csar.code.search.ProjectCodeSearcher;
import org.qmul.csar.lang.SerializableCode;
import org.qmul.csar.lang.Statement;
import org.qmul.csar.lang.descriptors.MethodDescriptor;
import org.qmul.csar.plugin.CsarPlugin;
import org.qmul.csar.query.CsarQuery;
import org.qmul.csar.query.CsarQueryFactory;

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
        CsarPlugin csarPlugin = new DummyJavaPlugin();
        csarPlugin.parse(Paths.get(directory), false, Paths.get("."), 1);
        csarPlugin.postprocess(1, query);
        List<Result> searchResults = csarPlugin.search(query, 1);
        return csarPlugin.refactor(query, searchResults, 1);
    }

    /**
     * A java plugin which doesn't write the refactor results to the files.
     * This is so that the tests will continue to work afterwards (if they change these tests will only work once).
     */
    private static final class DummyJavaPlugin extends CsarJavaPlugin {

        private Map<Path, Statement> code;
        private List<SerializableCode> searchResultObjects;
        private DefaultTypeHierarchyResolver thr;

        @Override
        public Map<Path, Statement> parse(Path projectDirectory, boolean narrowSearch, Path ignoreFile, int threadCount)
                throws Exception {
            code = super.parse(projectDirectory, narrowSearch, ignoreFile, threadCount);
            return code;
        }

        @Override
        public void postprocess(int threadCount, CsarQuery csarQuery) {
            // Create components
            QualifiedNameResolver qnr = new QualifiedNameResolver();
            thr = new DefaultTypeHierarchyResolver(qnr);
            MethodQualifiedTypeResolver mqtr = new MethodQualifiedTypeResolver();
            OverriddenMethodsResolver omr = new SelectiveOverriddenMethodsResolver(threadCount, qnr, thr,
                    (MethodDescriptor) csarQuery.getSearchTarget().getDescriptor());
            MethodUseResolver mur = new MethodUseResolver(qnr, thr);
            MethodCallTypeInstanceResolver mctir = new MethodCallTypeInstanceResolver(qnr, thr);

            // Create post-processor
            CodePostProcessor processor = new JavaPostProcessor(thr, mqtr, omr, mctir, mur);
            processor.postprocess(code);
        }

        @Override
        public List<Result> search(CsarQuery csarQuery, int threadCount) {
            ProjectCodeSearcher searcher = new JavaCodeSearcher(threadCount);
            searcher.setCsarQuery(csarQuery);
            searcher.setIterator(code.entrySet().iterator());
            searchResultObjects = searcher.resultObjects();
            return searcher.results();
        }

        @Override
        public List<Result> refactor(CsarQuery csarQuery, List<Result> searchResults, int threadCount) {
            ProjectCodeRefactorer refactorer = new JavaCodeRefactorer(threadCount, thr,
                    new DummyRefactorChangeWriter());
            refactorer.setRefactorDescriptor(csarQuery.getRefactorDescriptor().
                    orElseThrow(IllegalArgumentException::new));
            refactorer.setSearchResultObjects(searchResultObjects);
            return refactorer.results();
        }
    }
}
