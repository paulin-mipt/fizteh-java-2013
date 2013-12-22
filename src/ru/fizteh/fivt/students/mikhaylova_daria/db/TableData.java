package ru.fizteh.fivt.students.mikhaylova_daria.db;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.fizteh.fivt.storage.structured.*;

public class TableData implements Table, AutoCloseable {

    File tableFile;
    boolean isClosed = false;
    DirDataBase[] dirArray = new DirDataBase[16];
    private ArrayList<Class<?>> columnTypes;
    TableManager manager;
    private  ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock myWriteLock = readWriteLock.writeLock();
    private final Lock myReadLock = readWriteLock.readLock();

    private static ArrayList<Class<?>> normList(List<Class<?>> arg) {
        HashMap<String, Class<?>> types = new HashMap<>();
        types.put("Integer", Integer.class);
        types.put("Long", Long.class);
        types.put("Double", Double.class);
        types.put("Float", Float.class);
        types.put("Boolean", Boolean.class);
        types.put("Byte", Byte.class);
        types.put("byte", Byte.class);
        types.put("String", String.class);
        types.put("int", Integer.class);
        types.put("long", Long.class);
        types.put("double", Double.class);
        types.put("float", Float.class);
        types.put("boolean", Boolean.class);
        ArrayList<Class<?>> answer = new ArrayList<>();
        for (int i = 0; i < arg.size(); ++i) {
            if (arg.get(i) == null) {
                throw new IllegalArgumentException("Wrong type in " + i + " column: null type");
            }
            if (!types.containsKey(arg.get(i).getSimpleName())) {
                throw new IllegalArgumentException("Wrong type in " + i + " column: " + arg.get(i).getCanonicalName());
            }
            answer.add(types.get(arg.get(i).getSimpleName()));
        }
        return answer;
    }


    private static Class<?> normType(String arg) {
        HashMap<String, Class<?>> types = new HashMap<>();
        types.put("Integer", Integer.class);
        types.put("Long", Long.class);
        types.put("Double", Double.class);
        types.put("Float", Float.class);
        types.put("Boolean", Boolean.class);
        types.put("Byte", Byte.class);
        types.put("byte", Byte.class);
        types.put("String", String.class);
        types.put("int", Integer.class);
        types.put("long", Long.class);
        types.put("double", Double.class);
        types.put("float", Float.class);
        types.put("boolean", Boolean.class);
        return types.get(arg);
    }

    TableData(File tableFile, List<Class<?>> columnTypes, TableManager manager) throws IOException {
        if (columnTypes == null) {
            throw new IllegalArgumentException("list of column's types is null");
        }
        if (columnTypes.isEmpty()) {
            throw new IllegalArgumentException("list of column's types is empty");
        }
        if (!tableFile.exists()) {
            if (!tableFile.mkdir()) {
                throw new IllegalArgumentException("Creating of " + tableFile.toString() + "error");
            }
        }
        columnTypes = normList(columnTypes);
        this.manager = manager;
        HashMap<String, String> types = new HashMap<>();
        types.put("Integer", "int");
        types.put("Long", "long");
        types.put("Double", "double");
        types.put("Float", "float");
        types.put("Boolean", "boolean");
        types.put("Byte", "byte");
        types.put("String", "String");
        StringBuilder str = new StringBuilder();
        this.columnTypes = new ArrayList<>(columnTypes);
        this.tableFile = tableFile;
        for (int i = 0; i < columnTypes.size(); ++i) {
            str = str.append(types.get(columnTypes.get(i).getSimpleName()));
            str = str.append(" ");
        }
        File sign = new File(tableFile, "signature.tsv");
        try {
            if (!sign.createNewFile()) {
                throw new IllegalArgumentException("Creating \"signature.tsv\" error");
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage(), e);
        }
        try (BufferedWriter signatureWriter =
                     new BufferedWriter(new FileWriter(sign))) {
            signatureWriter.write(str.toString().trim());
        } catch (IOException e) {
            throw new IOException("Writing error: signature.tsv", e);
        }
        if (tableFile != null) {
            for (short i = 0; i < 16; ++i) {
                File dir = new File(tableFile.toPath().resolve(i + ".dir").toString());
                dirArray[i] = new DirDataBase(dir, i, this);
            }
        }
    }



