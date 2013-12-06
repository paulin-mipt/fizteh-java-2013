package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.students.dubovpavel.strings.WrappedMindfulDataBaseMultiFileHashMapBuilder;

import java.util.ArrayList;

public class TableStoreableBuilder extends WrappedMindfulDataBaseMultiFileHashMapBuilder {
    protected ArrayList<Class<?>> fields;

    protected ArrayList<Class<?>> cloneFields(ArrayList<Class<?>> types) {
        if (types == null) {
            return new ArrayList<>();
        } else {
            return (ArrayList<Class<?>>) types.clone();
        }
    }

    public void setFields(ArrayList<Class<?>> types) {
        fields = cloneFields(types);
    }

    public TableStoreable construct() {
        ArrayList<Class<?>> fieldsCopy = cloneFields(fields);
        return new TableStoreable(dir, dispatcher, fieldsCopy);
    }
}
