package ru.fizteh.fivt.students.inaumov.multifilemap.base;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileMapUtils;
import ru.fizteh.fivt.students.inaumov.multifilemap.MultiFileStringDatabaseTable;

import java.util.HashMap;
import java.io.File;
import java.util.regex.*;

public class DatabaseTableProvider implements TableProvider {
    private static final String CORRECT_SYMBOLS = "[^0-9a-zA-Zа-яА-Я]";
    private static final Pattern PATTERN = Pattern.compile(CORRECT_SYMBOLS);

    HashMap<String, MultiFileStringDatabaseTable> tables = new HashMap<String, MultiFileStringDatabaseTable>();
    private String dataBaseDirectoryPath;
    private MultiFileStringDatabaseTable currentTable = null;

    public DatabaseTableProvider(String dataBaseDirectoryPath) {
        if (dataBaseDirectoryPath == null || dataBaseDirectoryPath.isEmpty()) {
            throw new IllegalArgumentException("directory can't be null or empty");
        }

        this.dataBaseDirectoryPath = dataBaseDirectoryPath;

        File dataBaseDirectory = new File(dataBaseDirectoryPath);

        for (final File tableFile: dataBaseDirectory.listFiles()) {
            if (tableFile.isFile()) {
                continue;
            }
            MultiFileStringDatabaseTable table;
            table = new MultiFileStringDatabaseTable(dataBaseDirectoryPath, tableFile.getName());

            tables.put(table.getName(), table);
        }
    }

    public Table getTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("tablename can't be null or empty");
        }

        checkNameValidity(name);

        MultiFileStringDatabaseTable table = tables.get(name);

        if (table == null) {
            return table;
        }

        if (currentTable != null && currentTable.getUnsavedChangesNumber() > 0) {
            throw new IllegalStateException(currentTable.getUnsavedChangesNumber() + " unsaved changes");
        }

        currentTable = table;

        return table;
    }

    public Table createTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("table name can't be null or empty");
        }

        if (tables.containsKey(name)) {
            return null;
        }

        checkNameValidity(name);

        File tableDirectory = new File(dataBaseDirectoryPath, name);
        if (!tableDirectory.exists()) {
            tableDirectory.mkdir();
        }

        MultiFileStringDatabaseTable table = new MultiFileStringDatabaseTable(dataBaseDirectoryPath, name);
        tables.put(name, table);

        return table;
    }

    public void removeTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("table name can't be null or empty");
        }

        if (!tables.containsKey(name)) {
            throw new IllegalStateException(name + " not exists");
        }

        tables.remove(name);

        currentTable = null;

        File tableFile = new File(dataBaseDirectoryPath, name);
        MultiFileMapUtils.deleteFile(tableFile);
    }

    private void checkNameValidity(String name) {
        Matcher matcher = PATTERN.matcher(name);

        if (matcher.find()) {
            throw new IllegalArgumentException("incorrect table name");
        }
    }
}
