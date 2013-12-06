package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.util.HashMap;
import java.util.Map;

public class MyTableProvider implements TableProvider {
    private Map<String, MyTable> tables = new HashMap<String, MyTable>();

    @Override
    public MyTable getTable(String name) {
        return tables.get(name);
    }

    @Override
    public MyTable createTable(String name) {
        MyTable table = new MyTable(name);
        tables.put(name, table);
        return table;
    }

    @Override
    public void removeTable(String name) {
        tables.remove(name);
    }
}
