package com.purecs.ignorefiles;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

public class Rule {

    private final Type type;
    private final String regText;
    private final PathMatcher regMatcher;

    /**
     * Creates a new {@link Rule}. This is a rule for which the <tt>String</tt> argument may represent a directory or a
     * file.
     * @param type the rule type
     * @param regText a glob rule
     */
    public Rule(Type type, String regText) {
        this.type = type;
        this.regText = regText;
        regMatcher = FileSystems.getDefault().getPathMatcher("glob:" + regText);
    }

    /**
     *
     * @param baseDirectory
     * @param input
     * @return
     */
    public static Rule parse(String baseDirectory, String input) {
        // Source: https://github.com/EE/gitignore-to-glob/blob/master/lib/gitignore-to-glob.js
        // Source: https://git-scm.com/docs/gitignore
        Rule.Type type = input.startsWith("!") ? Type.UN_IGNORE : Type.IGNORE;

        if (input.startsWith("!") || input.startsWith("\\!") || input.startsWith("\\#"))
            input = input.substring(1);

        // normalize
        input = input.replace("\\", "/");

        // parse
        String prepend = input.startsWith("**/") ? "" : "**/";

        if (input.startsWith("/")) {
            input = Paths.get(baseDirectory, input).toAbsolutePath().toString().replace("\\", "/");
            return new FlexibleRule(type, input);
        } else if (input.endsWith("/")) {
            return new Rule(type, prepend + input + "**");
        } else if (input.endsWith("/**")) {
            return new Rule(type, prepend + input);
        } else if (input.matches("^.*/\\s+/$")) { // trailing spaces
            return new Rule(type, prepend + input);
        } else {
            return new FlexibleRule(type, prepend + input);
        }
    }

    public Type getType() {
        return type;
    }

    public String getRegText() {
        return regText;
    }

    protected PathMatcher getRegMatcher() {
        return regMatcher;
    }

    public boolean matches(Path path) {
        path = path.toAbsolutePath().normalize();
        return regMatcher.matches(path);
    }

    @Override
    public String toString() {
        return String.format("Rule{type=%s, regText='%s'}", type, regText);
    }

    public enum Type {
        IGNORE, UN_IGNORE
    }
}
