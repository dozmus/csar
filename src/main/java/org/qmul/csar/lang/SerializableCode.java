package org.qmul.csar.lang;

public interface SerializableCode {

    default String toPseudoCode() {
        return toPseudoCode(0);
    }

    String toPseudoCode(int indentation);
}
