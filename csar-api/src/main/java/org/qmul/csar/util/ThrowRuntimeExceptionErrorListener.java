package org.qmul.csar.util;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * An error listener which throws a {@link RuntimeException} when
 * {@link ANTLRErrorListener#syntaxError(Recognizer, Object, int, int, String, RecognitionException)} is invoked.
 */
public class ThrowRuntimeExceptionErrorListener extends DummyANTLRErrorListener {

    private final String name;

    public ThrowRuntimeExceptionErrorListener(String name) {
        this.name = name;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object badSymbol, int line, int charPosInLine, String msg,
            RecognitionException e) {
        String pos = line + ":" + charPosInLine;
        throw new RuntimeException("syntax error parsing " + name + " for symbol `" + badSymbol + "` at " + pos);
    }
}