    TableData(File tableFile, TableManager manager) throws IOException {
        this.manager = manager;
        this.columnTypes = new ArrayList<>();
        this.tableFile = tableFile;
        String signature;
        File sign = new File(tableFile, "signature.tsv");
        if (!sign.exists()) {
            throw new IllegalArgumentException(sign.getName() + " does not exist");
        }
        try (BufferedReader signatureReader =
                     new BufferedReader(new FileReader(sign))) {
            signature = signatureReader.readLine();
        } catch (IOException e) {
            throw new IOException("Reading error: signature.tsv", e);
        }
        if (signature == null) {
            throw new IllegalArgumentException("\"signature.tsv\" is bad");
        }
        String[] signatures = signature.trim().split(" ");
        if (signatures.length == 0) {
            throw new IllegalArgumentException(sign.toString() + " Empty type list");
        }

        for (int i = 0; i < signatures.length; ++i) {
            if (signatures[i].equals("int")) {
                columnTypes.add(Integer.class);
            } else if (signatures[i].equals("long")) {
                columnTypes.add(Long.class);
            }  else if (signatures[i].equals("byte")) {
                columnTypes.add(Byte.class);
            } else if (signatures[i].equals("float")) {
                columnTypes.add(Float.class);
            } else if (signatures[i].equals("double")) {
                columnTypes.add(Double.class);
            } else if (signatures[i].equals("boolean")) {
                columnTypes.add(Boolean.class);
            } else if (signatures[i].equals("String")) {
                columnTypes.add(String.class);
            } else {
                throw new IllegalArgumentException("This type is not supposed: "
                        + signatures[i]);
            }
        }

        for (short i = 0; i < 16; ++i) {
            File dir = new File(tableFile.toPath().resolve(i + ".dir").toString());
            dirArray[i] = new DirDataBase(dir, i, this);
        }

    }

    public String getName() {
        myReadLock.lock();
        try {
            if (isClosed) {

                throw new  IllegalStateException("Table is closed");
            }
            return tableFile.getName();
        } finally {
            myReadLock.unlock();
        }
    }

