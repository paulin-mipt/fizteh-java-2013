package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MyTableProviderFactory implements TableProviderFactory {
    @Override
    public MyTableProvider create(String dir) {
        MyTableProvider provider = new MyTableProvider();
        provider.createTable(dir);
        return provider;
    }
}
