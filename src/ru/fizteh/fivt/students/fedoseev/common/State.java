package ru.fizteh.fivt.students.fedoseev.common;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public interface State<T> {
    File getCurDir();

    T getCurTable();

    void createTable(String curTableName) throws IOException, ClassNotFoundException;

    void setCurTable(String curTableName);

    void removeTable(String curTableName) throws IOException;

    void saveTable(T table) throws IOException;

    void readTableOff(T table) throws IOException, ParseException;

    String get(String key);

    String put(String key, String value) throws ParseException;

    String remove(String key);

    int commit();

    int rollback();

    int size();

    int getDiffSize();

    File getCurTableDir();

    void clearContentAndDiff();

    boolean usingTables();
}
