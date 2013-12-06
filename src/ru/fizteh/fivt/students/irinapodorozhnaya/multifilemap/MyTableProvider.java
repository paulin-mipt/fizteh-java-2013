package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.extend.ExtendProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.extend.ExtendTable;
import ru.fizteh.fivt.students.irinapodorozhnaya.shell.CommandRemove;

public class MyTableProvider implements ExtendProvider {
    

    private final File dataBaseDir;
    private final Map<String, ExtendTable> tables = new HashMap<>();
    private static final String STRING_NAME_FORMAT = "[a-zA-Z0-9А-Яа-я]+";
    
    public MyTableProvider(File dataBaseDir) {
        this.dataBaseDir = dataBaseDir;
        for (String tableName: dataBaseDir.list()) {
            tables.put(tableName, new MyTable(tableName, dataBaseDir));
            tables.get(tableName).loadAll();
        }
    }
   
    @Override
    public ExtendTable getTable(String name) {
        if (name == null || !name.matches(STRING_NAME_FORMAT)) {
            throw new IllegalArgumentException("table name is null or has illegal name");
        }
        return tables.get(name);
    }

    @Override
    public void removeTable(String name) {
        if (name == null || !name.matches(STRING_NAME_FORMAT)) {
            throw new IllegalArgumentException("table name is null or has illegal name");
        }
        if (tables.remove(name) == null) {
            throw new IllegalStateException(name + " not exists");
        }
        File table = new File(dataBaseDir, name);
        CommandRemove.deleteRecursivly(table);
    }
    
    @Override
    public ExtendTable createTable(String name) {
        if (name == null || !name.matches(STRING_NAME_FORMAT)) {
            throw new IllegalArgumentException("table name is null or has illegal name");
        }
        File table = new File(dataBaseDir, name);
        if (table.isDirectory()) {
            return null;
        }
        if (!table.mkdir()) {
            throw new IllegalArgumentException("table has illegal name");
        }
        tables.put(name, new MyTable(name, dataBaseDir));
        return tables.get(name);
    }
}
