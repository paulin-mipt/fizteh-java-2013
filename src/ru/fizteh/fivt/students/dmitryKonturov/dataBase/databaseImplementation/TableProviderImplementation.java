package ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.DatabaseException;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils.CheckDatabasesWorkspace;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils.JsonUtils;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.utils.MultiFileMapLoaderWriter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableProviderImplementation implements TableProvider {

    private final Path workspace;
    private boolean isLoading = false;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private Lock writeLock = readWriteLock.writeLock();
    private Lock readLock =  readWriteLock.readLock();

    private HashMap<String, Table> existingTables = new HashMap<>();
    static final Class[] ALLOWED_TYPES = new Class[]{
            Integer.class,
            Long.class,
            Byte.class,
            Float.class,
            Double.class,
            String.class,
            Boolean.class
    };

    private boolean isAllowedNameForTable(String tableName) {
        if (tableName == null || tableName.trim().length() == 0) {
            return false;
        }
        boolean containDisallowedCharacter = tableName.contains("\\") || tableName.contains("/")
                || tableName.contains(":") || tableName.contains("*")
                || tableName.contains("?") || tableName.contains("\"")
                || tableName.contains("<") || tableName.contains(">")
                || tableName.contains("|");
        return !containDisallowedCharacter;
    }

    TableProviderImplementation(Path path) throws IOException, DatabaseException {
        workspace = path;
        CheckDatabasesWorkspace.checkWorkspace(workspace);
        isLoading = true;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                String tableName = entry.toFile().getName();
                TableImplementation currentTable = new TableImplementation(tableName, this);
                existingTables.put(tableName, currentTable);
            }
        } catch (IOException e) {
            throw new IOException("Fail to load existing base", e);
        } catch (Exception e) {
            throw new DatabaseException("Fail to load existing base", e);
        }
        isLoading = false;
    }

    Path getWorkspace() {
        return workspace;
    }

    boolean isProviderLoading() {
        return isLoading;
    }

    @Override
    public Table getTable(String name) {
        if (!isAllowedNameForTable(name)) {
            throw new IllegalArgumentException("name is null or contains disallowed characters: " + name);
        }
        readLock.lock();
        try {
            return existingTables.get(name);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (!isAllowedNameForTable(name)) {
            throw new IllegalArgumentException("Name is null or contains disallowed characters: " + name);
        }
        if (columnTypes == null) {
            throw new IllegalArgumentException("columnTypes is null");
        }
        if (columnTypes.size() == 0) {
            throw new IllegalArgumentException("columnTypes is empty");
        }

        for (Class<?> currentType : columnTypes) {
            if (currentType == null) {
                throw new ColumnFormatException("null type");
            }
            boolean isAllowed = false;
            for (Class<?> type : ALLOWED_TYPES) {
                if (currentType.equals(type)) {
                    isAllowed = true;
                }
            }
            if (!isAllowed) {
                throw new IllegalArgumentException("Not all column types is allowed");
            }
        }

        Table oldTable;
        writeLock.lock();
        try {
            oldTable = existingTables.get(name);
            if (oldTable != null) {
                return null;
            }

            Table newTable;
            try {
                newTable = new TableImplementation(name, this, columnTypes);
            } catch (Exception e) {
                throw new IOException("Fail to create database", e);
            }
            existingTables.put(name, newTable);

            return newTable;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        if (!isAllowedNameForTable(name)) {
            throw new IllegalArgumentException("Name is null or contains disallowed characters: " + name);
        }
        writeLock.lock();
        try {
            if (existingTables.get(name) != null) {
                try {
                    MultiFileMapLoaderWriter.recursiveRemove(workspace.resolve(name));
                    existingTables.remove(name);
                } catch (Exception e) {
                    throw new IOException("Cannot remove table", e);
                }
            } else {
                throw new IllegalStateException("Not exists");
            }
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return JsonUtils.deserialize(this, table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return JsonUtils.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Null table");
        }
        return new StoreableImplementation(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table == null || values == null) {
            throw new IllegalArgumentException("Null arguments");
        }
        int valueSize = values.size();
        if (valueSize != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("Requested column count and table column count not match");
        }
        Storeable toReturn = createFor(table);
        for (int i = 0; i < values.size(); ++i) {
            toReturn.setColumnAt(i, values.get(i));
        }
        return toReturn;
    }
}
