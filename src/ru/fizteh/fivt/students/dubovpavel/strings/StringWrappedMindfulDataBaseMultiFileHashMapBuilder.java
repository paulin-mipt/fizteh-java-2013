package ru.fizteh.fivt.students.dubovpavel.strings;

public class StringWrappedMindfulDataBaseMultiFileHashMapBuilder
        extends WrappedMindfulDataBaseMultiFileHashMapBuilder<String> {
    public StringWrappedMindfulDataBaseMultiFileHashMap construct() {
        assert (dir != null && dispatcher != null);
        return new StringWrappedMindfulDataBaseMultiFileHashMap(dir, dispatcher);
    }
}
