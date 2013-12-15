package ru.fizteh.fivt.students.inaumov.filemap;

public interface FileMapShellState<Table, Key, Value> {
    Value put(Key key, Value value);

    Value get(Key key);

    Value remove(Key key);

    int size();

    int commit();

    int rollback();

    Table getTable();

    String keyToString(Key key);

    String valueToString(Value value);

    Key parseKey(String key);

    Value parseValue(String value);

    String[] parsePutCommand(String argumentLine);
}
