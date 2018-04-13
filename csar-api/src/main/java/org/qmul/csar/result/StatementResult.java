package org.qmul.csar.result;

import org.qmul.csar.lang.Statement;

import java.nio.file.Path;

/**
 * A match from the searching performed by {@link org.qmul.csar.Csar} which yielded a {@link Statement}.
 */
public class StatementResult extends Result {

    private final Statement statement;

    /**
     * Creates a new StatementResult according to the arguments provided.
     *
     * @param path the path which contained this result
     * @param lineNumber the line at which the result was found
     * @param codeFragment the result's code fragment
     * @param statement the result's statement
     */
    public StatementResult(Path path, int lineNumber, String codeFragment, Statement statement) {
        super(path, lineNumber, codeFragment);
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }
}
