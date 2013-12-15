package ru.fizteh.fivt.students.baldindima.junit;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.JSONArray;
import org.json.JSONException;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class DataBaseTable implements TableProvider {

    private String tableDirectory;
    private Map<String, DataBase> tables;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public DataBaseTable(String nTableDirectory) {
        tableDirectory = nTableDirectory;
        tables = new HashMap();
    }


    private void checkName(String name) {
        if ((name == null) || name.trim().length() == 0) {
            throw new IllegalArgumentException("Cannot create table");
        }

        if (name.matches("[" + '"' + "'\\/:/*/?/</>/|/.\\\\]+")

                || name.contains(File.separator)
                || name.contains(".")) {
            throw new RuntimeException("Wrong symbols");
        }
    }

    public Table createTable(String name, List<Class<?>> types) throws IOException {
        if (types == null || types.size() == 0) {
            throw new IllegalArgumentException("wrong list of types");
        }
        checkName(name);
        String path = tableDirectory + File.separator + name;

        File file = new File(path);
        writeLock.lock();
        try {
            if (file.exists()) {
                return null;
            }

            if (!file.mkdir()) {
                throw new RuntimeException("Cannot create table " + name);
            }


            DataBase table = new DataBase(path, this, types);

            tables.put(name, table);
            return table;
        } finally {
            writeLock.unlock();
        }


    }

    public Table getTable(String name) {
        checkName(name);
        String path = tableDirectory + File.separator + name;

        File file = new File(path);
        if ((!file.exists()) || (file.isFile())) {
            return null;
        }
        
        
        
        writeLock.lock();
        try {
        	if (tables.containsKey(name)) {
                return tables.get(name);
            }
            DataBase table = new DataBase(path, this);
            tables.put(name, table);
            return table;
        } catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        } finally {
            writeLock.unlock();
        }


    }

    public void removeTable(String name) throws IOException {
        checkName(name);
        String path = tableDirectory + File.separator + name;

        File file = new File(path);

        if (!file.exists()) {
            throw new IllegalStateException("Table not exist");
        }
        writeLock.lock();
        try {
            if (tables.containsKey(name)) {
                tables.get(name).drop();
                tables.remove(name);

            } else {
                DataBase base = new DataBase(name, this);
                base.drop();

            }


            if (!file.delete()) {
                throw new RuntimeException("Cannot delete a table" + name);
            }
        } finally {
            writeLock.unlock();
        }

    }


    public Storeable deserialize(Table table, String value) throws ParseException {
        Storeable storeable;
        try {
            JSONArray jsonValue = new JSONArray(value);
            List<Object> values = new ArrayList<>();
            for (int i = 0; i < jsonValue.length(); ++i) {
                values.add(jsonValue.get(i));
            }

            storeable = createFor(table, values);
        } catch (JSONException e) {
            throw new ParseException(e.getMessage(), 0);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid number of arguments", 0);
        } catch (ColumnFormatException e) {
            throw new ParseException(e.getMessage(), 0);
        }

        return storeable;
    }


    public String serialize(Table table, Storeable value)
            throws ColumnFormatException {
        return JSONClass.serialize(table, value);
    }


    public Storeable createFor(Table table) {
        return new BaseStoreable(table);
    }


    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        BaseStoreable storeable = new BaseStoreable(table);
        storeable.setValues(values);
        return storeable;
    }


}
