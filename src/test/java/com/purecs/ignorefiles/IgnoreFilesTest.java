package com.purecs.ignorefiles;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class IgnoreFilesTest {

    private static final String BASE_DIR = Paths.get("").toAbsolutePath().toString();
    private static final String IGNORE_FILE_DIR = "src/test/resources/com/purecs/ignorefiles/";

    private static void assertTrue(Path path, String ruleText) {
        Rule rule = Rule.parse(BASE_DIR, ruleText);
        Assert.assertTrue(IgnoreFiles.ignored(path, rule));
    }

    private static void assertFalse(Path path, String ruleText) {
        Rule rule = Rule.parse(BASE_DIR, ruleText);
        Assert.assertFalse(IgnoreFiles.ignored(path, rule));
    }

    private void assertTrue(List<Rule> rules, Path path) {
        Assert.assertTrue(IgnoreFiles.ignored(path, rules));
    }

    private void assertFalse(List<Rule> rules, Path path) {
        Assert.assertFalse(IgnoreFiles.ignored(path, rules));
    }

    @Test
    public void testFileInput() throws Exception {
        assertTrue(Paths.get("code.java"), "*.java");
        assertTrue(Paths.get("code"), "*");
        assertTrue(Paths.get("code"), "code");
        assertTrue(Paths.get("code", "a.c"), "code");
        assertTrue(Paths.get("code.java"), "code.java");
        assertTrue(Paths.get("code.java"), "c[o|a]de.j??a");
        assertTrue(Paths.get("code.java"), "code.*");
        assertTrue(Paths.get("code.java"), "code*");
        assertFalse(Paths.get("code.java"), "z*");
        assertFalse(Paths.get("code.java"), "!code.java");
        assertTrue(Paths.get("code.java"), "/code.java");
        assertFalse(Paths.get("a", "code.java"), "/code.java");
        assertTrue(Paths.get("!code.java"), "\\!code.java");

        /*
         * The following test cases are from: https://www.atlassian.com/git/tutorials/gitignore (accessed: 29/09/17)
         * The only modification applied is that they have been translated to this testing syntax.
         * The content on that website is licensed under https://creativecommons.org/licenses/by/2.5/au/
         */
        // **/logs
        assertTrue(Paths.get("logs", "debug.log"), "**/logs");
        assertTrue(Paths.get("logs", "monday", "foo.bar"), "**/logs");
        assertTrue(Paths.get("builds", "logs", "debug.log"), "**/logs");

        // **/logs/debug.log
        assertTrue(Paths.get("logs", "debug.log"), "**/logs/debug.log");
        assertTrue(Paths.get("build", "logs", "debug.log"), "**/logs/debug.log");
        assertFalse(Paths.get("logs", "build", "debug.log"), "**/logs/debug.log");

        // *.log
        assertTrue(Paths.get("debug.log"), "*.log");
        assertTrue(Paths.get("foo.log"), "*.log");
        assertTrue(Paths.get(".log"), "*.log");
        assertTrue(Paths.get("logs", "debug.log"), "*.log");

        // debug?.log
        assertTrue(Paths.get("debug0.log"), "debug?.log");
        assertTrue(Paths.get("debugg.log"), "debug?.log");
        assertFalse(Paths.get("debug10.log"), "debug?.log");

        // debug[0-9].log
        assertTrue(Paths.get("debug0.log"), "debug[0-9].log");
        assertTrue(Paths.get("debug1.log"), "debug[0-9].log");
        assertFalse(Paths.get("debug10.log"), "debug[0-9].log");

        // debug[01].log
        assertTrue(Paths.get("debug0.log"), "debug[01].log");
        assertTrue(Paths.get("debug1.log"), "debug[01].log");
        assertFalse(Paths.get("debug2.log"), "debug[01].log");
        assertFalse(Paths.get("debug01.log"), "debug[01].log");

        // debug[!01].log
        assertTrue(Paths.get("debug2.log"), "debug[!01].log");
        assertFalse(Paths.get("debug0.log"), "debug[!01].log");
        assertFalse(Paths.get("debug1.log"), "debug[!01].log");
        assertFalse(Paths.get("debug01.log"), "debug[!01].log");

        // debug[a-z].log
        assertTrue(Paths.get("debuga.log"), "debug[a-z].log");
        assertTrue(Paths.get("debugb.log"), "debug[a-z].log");
        assertFalse(Paths.get("debug1.log"), "debug[a-z].log");

        // logs
        assertTrue(Paths.get("logs"), "logs");
        assertTrue(Paths.get("logs", "debug.log"), "logs");
        assertTrue(Paths.get("logs", "latest", "foo.bar"), "logs");
        assertTrue(Paths.get("build", "logs"), "logs");
        assertTrue(Paths.get("build", "logs", "debug.log"), "logs");

        // logs/
        assertTrue(Paths.get("logs", "debug.log"), "logs/");
        assertTrue(Paths.get("logs", "latest", "foo.bar"), "logs/");
        assertTrue(Paths.get("build", "logs", "foo.bar"), "logs/");
        assertTrue(Paths.get("build", "logs", "latest", "debug.log"), "logs/");
    }

    @Test
    public void testDirectoryInput() throws Exception {
        assertTrue(Paths.get("code", "a.java"), "code/**");
        assertTrue(Paths.get("code", "a.java"), "code/");
        assertTrue(Paths.get("code", "a", "z", "a.java"), "code/");
    }

    @Test
    public void testIgnored() throws IOException {
        Path path = Paths.get(IGNORE_FILE_DIR, ".csarignore");
        List<Rule> rules = IgnoreFiles.read(IGNORE_FILE_DIR, path);

        // Project files
        assertTrue(rules, Paths.get(IGNORE_FILE_DIR, "csar.iml"));
        assertTrue(rules, Paths.get(IGNORE_FILE_DIR, ".iml"));

        // Code/class files
        assertTrue(rules, Paths.get(IGNORE_FILE_DIR, "!test.java"));
        assertTrue(rules, Paths.get(IGNORE_FILE_DIR, "#test.java"));
        assertTrue(rules, Paths.get(IGNORE_FILE_DIR, "out", "license.txt"));
        assertFalse(rules, Paths.get(IGNORE_FILE_DIR, "out", "code.java"));
        assertFalse(rules, Paths.get(IGNORE_FILE_DIR, "code.java"));
    }
}
