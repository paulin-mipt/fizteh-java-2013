package ru.fizteh.fivt.students.asaitgalin.multifilehashmap;

import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.extensions.ChangesCountingTable;
import ru.fizteh.fivt.students.asaitgalin.multifilehashmap.extensions.ChangesCountingTableProvider;
import ru.fizteh.fivt.students.asaitgalin.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileTableProvider implements ChangesCountingTableProvider {
    private static final String TABLE_NAME_FORMAT = "[A-Za-zА-Яа-я0-9]+";
    private Map<String, ChangesCountingTable> tableMap = new HashMap<>();
    private File dbDirectory;

    public MultiFileTableProvider(File dbDirectory) {
        if (dbDirectory == null) {
            throw new IllegalArgumentException("failed to create provider: name is null");
        }
        if (!dbDirectory.isDirectory()) {
            throw new IllegalArgumentException("failed to create provider: name is not a folder");
        }
        for (File f : dbDirectory.listFiles()) {
            if (f.isFile()) {
                continue;
            }
            MultiFileTable table = new MultiFileTable(f, f.getName());
            try {
                table.load();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            tableMap.put(table.getName(), table);
        }

        this.dbDirectory = dbDirectory;
    }

    @Override
    public ChangesCountingTable getTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("failed to get table: invalid name");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new RuntimeException("failed to get table: incorrect table name");
        }
        return tableMap.get(name);
    }

    @Override
    public ChangesCountingTable createTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("failed to create table: invalid name");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new RuntimeException("failed to create table: incorrect table name");
        }
        File tableDir = new File(dbDirectory, name);
        if (tableDir.exists()) {
            return null;
        }
        tableDir.mkdir();
        MultiFileTable table = new MultiFileTable(tableDir, name);
        tableMap.put(table.getName(), table);
        return table;
    }

    @Override
    public void removeTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("failed to remove table: invalid name");
        }
        File tableDir = new File(dbDirectory, name);
        if (!tableDir.exists()) {
            throw new IllegalStateException("failed to remove table: table does not exist");
        }
        tableMap.remove(name);
        try {
            FileUtils.deleteRecursively(tableDir);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
