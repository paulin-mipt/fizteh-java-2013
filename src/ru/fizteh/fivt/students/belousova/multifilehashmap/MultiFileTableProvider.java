package ru.fizteh.fivt.students.belousova.multifilehashmap;

import java.io.File;

public class MultiFileTableProvider extends AbstractTableProvider<ChangesCountingTable> implements ChangesCountingTableProvider {
    public MultiFileTableProvider(File directory) {
        if (directory == null) {
            throw new IllegalArgumentException("null directory");
        }
        if (!directory.exists()) {
            directory.mkdir();
        } else if (!directory.isDirectory()) {
            throw new IllegalArgumentException("'" + directory.getName() + "' is not a directory");
        }


        dataDitectory = directory;
        for (File tableFile : directory.listFiles()) {
            tableMap.put(tableFile.getName(), new MultiFileTable(tableFile));
        }
    }

    @Override
    public ChangesCountingTable createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("incorrect name");
        }
        File tableDirectory = new File(dataDitectory, name);
        if (!tableDirectory.mkdir()) {
            return null;
        }

        MultiFileTable table = new MultiFileTable(tableDirectory);
        tableMap.put(name, table);
        return table;
    }
}
