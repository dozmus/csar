package com.purecs.ignorefiles;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

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
