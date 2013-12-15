package ru.fizteh.fivt.students.dubovpavel.proxy;

import ru.fizteh.fivt.students.dubovpavel.parallel.TableStoreableParallelBuilder;

import java.util.ArrayList;

public class TableSessionalBuilder extends TableStoreableParallelBuilder {
    @Override
    public TableSessional construct() {
        ArrayList<Class<?>> fieldsCopy = cloneFields(fields);
        return new TableSessional(dir, dispatcher, fieldsCopy);
    }
}
