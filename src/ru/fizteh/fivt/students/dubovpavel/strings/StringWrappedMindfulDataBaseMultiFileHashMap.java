package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;

import java.io.File;

public class StringWrappedMindfulDataBaseMultiFileHashMap extends WrappedMindfulDataBaseMultiFileHashMap<String>
        implements Table {
    public StringWrappedMindfulDataBaseMultiFileHashMap(File path, Dispatcher dispatcher) {
        super(path, dispatcher, new StringTransformer());
    }

    @Override
    public String put(String key, String value) {
        if (value == null || value.contains("\n")) {
            throw new IllegalArgumentException();
        }
        return super.put(key, value);
    }
}
