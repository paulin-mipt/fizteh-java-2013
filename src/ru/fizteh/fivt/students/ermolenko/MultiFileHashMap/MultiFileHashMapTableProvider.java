package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.ermolenko.shell.Rm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapTableProvider implements TableProvider {

    private Map<String, MultiFileHashMapTable> mapOfTables;
    private File currentDir;

    public MultiFileHashMapTableProvider(File inDir) {

        if (inDir == null) {
            throw new IllegalArgumentException("null directory");
        }
        if (!inDir.isDirectory()) {
            throw new IllegalArgumentException("not a directory");
        }
        mapOfTables = new HashMap<String, MultiFileHashMapTable>();
        currentDir = inDir;
        File[] fileMas = currentDir.listFiles();
        if (fileMas.length != 0) {
            for (File fileMa : fileMas) {
                if (fileMa.isDirectory()) {
                    mapOfTables.put(fileMa.getName(), new MultiFileHashMapTable(fileMa));
                }
            }
        }
    }

    @Override
    public MultiFileHashMapTable getTable(String name) {

        if (name == null) {
            throw new IllegalArgumentException("null name to get");
        }
        if (!name.matches("[a-zA-Zа-яА-Я0-9]+")) {
            throw new IllegalArgumentException("incorrect name to get");
        }

        if (!mapOfTables.containsKey(name)) {
            return null;
        }

        return mapOfTables.get(name);
    }

    @Override
    public MultiFileHashMapTable createTable(String name) {

        if (name == null) {
            throw new IllegalArgumentException("null name to create");
        }
        if (!name.matches("[a-zA-Zа-яА-Я0-9]+")) {
            throw new IllegalArgumentException("incorrect name to create");
        }

        File dirOfTable = new File(currentDir, name);
        if (!dirOfTable.mkdir()) {
            return null;
        }

        MultiFileHashMapTable table = new MultiFileHashMapTable(dirOfTable);

        Table tmp = mapOfTables.get(name);
        if (tmp != null) {
            return null;
        }
        mapOfTables.put(name, table);
        return table;

    }

    @Override
    public void removeTable(String name) {

        if (name == null || !name.matches("[a-zA-Zа-яА-Я0-9]+")) {
            throw new IllegalArgumentException("incorrect table name to remove");
        }
        if (!mapOfTables.containsKey(name)) {
            throw new IllegalStateException("table doesn't exist");
        }

        File dirOfTable = new File(currentDir, name);
        try {
            Rm.remove(dirOfTable.getCanonicalFile().toPath());
        } catch (IOException e) {
            System.err.println(e);
        }

        mapOfTables.remove(name);
    }
}
