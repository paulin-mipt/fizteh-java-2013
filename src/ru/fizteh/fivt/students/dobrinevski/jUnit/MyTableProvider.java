package ru.fizteh.fivt.students.dobrinevski.jUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MultiFileHashMapCommands;
import ru.fizteh.fivt.students.dobrinevski.multiFileHashMap.MyMultiHashMap;
import ru.fizteh.fivt.students.dobrinevski.shell.Command;

import java.io.File;
import java.util.HashMap;

public class MyTableProvider implements TableProvider {
    HashMap<String, MyTable> bTables;
    File root;

    public MyTableProvider(String dir) throws IllegalArgumentException {
        if ((dir == null) || (dir.equals(""))) {
            throw new IllegalArgumentException("directory is null");
        }
        root = new File(dir);
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException("failed in open root directory");
        }
        bTables = new HashMap<String, MyTable>();
    }

    public Table getTable(String name) throws IllegalArgumentException {
        if ((name == null) || (name.equals(""))) {
            throw new IllegalArgumentException("Table name is null");
        }
        if (bTables.get(name) != null) {
            return bTables.get(name);
        }
        if (!isValidFileName(name)) {
            throw new IllegalArgumentException("Invalid name");
        }
        MyMultiHashMap newFileHashMap = new MyMultiHashMap();
        String[] args = new String[2];
        args[1] = name;
        try {
            Command use = new MultiFileHashMapCommands.Use(newFileHashMap, root);
            use.innerExecute(args);
            MyTable newTable = new MyTable(root, name, newFileHashMap);
            bTables.put(name, newTable);
            if (use.returnValue[0].lastIndexOf(" not exists") != -1) {
                return null;
            } else {
                return newTable;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Table createTable(String name) {
        if ((name == null) || (name.equals(""))) {
            throw new IllegalArgumentException("Table name is null");
        }
        if (bTables.get(name) != null) {
            return null;
        }
        if (!isValidFileName(name)) {
            throw new IllegalArgumentException("Invalid name");
        }
        MyMultiHashMap newFileHashMap = new MyMultiHashMap();
        String[] args = new String[2];
        args[1] = name;
        try {
            Command create = new MultiFileHashMapCommands.Create(newFileHashMap, root);
            create.innerExecute(args);
            if (create.returnValue[0].lastIndexOf(" exists") != -1) {
                return null;
            }
            new MultiFileHashMapCommands.Use(newFileHashMap, root).innerExecute(args);
            MyTable newTable = new MyTable(root, name, newFileHashMap);
            bTables.put(name, newTable);
            return newTable;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void removeTable(String name) {
        if ((name == null) || (name.equals(""))) {
            throw new IllegalArgumentException("Table name is null");
        }
        if (!isValidFileName(name)) {
            throw new IllegalArgumentException("Invalid name");
        }
        String[] args = new String[2];
        args[1] = name;
        Command drop;
        try {
            if (bTables.get(name) != null) {
                drop = new MultiFileHashMapCommands.Drop(bTables.get(name).baseHashMap, root);
                bTables.remove(name);
            } else {
                drop = new MultiFileHashMapCommands.Drop(new MyMultiHashMap(), root);
            }
            drop.innerExecute(args);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (drop.returnValue[0].lastIndexOf(" not exists") != -1) {
            throw new IllegalStateException(name + "not exists");
        }
    }

    private static boolean isValidFileName(String name) {
        return !(name.contains("\\") || name.contains("/")
                || name.contains(":") || name.contains("*")
                || name.contains("?") || name.contains("\"")
                || name.contains("<") || name.contains(">")
                || name.contains("\n") || name.contains(" ")
                || name.contains("|") || name.contains("\t"));
    }
}
