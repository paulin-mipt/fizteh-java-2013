package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.text.ParseException;

import java.io.IOException;

public interface State {
    boolean currentTableIsNull();

    String getFromCurrentTable(String key);
    String putToCurrentTable(String key, String value) throws ParseException;
    String removeFromCurrentTable(String key);

    boolean createTable(String name);
    boolean createStoreableTable(String name, String types) throws IOException;
    boolean removeTable(String name) throws IOException;
    void checkDropTableUsing(String name);
    boolean isTableExists(String name);
    void changeCurrentTable(String name);
    int getDiffCount();

    void writeTable() throws IOException;
    void readTable() throws IOException;

    int commit() throws IOException;
    int rollback();
    int size();
}
