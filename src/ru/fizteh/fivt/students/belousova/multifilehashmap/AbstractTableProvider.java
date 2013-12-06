package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractTableProvider<TableType> {
    protected final String TABLE_NAME_FORMAT = "[A-Za-zА-Яа-я0-9]+";
    protected final ReadWriteLock tableProviderTransactionLock = new ReentrantReadWriteLock(true);
    protected Map<String, TableType> tableMap = new HashMap<>();
    protected File dataDitectory;

    public TableType getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("incorrect name");
        }

        tableProviderTransactionLock.readLock().lock();
        try {
            return tableMap.get(name);
        } finally {
            tableProviderTransactionLock.readLock().unlock();
        }
    }

    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        if (!tableMap.containsKey(name)) {
            throw new IllegalStateException("table doesn't exists");
        }

        tableProviderTransactionLock.writeLock().lock();
        try {

            File tableDirectory = new File(dataDitectory, name);
            try {
                FileUtils.deleteDirectory(tableDirectory);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            tableMap.remove(name);
        } finally {
            tableProviderTransactionLock.writeLock().unlock();
        }
    }
}