    void checkKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (key.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("Bad char in key");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key is empty");
        }
    }

    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        myReadLock.lock();
        try {
            if (isClosed) {
                throw new  IllegalStateException("Table is closed");
            }
            int i = 0;
            try {
                for (; i < getColumnsCount(); ) {
                    if (normType(getColumnType(i).getSimpleName()) == null) {
                        throw new IllegalArgumentException("wrong type (The table contains "
                                + "unsupported type:" + getColumnType(i));
                    }
                    if (normType(getColumnType(i).getSimpleName()).equals(Integer.class)) {
                        value.getIntAt(i);
                    } else if (normType(getColumnType(i).getSimpleName()).equals(Long.class)) {
                        value.getLongAt(i);
                    } else if (normType(getColumnType(i).getSimpleName()).equals(Byte.class)) {
                        value.getByteAt(i);
                    } else if (normType(getColumnType(i).getSimpleName()).equals(Float.class)) {
                        value.getFloatAt(i);
                    } else if (normType(getColumnType(i).getSimpleName()).equals(Double.class)) {
                        value.getDoubleAt(i);
                    } else if (normType(getColumnType(i).getSimpleName()).equals(Boolean.class)) {
                        value.getBooleanAt(i);
                    } else if (normType(getColumnType(i).getSimpleName()).equals(String.class)) {
                        value.getStringAt(i);
                    }
                    ++i;
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("wrong type (" + e.getMessage() + " index i = " + i + ")", e);
            } catch (ClassCastException  e) {
                throw new ColumnFormatException("wrong type (" + e.getMessage() + " index i = " + i + "::"
                        + value.getColumnAt(i) + ")", e);

            }

            byte b = key.getBytes()[0];
            if (b < 0) {
                b *= (-1);
            }
            int nDirectory = b % 16;
            int nFile = (b / 16) % 16;
            return dirArray[nDirectory].fileArray[nFile].put(key, value, this);
        } finally {
            myReadLock.unlock();
        }
    }

    public Storeable remove(String key) throws IllegalArgumentException {
        checkKey(key);
        byte b = key.getBytes()[0];
        if (b < 0) {
            b *= (-1);
        }
        int nDirectory = b % 16;
        int nFile = b / 16 % 16;
        Storeable removedValue;
        myReadLock.lock();
        try {
            if (isClosed) {
                throw new  IllegalStateException("Table is closed");
            }
            try {
                removedValue = dirArray[nDirectory].fileArray[nFile].remove(key, this);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            return removedValue;
        } finally {
            myReadLock.unlock();
        }
    }

    public Storeable get(String key) throws IllegalArgumentException {
        checkKey(key);
        byte b = key.getBytes()[0];
        if (b < 0) {
            b *= (-1);
        }
        int nDirectory = b % 16;
        int nFile = (b / 16) % 16;
        Storeable getValue;
        myReadLock.lock();
        try {
            if (isClosed) {
                throw new  IllegalStateException("Table is closed");
            }
            try {
                getValue = dirArray[nDirectory].fileArray[nFile].get(key, this);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            return getValue;
        } finally {
            myReadLock.unlock();
        }
    }

    int countChanges() {
        myReadLock.lock();
        try {
            if (isClosed) {
                throw new  IllegalStateException("Table is closed");
            }
            int numberOfChanges = 0;
            for (int i = 0; i < 16; ++i) {
                numberOfChanges += dirArray[i].countChanges();
            }
            return numberOfChanges;
        } finally {
            myReadLock.unlock();
        }
    }

    public int size() {
        int numberOfKeys = 0;
        myWriteLock.lock();
        try {
            if (isClosed) {
                throw new IllegalStateException("Table is closed");
            }
            for (int i = 0; i < 16; ++i) {
                numberOfKeys += dirArray[i].size();
            }
        } finally {
            myWriteLock.unlock();
        }
        return numberOfKeys;
    }

    public int commit() {
        int numberOfChanges = 0;
        myWriteLock.lock();
        try {
            if (isClosed) {
                throw new IllegalStateException("Table is closed");
            }
            for (int i = 0; i < 16; ++i) {
                numberOfChanges += dirArray[i].commit();
            }
        } finally {
            myWriteLock.unlock();
        }
        return numberOfChanges;
    }

    public int rollback() {
        myReadLock.lock();
        try {
            if (isClosed) {
                throw new  IllegalStateException("Table is closed");
            }
            int numberOfChanges = 0;
            for (int i = 0; i < 16; ++i) {
                numberOfChanges += dirArray[i].rollback();
            }
            return numberOfChanges;
        } finally {
            myReadLock.unlock();
        }
    }

    public int getColumnsCount() {
        myReadLock.lock();
        try {
            if (isClosed) {
                throw new  IllegalStateException("Table is closed");
            }
            return columnTypes.size();
        } finally {
            myReadLock.unlock();
        }
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        myReadLock.lock();
        try {
            if (isClosed) {
                throw new  IllegalStateException("Table is closed");
            }
            if (columnIndex >= columnTypes.size()) {
                throw new IndexOutOfBoundsException("Index " + columnIndex
                        + "does not exist. Number of columns is" + columnTypes.size());
            }
            return columnTypes.get(columnIndex);
        } finally {
            myReadLock.unlock();
        }
    }

    public void close() {
        myWriteLock.lock();
        try {
            if (!isClosed) {
                rollback();
                manager.bidDataBase.remove(this.getName());
                isClosed = true;
            }
        } finally {
            myWriteLock.unlock();
        }
    }

    public String toString() {
        return this.getClass().getSimpleName() + "[" + tableFile.toPath().toAbsolutePath() + "]";
    }

}
