package ru.fizteh.fivt.students.mikhaylova_daria.db;

import org.junit.Test;


interface ProxyTestInterface {
    @Test
    void writerNullShouldFail();

    @Test
    void implementNullShouldFail();

    @Test
    void shouldTargetException();

    @Test
    void primitiveTypes();

    @Test
    void iterableTypes();

    @Test
    void arrayTypes();

    @Test
    void correctRecordStructure();

    @Test
    void returnValueIsNull();

    @Test
    void returnValueIsVoid();

    @Test
    void logNotOverrideMethodShouldNoLog();

}
