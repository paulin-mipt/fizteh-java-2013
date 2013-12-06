package ru.fizteh.fivt.students.mikhaylova_daria.db;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.DataFormatException;
import ru.fizteh.fivt.storage.structured.*;

public class FileMap {
    private ConcurrentHashMap<String, Storeable> fileMapInitial = new ConcurrentHashMap<>();
    private ThreadLocal<HashMap<String, Storeable>> fileMapNewValue = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        protected HashMap<String, Storeable> initialValue() {
            return null;
        }
    };
    private ThreadLocal<HashSet<String>> fileMapRemoveKey = new ThreadLocal<HashSet<String>>() {
        @Override
        protected HashSet<String> initialValue() {
            return null;
        }
    };


    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock myWriteLock = readWriteLock.writeLock();
    private final Lock myReadLock = readWriteLock.readLock();

    private File file;
    private Short[] id;
    Boolean isLoaded = false;

    FileMap() {

    }

    FileMap(File file, Short[] id) {
        this.file = file;
        this.id = id;
    }
    private void initialMap() {
        if (fileMapNewValue.get() == null) {
            fileMapNewValue.set(new HashMap<String, Storeable>());
        }
        if (fileMapRemoveKey.get() == null) {
            fileMapRemoveKey.set(new HashSet<String>());
        }
    }


    public Storeable put(String key, Storeable value, TableData table) {
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        table.checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        initialMap();
        try {
            table.manager.serialize(table, value);
        } catch (Exception e) {
            throw new ColumnFormatException("Wrong typelist of value", e);
        }
        myWriteLock.lock();
        try {
            if (!isLoaded) {
                try {
                    readFile(table);
                } catch (DataFormatException e) {
                    throw new IllegalArgumentException("Bad data", e);
                } catch (Exception e) {
                    throw new RuntimeException("Reading error", e);
                }
            }
        } finally {
            myWriteLock.unlock();
        }
        myReadLock.lock();
        try {
            if (fileMapRemoveKey.get().contains(key)) {
                fileMapRemoveKey.get().remove(key);
                fileMapNewValue.get().put(key, value);
                return null;
            }
            if ((!fileMapInitial.containsKey(key))) {
                return fileMapNewValue.get().put(key, value);
            } else if (fileMapNewValue.get().containsKey(key)) {
                return fileMapNewValue.get().put(key, value);
            } else {
                fileMapNewValue.get().put(key, value);
                return fileMapInitial.get(key);
            }
        } finally {
            myReadLock.unlock();
        }
    }

    public Storeable get(String key, TableData table) throws IllegalArgumentException {
        initialMap();
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        table.checkKey(key);
        myWriteLock.lock();
        try {
            if (!isLoaded) {
                try {
                    readFile(table);
                } catch (DataFormatException e) {
                    throw new IllegalArgumentException("Bad data", e);
                } catch (Exception e) {
                    throw new RuntimeException("Reading error", e);
                }
            }
        } finally {
            myWriteLock.unlock();
        }
        myReadLock.lock();
        try {
            if (fileMapNewValue.get().containsKey(key)) {
                return fileMapNewValue.get().get(key);
            } else {
                if (fileMapRemoveKey.get().contains(key)) {
                    return null;
                } else {
                    return fileMapInitial.get(key);
                }
            }
        } finally {
            myReadLock.unlock();
        }
    }

    public Storeable remove(String key, TableData table) throws IllegalArgumentException {
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        table.checkKey(key);
        initialMap();
        myWriteLock.lock();
        try {
            if (!isLoaded) {
                try {
                    readFile(table);
                } catch (DataFormatException e) {
                    throw new IllegalArgumentException("Bad data", e);
                } catch (Exception e) {
                    throw new RuntimeException("Reading error", e);
                }
            }
        } finally {
            myWriteLock.unlock();
        }
        myReadLock.lock();
        try {
            if (fileMapNewValue.get().containsKey(key)) {
                if (!fileMapInitial.containsKey(key)) {
                    return fileMapNewValue.get().remove(key);
                } else {
                    fileMapRemoveKey.get().add(key);
                    return fileMapNewValue.get().remove(key);
                }

            } else {
                if (fileMapRemoveKey.get().contains(key)) {
                    return null;
                } else {
                    if (fileMapInitial.containsKey(key)) {
                        fileMapRemoveKey.get().add(key);
                        return fileMapInitial.get(key);
                    } else {
                        return null;
                    }
                }
            }
        } finally {
            myReadLock.unlock();
        }
    }

    private void writeFile(TableData table) throws Exception {
        initialMap();
        RandomAccessFile fileDataBase = null;
        Exception e = null;
        myWriteLock.lock();
        try {
            try {
                fileDataBase = new RandomAccessFile(file, "rw");
                fileDataBase.setLength(0);
                HashMap<String, Long> offsets = new HashMap<String, Long>();
                long currentOffsetOfValue;
                long offset = fileDataBase.getFilePointer();
                for (String key: fileMapInitial.keySet()) {
                    fileDataBase.write(key.getBytes("UTF8"));
                    fileDataBase.write("\0".getBytes());
                    offset = fileDataBase.getFilePointer();
                    offsets.put(key, offset);
                    fileDataBase.seek(fileDataBase.getFilePointer() + 4);
                    currentOffsetOfValue = fileDataBase.getFilePointer();
                }

                long currentPosition = 0;
                for (String key: fileMapInitial.keySet()) {
                    String value = table.manager.serialize(table, fileMapInitial.get(key));
                    fileDataBase.write(value.getBytes("UTF8"));
                    currentPosition  = fileDataBase.getFilePointer();
                    currentOffsetOfValue = currentPosition - value.getBytes("UTF8").length;
                    fileDataBase.seek(offsets.get(key));
                    Integer lastOffsetInt = new Long(currentOffsetOfValue).intValue();
                    fileDataBase.writeInt(lastOffsetInt);
                    fileDataBase.seek(currentPosition);
                }
            } catch (Exception exp) {
                e = exp;
                throw e;
            } finally {
                try {
                    if (fileDataBase != null) {
                        fileDataBase.close();
                    }
                } catch (Throwable th) {
                    if (e != null) {
                        e.addSuppressed(th);
                    }
                }
            }
            if (file.length() == 0) {
                deleteEmptyFile();
            }
            fileMapRemoveKey.get().clear();
            fileMapNewValue.get().clear();
        } finally {
            myWriteLock.unlock();
        }
    }

    private boolean deleteEmptyFile() {
        initialMap();
        isLoaded = false;
        fileMapRemoveKey.get().clear();
        fileMapNewValue.get().clear();
        return file.delete();
    }

    void setAside() {
        initialMap();
        myWriteLock.lock();
        try {
            if (isLoaded) {
                fileMapRemoveKey.get().clear();
                fileMapNewValue.get().clear();
                myWriteLock.lock();
                try {
                    fileMapInitial.clear();
                } finally {
                    myWriteLock.unlock();
                }
                isLoaded = false;
            }
        } finally {
            myWriteLock.unlock();
        }
    }

    void readFile(TableData table) throws IOException, DataFormatException, ParseException {
        isLoaded = true;
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        Exception e = null;
        Storeable storeableValue;
        RandomAccessFile dataBase = null;
        myWriteLock.lock();
        try {
            fileMapInitial.clear();
            try {
                dataBase = new RandomAccessFile(file, "r");
                HashMap<Integer, String> offsetAndKeyMap = new HashMap<Integer, String>();
                HashMap<String, Integer> keyAndValueLength = new HashMap<String, Integer>();
                String key = readKey(dataBase);
                byte b = key.getBytes()[0];
                if (b < 0) {
                    b *= (-1);
                }
                if (id[0] != b % 16 && id[1] != b / 16 % 16) {
                    throw new DataFormatException("Illegal key in file1 " + file.toPath().toString());
                }
                if (keyAndValueLength.containsKey(key)) {
                    throw new DataFormatException("Illegal key in file2 " + file.toPath().toString());
                }
                Integer offset = 0;
                try {
                    offset = dataBase.readInt();
                } catch (EOFException e1) {
                    throw new DataFormatException(file.getName());
                }
                offsetAndKeyMap.put(offset, key);
                final int firstOffset = offset;
                try {
                    int lastOffset = offset;
                    String lastKey;
                    while (dataBase.getFilePointer() < firstOffset) {
                        lastKey = key;
                        key = readKey(dataBase);
                        lastOffset = offset;
                        offset = dataBase.readInt();
                        offsetAndKeyMap.put(offset, key);
                        keyAndValueLength.put(lastKey, offset - lastOffset);
                        if (keyAndValueLength.containsKey(key)) {
                            throw new DataFormatException(file.getName() + ": " + key
                                    + ": The key is already contained");
                        }
                    }
                    keyAndValueLength.put(key, (int) dataBase.length() - offset);
                } catch (EOFException e1) {
                    throw new DataFormatException(file.getName());
                }
                int lengthOfValue = 0;
                while (dataBase.getFilePointer() < dataBase.length()) {
                    int currentOffset = (int) dataBase.getFilePointer();
                    if (!offsetAndKeyMap.containsKey(currentOffset)) {
                        throw new DataFormatException("Illegal key in file " + file.toPath().toString());
                    } else {
                        key = offsetAndKeyMap.get(currentOffset);
                        lengthOfValue = keyAndValueLength.get(key);
                    }
                    byte[] valueInBytes = new byte[lengthOfValue];
                    for (int i = 0; i < lengthOfValue; ++i) {
                        valueInBytes[i] = dataBase.readByte();
                    }
                    String value = new String(valueInBytes, "UTF8");
                    storeableValue = table.manager.deserialize(table, value);
                    fileMapInitial.put(key, storeableValue);
                }
            } catch (FileNotFoundException e1) {
                e = e1;
                return;
            } catch (EOFException e2) {
                e = e2;
                throw new DataFormatException(file.toString());
            } finally {
                if (dataBase != null) {
                    try {
                        dataBase.close();
                    } catch (Throwable th) {
                        if (e != null) {
                            e.addSuppressed(th);
                        }
                    }
                }
            }
        } finally {
            myWriteLock.unlock();
        }
    }


    private String readKey(RandomAccessFile dateBase) throws IOException, DataFormatException {
        Vector<Byte> keyBuilder = new Vector<Byte>();
        try {
            byte buf = dateBase.readByte();
            while (buf != "\0".getBytes("UTF8")[0]) {
                keyBuilder.add(buf);
                buf = dateBase.readByte();
            }
        } catch (EOFException e) {
            throw new DataFormatException(file.getName());
        }
        String key = null;
        try {
            byte[] keyInBytes = new byte[keyBuilder.size()];
            for (int i = 0; i < keyBuilder.size(); ++i) {
                keyInBytes[i] = keyBuilder.elementAt(i);
            }
            key = new String(keyInBytes, "UTF8");
        } catch (Exception e) {
            throw  new IOException(file.getName(), e);
        }
        return key;
    }

    int numberOfChangesCounter(TableData table) {
        initialMap();
        int numberOfChanges = 0;
        Set<String> newKeys = fileMapNewValue.get().keySet();
        Set<String> oldKeys = fileMapInitial.keySet();
        myReadLock.lock();
        try {
            for (String key: newKeys) {
                if (oldKeys.contains(key)) {
                    String val1 = table.manager.serialize(table, fileMapNewValue.get().get(key));
                    String val2 = table.manager.serialize(table, fileMapInitial.get(key));
                    if (!val1.equals(val2)) {
                        ++numberOfChanges;
                    }
                } else {
                    ++numberOfChanges;
                }
            }
            Set<String> removeKeys = fileMapRemoveKey.get();
            for (String key: removeKeys) {
                if (oldKeys.contains(key)) {
                    ++numberOfChanges;
                }
            }
        } finally {
            myReadLock.unlock();
        }
        return numberOfChanges;
    }


    void commit(TableData table) {
        initialMap();
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        myWriteLock.lock();
        try {
            int numberOfChanges = numberOfChangesCounter(table);
            if (numberOfChanges != 0) {
                Set<String> newKeys = fileMapNewValue.get().keySet();
                Set<String> oldKeys = fileMapInitial.keySet();
                for (String key: newKeys) {
                    if (oldKeys.contains(key)) {
                        String val1 = table.manager.serialize(table, fileMapNewValue.get().get(key));
                        String val2 = table.manager.serialize(table, fileMapInitial.get(key));
                        if (!val1.equals(val2)) {
                            fileMapInitial.put(key, fileMapNewValue.get().get(key));
                        }
                    } else {
                        fileMapInitial.put(key, fileMapNewValue.get().get(key));
                    }
                }
                Set<String> removeKeys = fileMapRemoveKey.get();
                for (String key: removeKeys) {
                    if (oldKeys.contains(key)) {
                        fileMapInitial.remove(key);
                    }
                }
                try {
                    writeFile(table);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Writing error", e);
                }
            }
        } finally {
            myWriteLock.unlock();
        }
    }

    int rollback(TableData table) {
        initialMap();
        int numberOfChanges = numberOfChangesCounter(table);
        fileMapRemoveKey.get().clear();
        fileMapNewValue.get().clear();
        return numberOfChanges;
    }

    int size(TableData table) {
        if (table == null) {
            throw new IllegalArgumentException("Table is null");
        }
        initialMap();
        myWriteLock.lock();
        try {
            if (!isLoaded) {
                try {
                    readFile(table);
                } catch (DataFormatException e) {
                    throw new IllegalArgumentException("Bad dates", e);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Reading error", e);
                }
            }
            Set<String> newKeys = fileMapNewValue.get().keySet();
            Set<String> oldKeys = fileMapInitial.keySet();
            int numberOfNew = 0;
            for (String key: newKeys) {
                if (!oldKeys.contains(key)) {
                    ++numberOfNew;
                }
            }
            return fileMapInitial.size() - fileMapRemoveKey.get().size() + numberOfNew;
        } finally {
            myWriteLock.unlock();
        }
    }
}


