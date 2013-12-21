package ru.fizteh.fivt.students.kislenko.storeable;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTableProvider implements TableProvider, AutoCloseable {
    private HashMap<String, MyTable> tables = new HashMap<String, MyTable>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private String nameOfCurrentCreatingTable;
    private Path path;
    boolean closed = false;

    public MyTableProvider(String providerPath) {
        path = Paths.get(providerPath);
    }

    public Path getPath() {
        return path;
    }

    @Override
    public MyTable getTable(String name) {
        assertClosed();
        if (name == null || !Paths.get(name).getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
            //System.out.println(Thread.currentThread().getName() + " exit get with incorrect table name");
            throw new IllegalArgumentException("Incorrect table name.");
        }
        lock.writeLock().lock();
        try {
            assertClosed();
            MyTable table = tables.get(name);
            if (table != null && table.isClosed()) {
                MyTable returnTable = new MyTable(name, table.getTypes(), this);
                returnTable.setStorage(table.getStorage());
                tables.put(name, returnTable);
                return returnTable;
            }
            //System.out.println(Thread.currentThread().getName() + " exit get with success");
            return table;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public MyTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        assertClosed();
        if (name == null) {
            //System.out.println(Thread.currentThread().getName() + " exit with null name");
            throw new IllegalArgumentException("Incorrect table name.");
        }
        if (columnTypes == null) {
            //System.out.println(Thread.currentThread().getName() + " exit with incorrect columns");
            throw new IllegalArgumentException("Incorrect column type list.");
        }
        if (columnTypes.size() == 0) {
            //System.out.println(Thread.currentThread().getName() + " exit with empty columns list");
            throw new IllegalArgumentException("Empty signature.");
        }
        for (Class<?> columnType : columnTypes) {
            if (columnType == null) {
                //    System.out.println(Thread.currentThread().getName() + " exit with bad column list");
                throw new IllegalArgumentException("Incorrect column types in creating table.");
            }
            if (columnType != Integer.class && columnType != Long.class && columnType != Byte.class
                    && columnType != Float.class && columnType != Double.class && columnType != Boolean.class
                    && columnType != String.class) {
                //    System.out.println(Thread.currentThread().getName() + " exit with bad column list");
                throw new IllegalArgumentException("Incorrect column types in creating table.");
            }
        }
        if (name.contains(".")) {
            //System.out.println(Thread.currentThread().getName() + " exit with dots in the name");
            throw new RuntimeException("There is a fucking dot!");
        }
        if (!Paths.get(name).getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
            //System.out.println(Thread.currentThread().getName() + " exit with collision incorrect name");
            throw new IllegalArgumentException("Incorrect table name.");
        }
        while (!lock.writeLock().tryLock()) {
            if (name.equals(nameOfCurrentCreatingTable)) {
                //    System.out.println(Thread.currentThread().getName() + " exit with collision in name");
                return null;
            }
        }
        nameOfCurrentCreatingTable = name;
        if (tables.containsKey(name)) {
            lock.writeLock().unlock();
            //System.out.println(Thread.currentThread().getName()
            // + " exit with name that doesn't contains in name list");
            return null;
        }
        MyTable table = new MyTable(name, columnTypes, this);
        tables.put(name, table);
        nameOfCurrentCreatingTable = null;
        lock.writeLock().unlock();
        assertClosed();
        //System.out.println(Thread.currentThread().getName() + " exit create with success");
        return table;
    }

    @Override
    public void removeTable(String name) throws IOException {
        assertClosed();
        if (name == null || !Paths.get(name).getFileName().toString().matches("[0-9a-zA-Zа-яА-Я]+")) {
            //    System.out.println(Thread.currentThread().getName() + " exit remove with incorrect table name");
            throw new IllegalArgumentException("Incorrect table name.");
        }
        lock.writeLock().lock();
        assertClosed();
        if (!tables.containsKey(name)) {
            lock.writeLock().unlock();
            //    System.out.println(Thread.currentThread().getName()
            // + " exit remove, because haven't table with that name");
            throw new IllegalStateException("Have no table to remove.");
        }
        tables.remove(name);
        lock.writeLock().unlock();
        //System.out.println(Thread.currentThread().getName() + " exit remove with success");
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        assertClosed();
        JSONArray array;
        try {
            array = new JSONArray(value);
        } catch (JSONException e) {
            throw new ParseException("Very strange string in input.", -1);
        }
        if (array.length() != table.getColumnsCount()) {
            throw new ParseException("Incorrect count of columns in input.",
                    Math.min(array.length(), table.getColumnsCount()));
        }
        List<Object> values = new ArrayList<Object>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (array.get(i).equals(null)) {
                values.add(null);
            } else if (array.get(i).getClass() == table.getColumnType(i)) {
                values.add(array.get(i));
            } else if ((array.get(i).getClass() == Long.class || array.get(i).getClass() == Integer.class)
                    && table.getColumnType(i) == Long.class) {
                values.add(array.getLong(i));
            } else if (array.get(i).getClass() == Integer.class && table.getColumnType(i) == Byte.class) {
                Integer a = array.getInt(i);
                values.add(a.byteValue());
            } else if (array.get(i).getClass() == Double.class && table.getColumnType(i) == Float.class) {
                Double a = array.getDouble(i);
                values.add(a.floatValue());
            } else {
                throw new ParseException("Incorrect value string.", -1);
            }
        }
        return createFor(table, values);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        assertClosed();
        Object[] values = new Object[table.getColumnsCount()];
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            values[i] = value.getColumnAt(i);
        }
        JSONArray array = new JSONArray(values);
        return array.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        assertClosed();
        ArrayList<Class<?>> types = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
        }
        Value v = new Value(types);
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            v.setColumnAt(i, null);
        }
        return v;
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        assertClosed();
        if (values.size() < table.getColumnsCount()) {
            throw new ColumnFormatException("Invalid list of values.");
        }
        ArrayList<Class<?>> types = new ArrayList<Class<?>>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            types.add(table.getColumnType(i));
            if (values.get(i) != null && !table.getColumnType(i).equals(values.get(i).getClass())) {
                throw new ColumnFormatException("Invalid list of values.");
            }
        }
        Value v = new Value(types);
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            v.setColumnAt(i, values.get(i));
        }
        assertClosed();
        return v;
    }

    @Override
    public void close() throws Exception {
        if (closed) {
            return;
        }
        for (String tableName : tables.keySet()) {
            if (!tables.get(tableName).isClosed()) {
                tables.get(tableName).close();
            }
        }
        closed = true;
    }

    private void assertClosed() {
        if (closed) {
            throw new IllegalStateException("Provider was closed.");
        }
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + path.toString() + "]";
    }
}
