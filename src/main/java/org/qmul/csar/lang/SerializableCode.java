package org.qmul.csar.lang;

public interface SerializableCode { // TODO document + define the indentation String here

    default String toPseudoCode() {
        return toPseudoCode(0);
    }

    String toPseudoCode(int indentation);
}
