package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class FileDatabase implements AutoCloseable {
    private static final int OFFSET_BYTES = 4;

    private Path dbFilePath;
    private Map<String, String> database = new ConcurrentHashMap<String, String>();
    
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    
    private volatile boolean isLoaded = false;
    private volatile boolean isChanged = false;
    
    public FileDatabase(Path dbFilePath) throws IOException {
        this.dbFilePath = dbFilePath;
    }

    public String get(String key) throws IOException {
        loadDatabaseIfNotLoaded();
        return database.get(key);
    }
    
    public String put(String key, String value) throws IOException {
        loadDatabaseIfNotLoaded();
        isChanged = true;
        String oldValue = database.put(key, value);
        return oldValue;
    }
    
    public String remove(String key) throws IOException {   
        loadDatabaseIfNotLoaded();
        isChanged = true;
        String value = database.remove(key);
        return value;
    }
    
    public int size() throws IOException {
        loadDatabaseIfNotLoaded();
        return database.size();
    }
    
    public void save() throws IOException {
        if (isChanged) {
            writeLock.lock();
            try {
                if (isChanged) {
                    try {
                        Files.createDirectories(dbFilePath.getParent());
                        
                    } catch (IOException e) {
                        throw new IOException("Error while saving database file: "
                                + "couldn't create a directory "
                                + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
                    }
                    
                    try (RandomAccessFile dbFile = new RandomAccessFile(dbFilePath.toFile(), "rw")) {
                        writeDataToFile(dbFile);
                        
                    } catch (IOException e) {
                        throw new IOException("Error while saving database file or while saving changes: "
                                + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
                    }
                    
                    try {
                        deleteIfEmpty();
                        
                    } catch (IOException e) {
                        throw new IOException("Error while saving database file: "
                                + "couldn't delete a file or a directory "
                                + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
                    }
                    
                    isChanged = false;
                }
            } finally {
                writeLock.unlock();
            }
        }
        
    }
    
    private void loadDatabaseIfNotLoaded() throws IOException {
        if (!isLoaded) {
            writeLock.lock();
            try {
                if (!isLoaded) {
                    loadDatabase();
                    isLoaded = true;
                }
            } finally {
                writeLock.unlock();
            }
        }
    }
    
    private void loadDatabase() throws IOException {
        try {
            Files.createDirectories(dbFilePath.getParent());
            
        } catch (IOException e) {
            throw new IOException("Error while opening database file: couldn't create a directory: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        try (RandomAccessFile dbFile = new RandomAccessFile(dbFilePath.toFile(), "rw")) {
            
            getDataFromFile(dbFile);
        } catch (IOException e) {
            throw new IOException("Error while opening database file or while loading data: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
        
        try {
            deleteIfEmpty();
        } catch (IOException e) {
            throw new IOException("Error while opening database file: couldn't delete a file or a directory "
                    + ((e.getMessage() != null) ? e.getMessage() : "unknown error"), e);
        }
    }
    
    @Override
    public void close() throws IOException {
        save();
    }

    private void getDataFromFile(RandomAccessFile dbFile) throws IOException {
        String key;
        String value;
        ArrayList<Byte> tempKey = new ArrayList<Byte>();
        int tempOffset1;
        int tempOffset2;
        long currentPosition;
        byte tempByte;
        byte[] tempArray;

        dbFile.seek(0);
        while (dbFile.getFilePointer() != dbFile.length()) {
            tempByte = dbFile.readByte();
            if (tempByte != '\0') {
                tempKey.add(tempByte);
            } else {
                tempOffset1 = dbFile.readInt();
                currentPosition = dbFile.getFilePointer();
                while (dbFile.readByte() != '\0'
                        && dbFile.getFilePointer() != dbFile.length());
                if (dbFile.getFilePointer() == dbFile.length()) {
                    tempOffset2 = (int) dbFile.length();
                } else {
                    tempOffset2 = dbFile.readInt();
                }
                dbFile.seek(tempOffset1);
                tempArray = new byte[tempOffset2 - tempOffset1];
                dbFile.readFully(tempArray);
                value = new String(tempArray, StandardCharsets.UTF_8);
                tempArray = new byte[tempKey.size()];
                for (int i = 0; i < tempKey.size(); ++i) {
                    tempArray[i] = tempKey.get(i).byteValue();
                }
                key = new String(tempArray, StandardCharsets.UTF_8);
                database.put(key, value);
                tempKey.clear();
                dbFile.seek(currentPosition);
            }
        }
    }

    private void writeDataToFile(RandomAccessFile dbFile) throws IOException {
        int currentOffset = 0;
        long returnPosition;
        String value;

        dbFile.setLength(0);

        for (String key : database.keySet()) {
            currentOffset += key.getBytes(StandardCharsets.UTF_8).length + OFFSET_BYTES + 1;
        }

        for (String key : database.keySet()) {
            dbFile.write(key.getBytes(StandardCharsets.UTF_8));
            dbFile.writeByte(0);
            dbFile.writeInt(currentOffset);
            value = database.get(key);
            returnPosition = dbFile.getFilePointer();
            dbFile.seek(currentOffset);
            dbFile.write(value.getBytes(StandardCharsets.UTF_8));

            currentOffset += value.getBytes(StandardCharsets.UTF_8).length;
            dbFile.seek(returnPosition);
        }
        
    }
    
    private void deleteIfEmpty() throws IOException { 
        if (Files.size(dbFilePath) == 0) {
            Files.delete(dbFilePath);
            if (dbFilePath.getParent().toFile().list().length == 0) {
                Files.delete(dbFilePath.getParent());
            }
        }
    }
    
    public void selfCheck(int nDirectory, int nFile) throws IllegalArgumentException, IOException {
        loadDatabaseIfNotLoaded();
        
        readLock.lock();
        try {
            for (String key : database.keySet()) {
                if (TableImplementation.DirectoryAndFileNumberCalculator.getnDirectory(key) != nDirectory
                        || TableImplementation.DirectoryAndFileNumberCalculator.getnFile(key) != nFile) {
                    throw new IllegalArgumentException(
                            String.format("Wrong key placement: %s in directory %d, file %d", key, nDirectory, nFile));
                }
            }
        } finally {
            readLock.unlock();
        }
    }
}
