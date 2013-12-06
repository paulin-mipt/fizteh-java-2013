package ru.fizteh.fivt.students.vyatkina.database;

public interface DatabaseAdapter {

    boolean tableIsSelected();

    boolean createTable(String name);

    boolean createTable(String name, String structedSignature);

    boolean useTable(String name);

    boolean dropTable(String name);

    void saveChangesOnExit();

    String get(String key);

    String put(String key, String value);

    String remove(String key);

    int commit();

    int rollback();

    int size();

    int unsavedChanges();
}
