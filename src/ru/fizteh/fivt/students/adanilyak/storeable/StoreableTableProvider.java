package ru.fizteh.fivt.students.adanilyak.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.adanilyak.serializer.JSONserializer;
import ru.fizteh.fivt.students.adanilyak.tools.CheckOnCorrect;
import ru.fizteh.fivt.students.adanilyak.tools.DeleteDirectory;
import ru.fizteh.fivt.students.adanilyak.tools.WorkStatus;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Alexander
 * Date: 03.11.13
 * Time: 16:50
 */
public class StoreableTableProvider implements TableProvider, AutoCloseable {
    private Map<String, Table> allTablesMap = new HashMap<>();
    private File allTablesDirectory;
    private final Lock lock = new ReentrantLock(true);
    private WorkStatus status;

    public StoreableTableProvider(File atDirectory) throws IOException {
        if (atDirectory == null) {
            throw new IllegalArgumentException("Directory is not set");
        }
        if (!atDirectory.exists()) {
            if (!atDirectory.mkdirs()) {
                throw new IOException("storeable table factory create: table provider unavailable");
            }
        } else if (!atDirectory.isDirectory()) {
            throw new IllegalArgumentException(atDirectory.getName() + ": not a directory");
        }
        allTablesDirectory = atDirectory;
        status = WorkStatus.WORKING;
        for (File tableFile : allTablesDirectory.listFiles()) {
            Table table = new StoreableTable(tableFile, this);
            allTablesMap.put(tableFile.getName(), table);
        }
    }

    @Override
    public Table getTable(String tableName) {
        status.isOkForOperations();
        if (!CheckOnCorrect.goodName(tableName)) {
            throw new IllegalArgumentException("get table: name is bad");
        }
        lock.lock();
        try {
            Table getRes = allTablesMap.get(tableName);
            if (getRes == null) {
                return null;
            }
            if (((StoreableTable) getRes).isOkForOperations()) {
                return getRes;
            } else {
                File tableFile = new File(allTablesDirectory, tableName);
                Table newTable = null;
                try {
                    newTable = new StoreableTable(tableFile, this);
                } catch (IOException exc) {
                    System.err.println(exc.getMessage());
                }
                allTablesMap.put(tableName, newTable);
                return newTable;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Table createTable(String tableName, List<Class<?>> columnTypes) throws IOException {
        status.isOkForOperations();
        if (!CheckOnCorrect.goodName(tableName) || !CheckOnCorrect.goodColumnTypes(columnTypes)) {
            throw new IllegalArgumentException("create table: name or column types is bad");
        }
        File tableFile = new File(allTablesDirectory, tableName);
        lock.lock();
        try {
            if (!tableFile.mkdir()) {
                if (((StoreableTable) allTablesMap.get(tableName)).isOkForOperations()) {
                    return null;
                } else {
                    return getTable(tableName);
                }
            }
            Table newTable = new StoreableTable(tableFile, columnTypes, this);
            allTablesMap.put(tableName, newTable);
            return newTable;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeTable(String tableName) {
        status.isOkForOperations();
        if (!CheckOnCorrect.goodName(tableName)) {
            throw new IllegalArgumentException("remove table: name is bad");
        }
        lock.lock();
        try {
            if (allTablesMap.get(tableName) == null) {
                throw new IllegalStateException(tableName + " not exists");
            }
            File tableFile = new File(allTablesDirectory, tableName);
            try {
                DeleteDirectory.rm(tableFile);
            } catch (IOException exc) {
                System.err.println(exc.getMessage());
            }
            allTablesMap.remove(tableName);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        status.isOkForOperations();
        if (value == null) {
            throw new ParseException("storeable table provider: deserialize: value can not be null", 0);
        }
        return JSONserializer.deserialize(table, value, this);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        status.isOkForOperations();
        List<Class<?>> columnTypes = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            columnTypes.add(table.getColumnType(i));
        }
        if (!CheckOnCorrect.goodStoreable(value, columnTypes)) {
            throw new ColumnFormatException("storeable table provider: serialize: bad value");
        }
        return JSONserializer.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        status.isOkForOperations();
        return new StoreableRow(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        status.isOkForOperations();
        return new StoreableRow(table, values);
    }

    @Override
    public String toString() {
        status.isOkForOperations();
        return getClass().getSimpleName() + "[" + allTablesDirectory + "]";
    }

    @Override
    public void close() {
        status.isOkForClose();
        for (String tableName : allTablesMap.keySet()) {
            ((StoreableTable) allTablesMap.get(tableName)).close();
        }
        status = WorkStatus.CLOSED;
    }

    public boolean isOkForOperations() {
        try {
            status.isOkForOperations();
        } catch (IllegalStateException exc) {
            return false;
        }
        return true;
    }
}
