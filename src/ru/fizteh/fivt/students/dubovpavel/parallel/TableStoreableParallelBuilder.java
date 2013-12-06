package ru.fizteh.fivt.students.dubovpavel.parallel;

import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreable;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreableBuilder;

import java.util.ArrayList;

public class TableStoreableParallelBuilder extends TableStoreableBuilder {
    @Override
    public TableStoreable construct() {
        ArrayList<Class<?>> fieldsCopy = cloneFields(fields);
        return new TableStoreableParallel(dir, dispatcher, fieldsCopy);
    }
}
