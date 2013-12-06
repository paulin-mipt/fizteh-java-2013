package ru.fizteh.fivt.students.piakovenko.filemap.storable;



import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.piakovenko.filemap.Exit;
import ru.fizteh.fivt.students.piakovenko.filemap.Get;
import ru.fizteh.fivt.students.piakovenko.filemap.GlobalFileMapState;
import ru.fizteh.fivt.students.piakovenko.filemap.Put;
import ru.fizteh.fivt.students.piakovenko.filemap.storable.JSON.JSONSerializer;
import ru.fizteh.fivt.students.piakovenko.shell.Remove;
import ru.fizteh.fivt.students.piakovenko.shell.Shell;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class DataBase implements Table {
    private String name;
    private RandomAccessFile raDataBaseFile = null;
    private Map<String, Storeable> map = null;
    private Shell shell = null;
    private File dataBaseStorage = null;
    private List<Class<?>> storeableClasses;
    private final String nameOfFileWithTypes = "signature.tsv";
    private ThreadLocal<Transaction> transaction;
    protected final Lock lock = new ReentrantLock(true);
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    private class Transaction {
        private Map<String, Storeable> newMap;

        public Transaction() {
            this.newMap = new HashMap<String, Storeable>();
        }

        public void put(String key, Storeable value) {
            newMap.put(key, value);
        }

        public Storeable get(String key) {
            if (newMap.containsKey(key)) {
                return newMap.get(key);
            }
            return map.get(key);
        }

        public int commit() {
            int count = 0;
            for (String key : newMap.keySet()) {
                Storeable value = newMap.get(key);
                if (isChanged(value, map.get(key))) {
                    if (value == null) {
                        map.remove(key);
                    } else {
                        map.put(key, value);
                    }
                    ++count;
                }
            }
            return count;
        }


        public void clearMap() {
            newMap.clear();
        }

        public int transactionGetSize() {
            return map.size() + calcSize();
        }

        public int calcChanges() {
            int count = 0;
            for (final String key : newMap.keySet()) {
                Storeable newKey = newMap.get(key);
                if (isChanged(newKey, map.get(key))) {
                    ++count;
                }
            }
            return count;
        }

        private int calcSize() {
            int count = 0;
            for (final String key : newMap.keySet()) {
                Storeable newValue = newMap.get(key);
                Storeable oldValue = map.get(key);
                if (newValue == null && oldValue != null) {
                    --count;
                } else if (newValue != null && oldValue == null) {
                    ++count;
                }
            }
            return count;
        }

        private boolean isChanged(Storeable oldValue, Storeable newValue) {
            if (newValue == null && oldValue == null) {
                return false;
            }
            if (newValue == null || oldValue == null) {
                return true;
            }
            return !oldValue.equals(newValue);
        }

    }

    public void checkAlienStoreable(Storeable storeable) {
        for (int index = 0; index < getColumnsCount(); ++index) {
            try {
                Object o = storeable.getColumnAt(index);
                if (o == null) {
                    continue;
                }
                if (!o.getClass().equals(getColumnType(index))) {
                    throw new ColumnFormatException("Alien storeable with incompatible types");
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("Alien storeable with less columns");
            }
        }
        try {
            storeable.getColumnAt(getColumnsCount());
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        throw new ColumnFormatException("Alien storeable with more columns");
    }

    private boolean isValidNameDirectory(String name) {
        return Checker.isValidFileNumber(name);
    }

    private boolean isValidNameFile(String name) {
        return Checker.isValidFileNumber(name);
    }

    private int ruleNumberDirectory(String key) {
        int b = Math.abs(key.getBytes()[0]);
        return b % 16;
    }

    private int ruleNumberFile(String key) {
        int b = Math.abs(key.getBytes()[0]);
        return b / 16 % 16;
    }

    private void readFromFile() throws IOException  {
        long length = raDataBaseFile.length();
        while (length > 0) {
            int l1 = raDataBaseFile.readInt();
            if (l1 <= 0) {
                throw new IOException("Length of new key less or equals zero");
            } else if (l1 > 1024 * 1024) {
                throw new IOException("Key greater than 1 MB");
            }
            length -= 4;
            int l2 = raDataBaseFile.readInt();
            if (l2 <= 0) {
                throw new IOException("Length of new value less or equals zero");
            } else if (l2 > 1024 * 1024) {
                throw new IOException("Value greater than 1 MB");
            }
            length -= 4;
            byte [] key = new byte [l1];
            byte [] value = new byte [l2];
            if (raDataBaseFile.read(key) < l1) {
                throw new IOException("Key: read less, that it was pointed to read");
            } else {
                length -= l1;
            }
            if (raDataBaseFile.read(value) < l2) {
                throw new IOException("Value: read less, that it was pointed to read");
            } else {
                length -= l2;
            }
            try {
                map.put(new String(key, StandardCharsets.UTF_8), JSONSerializer.deserialize(
                        this, new String(value, StandardCharsets.UTF_8)));
            } catch (ParseException e) {
                System.err.println("readFromFile: problem with desereliaze" + e.getMessage());
                System.exit(1);
            }
        }
    }

    private void readFromFile(File storage, int numberOfDirectory) throws IOException {
        RandomAccessFile ra = null;
        try {
            ra = new RandomAccessFile(storage, "rw");
            int numberOfFile =  Integer.parseInt(storage.getName().substring(0, storage.getName().indexOf('.')), 10);
            long length = ra.length();
            while (length > 0) {
                int l1 = ra.readInt();
                if (l1 <= 0) {
                    throw new IOException("Length of new key less or equals zero");
                } else if (l1 > 1024 * 1024) {
                    throw new IOException("Key greater than 1 MB");
                }
                length -= 4;
                int l2 = ra.readInt();
                if (l2 <= 0) {
                    throw new IOException("Length of new value less or equals zero");
                } else if (l2 > 1024 * 1024) {
                    throw new IOException("Value greater than 1 MB");
                }
                length -= 4;
                byte [] key = new byte [l1];
                byte [] value = new byte [l2];
                if (ra.read(key) < l1) {
                    throw new IOException("Key: read less, that it was pointed to read");
                } else {
                    length -= l1;
                }
                if (ra.read(value) < l2) {
                    throw new IOException("Value: read less, that it was pointed to read");
                } else {
                    length -= l2;
                }
                String keyString = new String(key, StandardCharsets.UTF_8);
                String valueString = new String(value, StandardCharsets.UTF_8);
                if (ruleNumberFile(keyString) != numberOfFile || ruleNumberDirectory(keyString) != numberOfDirectory) {
                    throw new IOException("Wrong place of key value! Key: " + keyString + " Value: " + valueString);
                } else {
                    try {
                        map.put(keyString, JSONSerializer.deserialize(this, valueString));
                    } catch (ParseException e) {
                        System.err.println("readFromFile: problem with deserializer" + e.getMessage());
                        System.exit(1);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        } finally {
            if (ra != null) {
                ra.close();
            }
        }
    }

    private void saveToFile() throws IOException {
        long length  = 0;
        raDataBaseFile.seek(0);
        for (String key: map.keySet()) {
            byte [] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte [] valueBytes = JSONSerializer.serialize(this, map.get(key)).getBytes(StandardCharsets.UTF_8);
            raDataBaseFile.writeInt(keyBytes.length);
            raDataBaseFile.writeInt(valueBytes.length);
            raDataBaseFile.write(keyBytes);
            raDataBaseFile.write(valueBytes);
            length += 4 + 4 + keyBytes.length + valueBytes.length;
        }
        raDataBaseFile.setLength(length);
    }

    private void saveToFile(File f, String key, String value) throws IOException {
        RandomAccessFile ra = null;
        try {
            ra = new RandomAccessFile(f, "rw");
            ra.seek(ra.length());
            byte [] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte [] valueBytes = value.getBytes(StandardCharsets.UTF_8);
            ra.writeInt(keyBytes.length);
            ra.writeInt(valueBytes.length);
            ra.write(keyBytes);
            ra.write(valueBytes);
        } finally {
            ra.close();
        }
    }

    private void saveToDirectory() throws IOException {
        if (dataBaseStorage.exists()) {
            Remove.removeRecursively(dataBaseStorage);
        }
        if (!dataBaseStorage.mkdirs()) {
            throw new IOException("Unable to create this directory - " + dataBaseStorage.getCanonicalPath());
        }
        for (String key : map.keySet()) {
            Integer numberOfDirectory = ruleNumberDirectory(key);
            Integer numberOfFile = ruleNumberFile(key);
            File directory = new File(dataBaseStorage, numberOfDirectory.toString() + ".dir");
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Unable to create this directory - " + directory.getCanonicalPath());
                }
            }
            File writeFile = new File(directory, numberOfFile.toString() + ".dat");
            if (!writeFile.exists()) {
                writeFile.createNewFile();
            }
            saveToFile(writeFile, key, JSONSerializer.serialize(this, map.get(key)));
        }
    }

    private void loadDataBase(File dataBaseFile) throws IOException {
        raDataBaseFile = new RandomAccessFile(dataBaseFile, "rw");
        try {
            readFromFile();
        } catch (IOException e) {
            System.err.println("Error! " + e.getCause());
            System.exit(1);
        }
    }

    private void readFromDirectory(File dir, int numberOfDirectory) throws IOException {
        for (File f: dir.listFiles()) {
            if (!isValidNameFile(f.getName())) {
                throw new IOException("Wrong name of file!");
            }
            readFromFile(f, numberOfDirectory);
        }
    }

    private void loadFromDirectory(File directory) throws IOException {
        for (File f : directory.listFiles()) {
            if (!isValidNameDirectory(f.getName())) {
                throw new IOException("Wrong name of directory!");
            }
            int numberOfDirectory = Integer.parseInt(f.getName().substring(0, f.getName().indexOf('.')), 10);
            readFromDirectory(f, numberOfDirectory);
        }
    }

    public DataBase(Shell sl, File storage, TableProvider parent, List<Class<?>> columnTypes) {
        map = new HashMap<String, Storeable>();
        shell  = sl;
        dataBaseStorage = storage;
        name = storage.getName();
        storeableClasses = columnTypes;
        transaction = new ThreadLocal<Transaction>() {
           @Override
           protected Transaction initialValue() {
            return new Transaction();
           }
        };
    }

    public DataBase(Shell sl, File storage, TableProvider parent) {
        map = new HashMap<String, Storeable>();
        shell  = sl;
        dataBaseStorage = storage;
        name = storage.getName();
        transaction = new ThreadLocal<Transaction>() {
            @Override
            protected Transaction initialValue() {
                return new Transaction();
            }
        };
    }


    public void load() throws IOException {
        if (dataBaseStorage.isFile()) {
            loadDataBase(dataBaseStorage);
        } else {
            loadFromDirectory(dataBaseStorage);
        }
    }

    public String getName() {
        return name;
    }

    public void initialize(GlobalFileMapState state) {
        shell.addCommand(new Exit(state));
        shell.addCommand(new Put(state));
        shell.addCommand(new Get(state));
        shell.addCommand(new ru.fizteh.fivt.students.piakovenko.filemap.Remove(state));
        try {
            load();
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        }
    }

    public void saveDataBase() throws IOException {
        if (dataBaseStorage.isFile()) {
            try {
                saveToFile();
            } finally {
                raDataBaseFile.close();
            }
        } else {
            saveToDirectory();
        }
    }

    public Storeable get(String key) throws IllegalArgumentException {
        try {
            readWriteLock.readLock().lock();
            Checker.stringNotEmpty(key);
            return transaction.get().get(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        try {
            readWriteLock.writeLock().lock();
            Checker.stringNotEmpty(key);
            Checker.keyFormat(key);
            if (value == null) {
                throw new IllegalArgumentException("Value cannot be null");
            }
            checkAlienStoreable(value);
            Storeable oldValue = transaction.get().get(key);
            transaction.get().put(key, value);
            return oldValue;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public Storeable remove(String key) throws IllegalArgumentException {
        try {
            readWriteLock.writeLock().lock();
            Checker.stringNotEmpty(key);
            Storeable oldValue = transaction.get().get(key);
            transaction.get().put(key, null);
            transaction.get().calcChanges();
            return oldValue;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public File returnFiledirectory() {
        return dataBaseStorage;
    }

    public int size() {
        try {
            lock.lock();
            System.out.println(transaction.get().transactionGetSize());
            return transaction.get().transactionGetSize();
        } finally {
            lock.unlock();
        }
    }

    public int commit() {
        try {
            lock.lock();
            int changesCount = transaction.get().commit();
            transaction.get().clearMap();
            return changesCount;
        } finally {
            lock.unlock();
        }
    }

    public int rollback() {
        try {
            lock.lock();
            int count = transaction.get().calcChanges();
            transaction.get().clearMap();
            return count;
        } finally {
            lock.unlock();
        }
    }

    public int getColumnsCount() {
        return storeableClasses.size();
    }
    public int numberOfChanges() {
        return transaction.get().calcChanges();
    }

    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return storeableClasses.get(columnIndex);
    }

}
