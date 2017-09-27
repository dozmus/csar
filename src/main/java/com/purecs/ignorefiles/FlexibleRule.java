package com.purecs.ignorefiles;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

/**
 * A rule which may represent a file or a directory.
 */
public class FlexibleRule extends Rule {

    private String dirText;
    private PathMatcher dirMatcher;

    /**
     * Creates a new {@link FlexibleRule}. This is a rule for which the <tt>String</tt> argument may represent a
     * directory or a file. So two matchers are created, one for <tt>regText</tt> and one for <tt>regText + "/**"</tt>.
     * <p>
     * This must not be used with <tt>regText</tt> which is not ambiguous, i.e. one which ends with <tt>"/"</tt> or
     * <tt>"/**"</tt>.
     * @param type the rule type
     * @param regText a glob rule
     */
    public FlexibleRule(Type type, String regText) {
        super(type, regText);

        // Directory matcher
        dirText = regText + "/**";
        dirMatcher = FileSystems.getDefault().getPathMatcher("glob:" + dirText);
    }

    public boolean matches(Path path) {
        path = path.toAbsolutePath().normalize();
        return super.matches(path) || dirMatcher.matches(path);
    }

    public String getDirText() {
        return dirText;
    }

    @Override
    public String toString() {
        return String.format("FlexibleRule{dirText='%s'} %s", dirText, super.toString());
    }
}
