package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MyTableProvider implements TableProvider {
    private Map<String, MyTable> tables = new HashMap<String, MyTable>();

    @Override
    public MyTable getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Incorrect table name.");
        } else if (!Paths.get(name).getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
            throw new IllegalArgumentException("Incorrect table name.");
        }
        return tables.get(name);
    }

    @Override
    public MyTable createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Incorrect table name.");
        } else if (!Paths.get(name).getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
            throw new IllegalArgumentException("Incorrect table name.");
        }
        if (tables.containsKey(name)) {
            return null;
        }
        MyTable table = new MyTable(name);
        tables.put(name, table);
        return table;
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Incorrect table name.");
        } else if (!Paths.get(name).getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
            throw new IllegalArgumentException("Incorrect table name.");
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException("Have no table to remove.");
        }
        tables.remove(name);
    }
}
