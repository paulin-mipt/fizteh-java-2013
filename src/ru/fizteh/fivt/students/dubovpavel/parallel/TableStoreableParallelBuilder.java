package ru.fizteh.fivt.students.dubovpavel.parallel;

import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreableBuilder;

import java.util.ArrayList;

public class TableStoreableParallelBuilder extends TableStoreableBuilder {
    @Override
    public TableStoreableParallel construct() {
        ArrayList<Class<?>> fieldsCopy = cloneFields(fields);
        return new TableStoreableParallel(dir, dispatcher, fieldsCopy);
    }
}
