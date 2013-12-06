package ru.fizteh.fivt.students.vyatkina.database.multitable;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.vyatkina.WrappedIOException;
import ru.fizteh.fivt.students.vyatkina.database.StringTableProvider;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderChecker;
import ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils.deleteFilesThatChanged;
import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils.deleteTableFromDisk;
import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils.getTableFromDisk;
import static ru.fizteh.fivt.students.vyatkina.database.superior.TableProviderUtils.rewriteFilesThatChanged;

public class MultiTableProvider implements StringTableProvider, TableProviderConstants {

    private Map<String, MultiTable> tables = new HashMap<>();
    private Path location;

    public MultiTableProvider(Path location) {
        this.location = location;
        loadDatabase();
    }

    public void loadDatabase() {
        for (File file : location.toFile().listFiles()) {
            if (file.isDirectory()) {
                MultiTable table = new MultiTable(file.getName(), this);
                tables.put(table.getName(), table);
            }
        }
    }

    @Override
    public Table getTable(String tableName) {
        TableProviderChecker.validTableNameCheck(tableName);
        if (tables.containsKey(tableName)) {
            try {
                MultiTable table = tables.get(tableName);
                Map<String, String> diskTable = getTableFromDisk(tableDirectory(tableName).toFile());
                for (Map.Entry<String, String> entry : diskTable.entrySet()) {
                    table.putValueFromDisk(entry.getKey(), entry.getValue());
                }
                return table;
            }
            catch (IOException e) {
                throw new WrappedIOException(e.getMessage());
            }
        } else {
            return null;
        }
    }

    @Override
    public Table createTable(String tableName) {
        TableProviderChecker.validTableNameCheck(tableName);
        if (tables.containsKey(tableName)) {
            return null;
        } else {
            try {
                Files.createDirectory(tableDirectory(tableName));
            }
            catch (IOException e) {
                throw new WrappedIOException(e.getMessage());
            }
            MultiTable newTable = new MultiTable(tableName, this);
            tables.put(newTable.getName(), newTable);
            return newTable;
        }
    }

    @Override
    public void removeTable(String tableName) {
        TableProviderChecker.validTableNameCheck(tableName);
        if (tables.containsKey(tableName)) {
            try {
                deleteTableFromDisk(tableDirectory(tableName).toFile());
            }
            catch (IOException e) {
                throw new WrappedIOException(e.getMessage());
            }
            tables.remove(tableName);
        } else {
            throw new IllegalStateException(TABLE_NOT_EXIST);
        }
    }

    void commitTable(MultiTable table) {
        Path tableDirectory = tableDirectory(table.getName());
        Set<String> keysThatValuesHaveChanged = table.getKeysThatValuesHaveChanged();
        try {
            Set<Path> filesThatChanged = deleteFilesThatChanged(tableDirectory, keysThatValuesHaveChanged);
            rewriteFilesThatChanged(tableDirectory, table.entriesThatChanged(), filesThatChanged);
        }
        catch (IOException e) {
            throw new WrappedIOException(e.getMessage());
        }
    }

    @Override
    public void saveChangesOnExit() {
        for (MultiTable table : tables.values()) {
            commitTable(table);
        }
    }

    private Path tableDirectory(String name) {
        return location.resolve(name);
    }
}
