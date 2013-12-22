package ru.fizteh.fivt.students.dzvonarev.filemap;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dzvonarev.shell.Remove;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyTableProvider implements TableProvider, AutoCloseable {

    public MyTableProvider(String dir) throws RuntimeException, IOException {
        isProviderClosed = false;
        workingDirectory = dir;
        currTable = null;
        multiFileMap = new HashMap<>();
        lock = new ReentrantLock(true);
        initTypeToString();
        readData();
    }

    private String workingDirectory;
    private String currTable;
    private HashMap<String, MyTable> multiFileMap;
    private HashMap<Class<?>, String> typeToString;
    private Lock lock;
    private volatile boolean isProviderClosed;

    public void initTypeToString() {
        typeToString = new HashMap<>();
        typeToString.put(Integer.class, "int");
        typeToString.put(Long.class, "long");
        typeToString.put(Double.class, "double");
        typeToString.put(Float.class, "float");
        typeToString.put(Boolean.class, "boolean");
        typeToString.put(Byte.class, "byte");
        typeToString.put(String.class, "String");
    }

    public String getCurrentTable() {
        return currTable;
    }

    public int getSize() throws IndexOutOfBoundsException {
        return multiFileMap.get(currTable).size();
    }

    public int changeCurrentTable(String newTable) {
        lock.lock();
        try {
            File newDirectory = new File(workingDirectory, newTable);
            if (!newDirectory.exists() || newDirectory.exists() && newDirectory.isFile()) {
                return -1;
            } else {
                currTable = newTable;
                return 0;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean tableNameIsValid(String name) {
        return !(name == null || !(name.matches("\\w+")));
    }

    public boolean typesAreValid(List<Class<?>> types) {
        if (types == null || types.isEmpty()) {
            return false;
        }
        for (Class<?> type : types) {
            if (typeToString.get(type) == null) {
                return false;
            }
        }
        return true;
    }

    public void readData() throws IOException, RuntimeException {   // IOException - for system errors
        File currDir = new File(workingDirectory);
        if (currDir.exists() && currDir.isDirectory()) {
            String[] tables = currDir.list();
            if (tables != null && tables.length != 0) {
                for (String table : tables) {
                    File dirTable = new File(workingDirectory + File.separator + table);
                    if (dirTable.isFile()) {
                        continue;
                    }
                    MyTable newTable = new MyTable(dirTable, this);
                    try {
                        newTable.readFileMap();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    multiFileMap.put(table, newTable);
                }
                for (String table : tables) {  /* CLEANING */
                    if (new File(workingDirectory + File.separator + table).isFile()) {
                        continue;
                    }
                    Remove rm = new Remove();
                    ArrayList<String> myArgs = new ArrayList<>();
                    myArgs.add(workingDirectory + File.separator + table);
                    myArgs.add("notFromShell");
                    rm.execute(myArgs);
                    if (!(new File(workingDirectory, table)).mkdir()) {
                        throw new IOException("exit: can't make " + table + " directory");
                    }
                    List<Class<?>> temp = multiFileMap.get(table).getTypeArray();
                    writeTypesInFile(table, temp);
                }
            }
        } else {
            throw new RuntimeException("wrong type (invalid directory name " + workingDirectory + ")");
        }
    }

    public void writeTypesInFile(String name, List<Class<?>> types) throws IOException {
        File signature = new File(workingDirectory + File.separator + name + File.separator + "signature.tsv");
        if (!signature.createNewFile()) {
            throw new IOException("can't create file signature.tsv");
        }
        try (PrintWriter myWriter = new PrintWriter(signature)) {
            for (Class<?> type : types) {
                myWriter.write((typeToString.get(type)) + " ");
            }
        }
    }


    public void writeAll() throws IOException {
        if (multiFileMap == null || multiFileMap.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, MyTable>> fileSet = multiFileMap.entrySet();
        for (Map.Entry<String, MyTable> currItem : fileSet) {
            MyTable value = currItem.getValue();
            value.clearTable();
            value.writeInTable();
        }
    }

    @Override
    public Table getTable(String tableName) throws IllegalArgumentException {
        checkProviderClosed();
        if (!tableNameIsValid(tableName)) {
            throw new IllegalArgumentException("wrong type (invalid table name " + tableName + ")");
        }
        lock.lock();
        try {
            if (multiFileMap.get(tableName) != null) {
                return multiFileMap.get(tableName);
            } else {
                File dirTable = new File(workingDirectory + File.separator + tableName);
                if (!(dirTable.exists() && dirTable.isDirectory())) {
                    return null;
                }
                MyTable newTable;
                try {
                    newTable = new MyTable(dirTable, this);
                    newTable.readFileMap();
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
                multiFileMap.put(tableName, newTable);
                return newTable;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Table createTable(String tableName, List<Class<?>> types) throws IllegalArgumentException, IOException {
        checkProviderClosed();
        if (!tableNameIsValid(tableName) || !typesAreValid(types)) {
            throw new IllegalArgumentException("wrong type (invalid table name " + tableName + " or types)");
        }
        lock.lock();
        try {
            if (multiFileMap.containsKey(tableName)) {
                return null;
            }
            File newTable = new File(workingDirectory, tableName);
            if (!newTable.mkdir()) {
                throw new IOException("Can't create table " + tableName);
            }
            writeTypesInFile(tableName, types);
            MyTable table = new MyTable(newTable, this);
            multiFileMap.put(tableName, table);
            return table;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeTable(String tableName) throws IllegalArgumentException, IllegalStateException {
        checkProviderClosed();
        if (!tableNameIsValid(tableName)) {
            throw new IllegalArgumentException("wrong type (invalid table name " + tableName + ")");
        }
        lock.lock();
        try {
            if (!multiFileMap.containsKey(tableName)) {
                throw new IllegalStateException(tableName + " not exists");
            }
            multiFileMap.remove(tableName);
            Remove shell = new Remove();
            ArrayList<String> myArgs = new ArrayList<>();
            myArgs.add(workingDirectory + File.separator + tableName);
            myArgs.add("notFromShell");
            shell.execute(myArgs);
            if ((currTable != null) && (currTable.equals(tableName))) {
                currTable = null;
            }
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage() + " can't remove " + tableName, e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        checkProviderClosed();
        Parser myParser = new Parser();
        ArrayList<Object> values = myParser.parseValueToList(value);
        try {
            return createFor(table, values);
        } catch (ColumnFormatException | IndexOutOfBoundsException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        checkProviderClosed();
        multiFileMap.get(table.getName()).checkingValueForValid(value);
        ArrayList<Object> temp = new ArrayList<>();
        for (int i = 0; i < multiFileMap.get(table.getName()).getColumnsCount(); ++i) {
            temp.add(value.getColumnAt(i));
        }
        JSONArray jArr = new JSONArray(temp);
        return jArr.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        checkProviderClosed();
        return new MyStoreable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        checkProviderClosed();
        return new MyStoreable(table, values);
    }

    public void removeClosedTable(String tableName) {
        multiFileMap.remove(tableName);
    }

    @Override
    public String toString() {
        checkProviderClosed();
        return MyTableProvider.class.getSimpleName() + "[" + workingDirectory + "]";
    }

    @Override
    public void close() {
        if (isProviderClosed) {
            return;
        }
        lock.lock();
        try {
            Set<Map.Entry<String, MyTable>> fileSet = multiFileMap.entrySet();
            for (Map.Entry<String, MyTable> currItem : fileSet) {
                MyTable value = currItem.getValue();
                value.closeFromProvider();    // because i may iter. on bad tables
            }
            multiFileMap.clear();
            isProviderClosed = true;
        } finally {
            lock.unlock();
        }
    }

    private void checkProviderClosed() {
        if (isProviderClosed) {
            throw new IllegalStateException("provider " + this.getClass().getSimpleName() + " is closed");
        }
    }
}
