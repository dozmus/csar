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

    private static void assertIgnored(Path path, String ruleText) {
        Rule rule = Rule.parse(BASE_DIR, ruleText);
        Assert.assertTrue(IgnoreFiles.ignored(path, rule));
    }

    private static void assertNotIgnored(Path path, String ruleText) {
        Rule rule = Rule.parse(BASE_DIR, ruleText);
        Assert.assertFalse(IgnoreFiles.ignored(path, rule));
    }

    private void assertIgnored(List<Rule> rules, Path path) {
        Assert.assertTrue(IgnoreFiles.ignored(path, rules));
    }

    private void assertNotIgnored(List<Rule> rules, Path path) {
        Assert.assertFalse(IgnoreFiles.ignored(path, rules));
    }

    @Test
    public void testFileInput() throws Exception {
        assertIgnored(Paths.get("code.java"), "*.java");
        assertIgnored(Paths.get("code"), "*");
        assertIgnored(Paths.get("code"), "code");
        assertIgnored(Paths.get("code", "a.c"), "code");
        assertIgnored(Paths.get("code.java"), "code.java");
        assertIgnored(Paths.get("code.java"), "c[o|a]de.j??a");
        assertIgnored(Paths.get("code.java"), "code.*");
        assertIgnored(Paths.get("code.java"), "code*");
        assertNotIgnored(Paths.get("code.java"), "z*");
        assertNotIgnored(Paths.get("code.java"), "!code.java");
        assertIgnored(Paths.get("code.java"), "/code.java");
        assertNotIgnored(Paths.get("a", "code.java"), "/code.java");
        assertIgnored(Paths.get("!code.java"), "\\!code.java");

        /*
         * The following test cases are from: https://www.atlassian.com/git/tutorials/gitignore (accessed: 29/09/17)
         * The only modification applied is that they have been translated to this testing syntax.
         * The content on that website is licensed under https://creativecommons.org/licenses/by/2.5/au/
         */
        // **/logs
        assertIgnored(Paths.get("logs", "debug.log"), "**/logs");
        assertIgnored(Paths.get("logs", "monday", "foo.bar"), "**/logs");
        assertIgnored(Paths.get("builds", "logs", "debug.log"), "**/logs");

        // **/logs/debug.log
        assertIgnored(Paths.get("logs", "debug.log"), "**/logs/debug.log");
        assertIgnored(Paths.get("build", "logs", "debug.log"), "**/logs/debug.log");
        assertNotIgnored(Paths.get("logs", "build", "debug.log"), "**/logs/debug.log");

        // *.log
        assertIgnored(Paths.get("debug.log"), "*.log");
        assertIgnored(Paths.get("foo.log"), "*.log");
        assertIgnored(Paths.get(".log"), "*.log");
        assertIgnored(Paths.get("logs", "debug.log"), "*.log");

        // debug?.log
        assertIgnored(Paths.get("debug0.log"), "debug?.log");
        assertIgnored(Paths.get("debugg.log"), "debug?.log");
        assertNotIgnored(Paths.get("debug10.log"), "debug?.log");

        // debug[0-9].log
        assertIgnored(Paths.get("debug0.log"), "debug[0-9].log");
        assertIgnored(Paths.get("debug1.log"), "debug[0-9].log");
        assertNotIgnored(Paths.get("debug10.log"), "debug[0-9].log");

        // debug[01].log
        assertIgnored(Paths.get("debug0.log"), "debug[01].log");
        assertIgnored(Paths.get("debug1.log"), "debug[01].log");
        assertNotIgnored(Paths.get("debug2.log"), "debug[01].log");
        assertNotIgnored(Paths.get("debug01.log"), "debug[01].log");

        // debug[!01].log
        assertIgnored(Paths.get("debug2.log"), "debug[!01].log");
        assertNotIgnored(Paths.get("debug0.log"), "debug[!01].log");
        assertNotIgnored(Paths.get("debug1.log"), "debug[!01].log");
        assertNotIgnored(Paths.get("debug01.log"), "debug[!01].log");

        // debug[a-z].log
        assertIgnored(Paths.get("debuga.log"), "debug[a-z].log");
        assertIgnored(Paths.get("debugb.log"), "debug[a-z].log");
        assertNotIgnored(Paths.get("debug1.log"), "debug[a-z].log");

        // logs
        assertIgnored(Paths.get("logs"), "logs");
        assertIgnored(Paths.get("logs", "debug.log"), "logs");
        assertIgnored(Paths.get("logs", "latest", "foo.bar"), "logs");
        assertIgnored(Paths.get("build", "logs"), "logs");
        assertIgnored(Paths.get("build", "logs", "debug.log"), "logs");

        // logs/
        assertIgnored(Paths.get("logs", "debug.log"), "logs/");
        assertIgnored(Paths.get("logs", "latest", "foo.bar"), "logs/");
        assertIgnored(Paths.get("build", "logs", "foo.bar"), "logs/");
        assertIgnored(Paths.get("build", "logs", "latest", "debug.log"), "logs/");
    }

    @Test
    public void testDirectoryInput() throws Exception {
        assertIgnored(Paths.get("code", "a.java"), "code/**");
        assertIgnored(Paths.get("code", "a.java"), "code/");
        assertIgnored(Paths.get("code", "a", "z", "a.java"), "code/");
    }

    @Test
    public void testIgnored() throws IOException {
        Path path = Paths.get(IGNORE_FILE_DIR, ".csarignore");
        List<Rule> rules = IgnoreFiles.read(IGNORE_FILE_DIR, path);

        // Project files
        assertIgnored(rules, Paths.get(IGNORE_FILE_DIR, "csar.iml"));
        assertIgnored(rules, Paths.get(IGNORE_FILE_DIR, ".iml"));

        // Code/class files
        assertIgnored(rules, Paths.get(IGNORE_FILE_DIR, "!test.java"));
        assertIgnored(rules, Paths.get(IGNORE_FILE_DIR, "#test.java"));
        assertIgnored(rules, Paths.get(IGNORE_FILE_DIR, "out", "license.txt"));
        assertNotIgnored(rules, Paths.get(IGNORE_FILE_DIR, "out", "code.java"));
        assertNotIgnored(rules, Paths.get(IGNORE_FILE_DIR, "code.java"));
    }
}
