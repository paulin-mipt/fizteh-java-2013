package ru.fizteh.fivt.students.baldindima.junit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class DataBaseTable implements TableProvider {

    private String tableDirectory;
    private Map<String, DataBase> tables;

    public DataBaseTable(String nTableDirectory) {
        tableDirectory = nTableDirectory;
        tables = new HashMap();
    }

    public DataBase getTableFromMap(String name) {
        if (!tables.containsKey(name)) {
            try {
                tables.put(name, new DataBase(name));
            } catch (IOException e) {
                throw new RuntimeException("cannot get table");
            }
        }
        return tables.get(name);
    }

    public void deleteTableFromMap(String name) {
        if (tables.containsKey(name)) {
            tables.remove(name);
        }
    }

    private void checkName(String name) {
        if ((name == null) || name.trim().length() == 0) {
            throw new IllegalArgumentException("Cannot create table");
        }

        if (name.matches("[" + '"' + "'\\/:/*/?/</>/|/.\\\\]+") || name.contains(File.separator)
                || name.contains(".")) {
            throw new RuntimeException("Wrong symbols");
        }
    }

    public Table createTable(String name) {
        checkName(name);
        String path = tableDirectory + File.separator + name;

        File file = new File(path);

        if (file.exists()) {
            return null;
        }

        if (!file.mkdir()) {
            throw new RuntimeException("Cannot create table " + name);
        }

        return getTableFromMap(path);
    }

    public Table getTable(String name) {
        checkName(name);
        String path = tableDirectory + File.separator + name;

        File file = new File(path);
        if ((!file.exists()) || (file.isFile())) {
            return null;
        }
        return getTableFromMap(path);
    }

    public void removeTable(String name) {
        checkName(name);
        String path = tableDirectory + File.separator + name;

        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalStateException("Table not exist");
        }

        DataBase base = getTableFromMap(path);
        try {
            base.drop();
        } catch (IOException e) {
            throw new RuntimeException("Cannot delete a table " + name);
        }
        if (!file.delete()) {
            throw new RuntimeException("Cannot delete a table " + name);
        }
        deleteTableFromMap(path);
    }


}
