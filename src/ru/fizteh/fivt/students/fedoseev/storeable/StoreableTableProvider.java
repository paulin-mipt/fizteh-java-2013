package ru.fizteh.fivt.students.fedoseev.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StoreableTableProvider implements TableProvider {
    private Map<String, StoreableTable> databaseTables = new HashMap<>();
    private ReadWriteLock locker = new ReentrantReadWriteLock();

    @Override
    public StoreableTable getTable(String tableName) {
        checkTableName(tableName);

        locker.readLock().lock();

        try {
            return databaseTables.get(tableName);
        } finally {
            locker.readLock().unlock();
        }
    }

    @Override
    public StoreableTable createTable(String tableName, List<Class<?>> columnTypes) throws IOException {
        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("CREATE TABLE ERROR: invalid column types");
        }
        for (Class<?> type : columnTypes) {
            if (type == null || ColumnTypes.typeToName(type) == null) {
                throw new IllegalArgumentException("CREATE TABLE ERROR: invalid column types");
            }
        }
        checkTableName(tableName);

        locker.writeLock().lock();

        try {
            if (databaseTables.get(tableName) != null) {
                return null;
            } else {
                StoreableTable newTable = new StoreableTable(tableName, columnTypes, this);

                databaseTables.put(tableName, newTable);

                return newTable;
            }
        } finally {
            locker.writeLock().unlock();
        }
    }

    @Override
    public void removeTable(String tableName) throws IOException {
        checkTableName(tableName);

        if (!databaseTables.containsKey(tableName)) {
            throw new IllegalStateException("REMOVE TABLE ERROR: not existing table");
        }

        locker.writeLock().lock();

        try {
            databaseTables.remove(tableName);
        } finally {
            locker.writeLock().unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("wrong type (DESERIALIZE ERROR: invalid value)");
        }

        XMLDeserializer deserializer = new XMLDeserializer(value);
        Storeable storable;
        List<Object> values = new ArrayList<>(table.getColumnsCount());

        for (int i = 0; i < table.getColumnsCount(); i++) {
            try {
                Class<?> type = table.getColumnType(i);
                Object nextValue = deserializer.getNext(type);

                if (nextValue != null) {
                    if (nextValue.toString().trim().isEmpty()) {
                        throw new ParseException("DESERIALIZE ERROR: invalid value", 0);
                    }
                }

                values.add(nextValue);
            } catch (ColumnFormatException e) {
                throw new ParseException("DESERIALIZE ERROR: invalid type", i);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("DESERIALIZE ERROR: invalid format", i);
            }
        }

        try {
            deserializer.close();
            storable = createFor(table, values);
        } catch (ColumnFormatException e) {
            throw new ParseException("DESERIALIZE ERROR: invalid type", 0);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("DESERIALIZE ERROR: invalid format", 0);
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), 0);
        }

        return storable;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            throw new IllegalArgumentException("wrong type (SERIALIZE ERROR: invalid value)");
        }

        try {
            XMLSerializer serializer = new XMLSerializer();

            for (int i = 0; i < table.getColumnsCount(); i++) {
                serializer.write(value.getColumnAt(i));
            }

            serializer.close();

            return serializer.getStringWriterContents();
        } catch (ParseException e) {
            throw new IllegalArgumentException("wrong type (" + e.getMessage() + ")");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        if (table == null) {
            return null;
        }

        return new StoreableStorable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values == null) {
            throw new IllegalArgumentException("CREATE FOR ERROR: invalid values");
        }

        if (table == null) {
            return null;
        }

        if (values.size() < table.getColumnsCount()) {
            throw new ColumnFormatException("CREATE FOR ERROR: invalid values");
        }

        for (int i = 0; i < table.getColumnsCount(); i++) {
            if (values.get(i) != null && !table.getColumnType(i).equals(values.get(i).getClass())) {
                throw new ColumnFormatException("CREATE FOR ERROR: invalid values");
            }
        }

        StoreableStorable storable = new StoreableStorable(table);

        storable.setColumns(values);

        return storable;
    }

    private void checkTableName(String tableName) throws IllegalArgumentException {
        if (tableName == null) {
            throw new IllegalArgumentException("GET | CREATE | REMOVE TABLE ERROR: invalid table name");
        }

        if (!tableName.matches("[^><\"?|*.]*")) {
            throw new RuntimeException("GET | CREATE | REMOVE ERROR: illegal symbol in table name");
        }

        String nameFormat = "[а-яА-яa-zA-Z0-9]+";

        if (!new File(tableName).toPath().getFileName().toString().matches(nameFormat)) {
            throw new IllegalArgumentException("GET | CREATE | REMOVE TABLE ERROR: invalid table name");
        }
    }
}
