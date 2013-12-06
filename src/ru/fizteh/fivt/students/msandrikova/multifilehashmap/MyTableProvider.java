package ru.fizteh.fivt.students.msandrikova.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.msandrikova.shell.Utils;

public class MyTableProvider implements ChangesCountingTableProvider {
    private File currentDirectory;
    private Map<String, ChangesCountingTable> mapOfTables = new HashMap<String, ChangesCountingTable>();
    

    public MyTableProvider(File dir) throws IllegalArgumentException {
        this.currentDirectory = dir;
        if (!this.currentDirectory.exists()) {
            throw new IllegalArgumentException("Given directory does not exist.");
        } else if (!this.currentDirectory.isDirectory()) {
            throw new IllegalArgumentException("Given directory name does not correspond to directory.");
        }
    }

    @Override
    public ChangesCountingTable getTable(String name) throws IllegalArgumentException {
        if (Utils.isEmpty(name) || !Utils.testBadSymbols(name)) {
            throw new IllegalArgumentException("Table name can not be null or empty "
                    + "or contain bad symbols");
        }
        if (this.mapOfTables.get(name) == null) {
            File tablePath = new File(this.currentDirectory, name);
            if (tablePath.exists()) {
                if (tablePath.isDirectory()) {
                    ChangesCountingTable newTable = new MyTable(this.currentDirectory, name);
                    this.mapOfTables.put(name, newTable);
                } else {
                    throw new IllegalArgumentException("File with name '" + name 
                            + "' must be directory.");
                }
            }
        }
        return this.mapOfTables.get(name);
    }

    @Override
    public ChangesCountingTable createTable(String name) throws IllegalArgumentException {
        if (Utils.isEmpty(name) || !Utils.testBadSymbols(name)) {
            throw new IllegalArgumentException("Table name can not be null or "
                    + "empty or contain bad symbols");
        }
        if (this.mapOfTables.get(name) != null) {
            return null;
        }
        File tablePath = new File(this.currentDirectory, name);
        if (tablePath.exists()) {
            if (tablePath.isDirectory()) {
                ChangesCountingTable newTable = new MyTable(this.currentDirectory, name);
                this.mapOfTables.put(name, newTable);
                return newTable;
            } else {
                throw new IllegalArgumentException("File with name '" + name + "' must be directory.");
            }
        }
        ChangesCountingTable newTable = new MyTable(this.currentDirectory, name);
        this.mapOfTables.put(name, newTable);
        return newTable;
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (Utils.isEmpty(name)) {
            throw new IllegalArgumentException("Table name can not be null or empty");
        }
        File tablePath = new File(this.currentDirectory, name);
        if (this.mapOfTables.get(name) == null) {
            if (tablePath.exists()) {
                if (tablePath.isDirectory()) {
                    ChangesCountingTable newTable = new MyTable(this.currentDirectory, name);
                    this.mapOfTables.put(name, newTable);
                } else {
                    Utils.generateAnError("File with name \"" + name 
                            + "\" exists and is not directory.", "drop", false);
                }
            } else {
                throw new IllegalStateException();
            }
        }
        try {
            Utils.remover(tablePath, "drop", false);
        } catch (IOException e) {
            Utils.generateAnError("Fatal error during deleting", "drop", false);
        }
        this.mapOfTables.remove(name);
    }

}
