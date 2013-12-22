package ru.fizteh.fivt.students.dubovpavel.filemap;

import java.text.ParseException;

public interface Serial<T> {
    public static class SerialException extends Exception {
        public SerialException(String msg) {
            super(msg);
        }
    }

    String serialize(T obj) throws SerialException;

    T deserialize(String obj) throws SerialException, ParseException;
}
