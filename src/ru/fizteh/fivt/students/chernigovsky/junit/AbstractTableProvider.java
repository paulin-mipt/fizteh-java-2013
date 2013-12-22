package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.students.chernigovsky.multifilehashmap.MultiFileHashMapUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractTableProvider<TableType> {
    protected static final String TABLE_NAME_FORMAT = "[A-Za-zА-Яа-я0-9]+";
    protected HashMap<String, TableType> tableHashMap;
    private File dbDirectory;
    protected boolean autoCommit;
    protected ReadWriteLock tableProviderLock;

    public AbstractTableProvider(File newDbDirectory, boolean flag) {
        tableProviderLock = new ReentrantReadWriteLock(false);
        dbDirectory = newDbDirectory;
        autoCommit = flag;
        tableHashMap = new HashMap<String, TableType>();
    }

    public File getDbDirectory() {
        return dbDirectory;
    }

    /**
     * Возвращает таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @return Объект, представляющий таблицу. Если таблицы с указанным именем не существует, возвращает null.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     */
    public TableType getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("wrong table name");
        }

        try {
            tableProviderLock.readLock().lock();
            return tableHashMap.get(name);
        } finally {
            tableProviderLock.readLock().unlock();
        }
    }

    /**
     * Удаляет таблицу с указанным названием.
     *
     * @param name Название таблицы.
     * @throws IllegalArgumentException Если название таблицы null или имеет недопустимое значение.
     * @throws IllegalStateException Если таблицы с указанным названием не существует.
     */
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (!name.matches(TABLE_NAME_FORMAT)) {
            throw new IllegalArgumentException("wrong table name");
        }

        if (tableHashMap.get(name) == null) {
            throw new IllegalStateException("no such table");
        }

        try {
            tableProviderLock.writeLock().lock();
            tableHashMap.remove(name);

            File tableDirectory = new File(getDbDirectory(), name);

            try {
                MultiFileHashMapUtils.delete(tableDirectory);
            } catch (IOException ex) {
                throw new IllegalArgumentException("directory removal error");
            }
        } finally {
            tableProviderLock.writeLock().unlock();
        }

    }
}
