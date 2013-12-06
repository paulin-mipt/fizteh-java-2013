package ru.fizteh.fivt.students.dobrinevski.jUnit;

import ru.fizteh.fivt.storage.strings.*;

public class MyTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String dir) {
        return new MyTableProvider(dir);
    }

    public MyTableProviderFactory() {

    }
}
