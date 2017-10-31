package com.purecs.ignorefiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IgnoreFiles {

    // XXX do trailing spaces enclosed by \ work properly? why would they even be necessary

    /**
     * Returns if the argument file is ignored by the provided list of rules. If any rule in the argument list
     * un-ignores the specified file, then <tt>false</tt> is returned.
     *
     * @param path the file to check against the rules
     * @param rules the list of rules to check the path against
     * @return returns if the path is ignored by the list of rules
     */
    public static boolean ignored(Path path, List<Rule> rules) {
        boolean ignored = false;

        for (Rule rule : rules) {
            if (rule.matches(path)) {
                if (rule.getType() == Rule.Type.UN_IGNORE)
                    return false;
                else
                    ignored = true;
            }
        }
        return ignored;
    }

    /**
     * Returns the result of calling {@link #ignored(Path, List)} with the argument {@link Rule} as the single entry
     * in the {@link List<Rule>}.
     *
     * @param path the file to check against the rule
     * @param rule the rule to check the path against
     * @return returns if the path is ignored by the rule
     */
    public static boolean ignored(Path path, Rule rule) {
        return ignored(path, Collections.singletonList(rule));
    }

    /**
     * Returns the {@link List<Rule>} expressed in the argument file.
     *
     * @param baseDirectory the base directory of the parsed rules
     * @param path the file to read
     * @return returns the {@link List<Rule>} expressed in the argument file.
     * @throws IOException if an I/O error occurs
     */
    public static List<Rule> read(String baseDirectory, Path path) throws IOException {
        List<Rule> rules = new ArrayList<>();

        for (String line : Files.readAllLines(path)) {
            line = line.trim();

            if (line.length() == 0 || line.startsWith("#"))
                continue;

            if (line.startsWith("\\#"))
                line = line.substring(1);
            rules.add(Rule.parse(baseDirectory, line));
        }
        return rules;
    }
}
