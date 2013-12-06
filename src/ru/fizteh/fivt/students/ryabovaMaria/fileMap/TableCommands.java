package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class TableCommands implements Table {
    private final ArrayList<Class<?>> types;
    private final TableProvider tableProvider;
    private final File tableDir;
    private HashMap<String, String>[][] lastList;
    private ReadWriteLock lock;
    private Lock readLock;
    private Lock writeLock;
    
    private class HashCalc {
        int hashCode;
        int numberOfDir;
        int numberOfFile;
        void get(String key) {
            if (key == null) {
                throw new IllegalArgumentException("Bad key");
            }
            hashCode = Math.abs(key.hashCode());
            numberOfDir = hashCode % 16;
            numberOfFile = hashCode / 16 % 16;
        }
    };
    
    private ThreadLocal<HashMap<String, String>[][]> diff = new ThreadLocal<HashMap<String, String>[][]>() {
        @Override
        protected HashMap<String, String> [][] initialValue() {
            HashMap<String, String> [][] diffObject = new HashMap[16][16];
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    diffObject[i][j] = new HashMap<String, String>();
                }
            }
            return diffObject;
        }
    };
    
    private ThreadLocal<HashMap<Integer, String>> update = new ThreadLocal<HashMap<Integer, String>>() {
        @Override
        protected HashMap<Integer, String> initialValue() {
            return new HashMap<Integer, String>();
        }
    };
    
    TableCommands(File directory, List<Class<?>> types, TableProvider tableProvider) throws IOException {
        lock = new ReentrantReadWriteLock(true);
        readLock = lock.readLock();
        writeLock = lock.writeLock();
        this.tableProvider = tableProvider;
        this.types = new ArrayList(types);
        lastList = new HashMap[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                lastList[i][j] = new HashMap<String, String>();
            }
        }
        tableDir = directory;
        isCorrectTable();
    }
    
    private void isCorrectTable() throws IOException {
        String[] listOfDirs = tableDir.list();
        int countOk = 0;
        for (int i = 0; i < listOfDirs.length; ++i) {
            boolean ok = false;
            int value = -1;
            for (int j = 0; j < 16; ++j) {
                String validName = String.valueOf(j) + ".dir";
                if (listOfDirs[i].equals(validName)) {
                    ok = true;
                    value = j;
                    break;
                }
            }
            if (ok) {
                ++countOk;
                isCorrectDir(value, listOfDirs[i]);
            } else {
                if (!(listOfDirs[i].equals("signature.tsv") && new File(tableDir, listOfDirs[i]).isFile())) {
                    throw new IllegalArgumentException("Incorrect table");
                }
            }
        }
        if (countOk == listOfDirs.length) {
            throw new IllegalArgumentException("no signature.tsv");
        }
    }
    
    private void isCorrectDir(int numOfDir, String name) throws IOException {
        File curDir = tableDir.toPath().resolve(name).normalize().toFile();
        String[] listOfFiles = curDir.list();
        if (listOfFiles.length == 0) {
            throw new IllegalArgumentException("empty dir"); 
        }
        for (int i = 0; i < listOfFiles.length; ++i) {
            boolean ok = false;
            int value = -1;
            for (int j = 0; j < 16; ++j) {
                String validName = String.valueOf(j) + ".dat";
                if (listOfFiles[i].equals(validName)) {
                    ok = true;
                    value = j;
                    break;
                }
            }
            if (ok) {
                isCorrectFile(numOfDir, value, curDir, listOfFiles[i]);
            } else {
                throw new IllegalArgumentException("Incorrect table");
            }
        }
    }
    
    private void isCorrectFile(int numOfDir, int numOfFile, File current, String name) throws IOException {
        File dbFile = current.toPath().resolve(name).normalize().toFile();
        if (!dbFile.exists()) {
            throw new IllegalArgumentException("Incorrect table");
        }
        if (!dbFile.isFile()) {
            throw new IllegalArgumentException("Incorrect table");
        }
        try (RandomAccessFile db = new RandomAccessFile(dbFile, "rw")) {
            long curPointer = 0;
            long lastPointer = 0;
            long length = db.length();
            if (length == 0) {
                throw new IllegalArgumentException("empty file");
            }
            db.seek(0);
            String lastKey = "";
            int lastOffset = 0;
            while (curPointer < length) {
                byte curByte = db.readByte();
                if (curByte == '\0') {
                    byte[] byteKey = new byte[(int) curPointer - (int) lastPointer];
                    curPointer = db.getFilePointer();
                    db.seek(lastPointer);
                    db.readFully(byteKey);
                    db.seek(curPointer);
                    String currentKey = new String(byteKey, "UTF-8");
                    int currentHashCode = Math.abs(currentKey.hashCode());
                    int currentNumOfDir = currentHashCode % 16;
                    int currentNumOfFile = currentHashCode / 16 % 16;
                    if (currentNumOfDir != numOfDir || currentNumOfFile != numOfFile) {
                        throw new IllegalArgumentException("Incorrect file " + dbFile.toString());
                    }
                    int offset = db.readInt();
                    if (!lastKey.isEmpty()) {
                        byte[] byteValue = new byte[offset - lastOffset];
                        curPointer = db.getFilePointer();
                        db.seek(lastOffset);
                        db.readFully(byteValue);
                        String lastValue = new String(byteValue, "UTF-8");
                        db.seek(curPointer);
                        if (lastList[numOfDir][numOfFile].containsKey(lastKey)) {
                            System.err.println(lastKey + " meets twice in db.dat");
                            System.exit(1);
                        }
                        lastList[numOfDir][numOfFile].put(lastKey, lastValue);
                    }
                    lastOffset = offset;
                    lastKey = currentKey;
                    lastPointer = db.getFilePointer();
                }
                curPointer = db.getFilePointer();
            }
            if (lastOffset == 0 || lastKey.isEmpty()) {
                throw new IllegalArgumentException("Incorrect file " + dbFile.toString());
            }
            byte[] byteValue = new byte[(int) length - lastOffset];
            db.seek(lastOffset);
            db.readFully(byteValue);
            String lastValue = new String(byteValue, "UTF-8");
            if (lastList[numOfDir][numOfFile].containsKey(lastKey)) {
                throw new IllegalArgumentException("Incorrect file" + dbFile.toString());
            }
            lastList[numOfDir][numOfFile].put(lastKey, lastValue);
        } catch (Exception e) {
            throw new IOException("Incorrect table", e);
        }
    }
    
    @Override
    public String getName() {
        return tableDir.getName();
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key");
        }
        HashCalc file = new HashCalc();
        file.get(key);
        String value;
        readLock.lock();
        try {
            value = lastList[file.numberOfDir][file.numberOfFile].get(key);
        } finally {
            readLock.unlock();
        }
        if (diff.get()[file.numberOfDir][file.numberOfFile].containsKey(key)) {
            value = diff.get()[file.numberOfDir][file.numberOfFile].get(key);
        }
        try {
            return tableProvider.deserialize(this, value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Storeable put(String key, Storeable value) {
        if (value == null || key == null || key.isEmpty() || key.matches(".*\\s.*")) {
            throw new IllegalArgumentException("Bad args");
        }
        try {
            HashCalc file = new HashCalc();
            file.get(key);
            update.get().put(file.numberOfDir * 16 + file.numberOfFile, " ");
            String stringValue = tableProvider.serialize(this, value);
            Storeable answer = get(key);
            diff.get()[file.numberOfDir][file.numberOfFile].put(key, stringValue);
            return answer;
        } catch (Exception e) {
            throw new ColumnFormatException("incorrect args", e);
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad args");
        }
        HashCalc file = new HashCalc();
        file.get(key);
        update.get().put(file.numberOfDir * 16 + file.numberOfFile, null);
        Storeable answer = get(key);
        diff.get()[file.numberOfDir][file.numberOfFile].put(key, null);
        return answer;
    }
    
    private int getCountSize(int first, int second) {
        readLock.lock();
        try {
            int result = lastList[first][second].size();
            for (Map.Entry entry : diff.get()[first][second].entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (value == null) {
                    if (lastList[first][second].containsKey(key)) {
                        --result;
                    }
                } else {
                    if (!lastList[first][second].containsKey(key)) {
                        ++result;
                    }
                }
            }
            return result;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        int countSize = 0;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                countSize += getCountSize(i, j);
            }
        }
        return countSize; 
    }
    
    private void writeIntoFile(int numOfDir, int numOfFile) throws IOException {
        String dirString = String.valueOf(numOfDir) + ".dir";
        String fileString = String.valueOf(numOfFile) + ".dat";
        File dbDir = tableDir.toPath().resolve(dirString).normalize().toFile();
        if (!dbDir.isDirectory()) {
            if (!dbDir.mkdir()) {
                throw new IOException("incorrect file");
            }
        }
        File dbFile = dbDir.toPath().resolve(fileString).normalize().toFile();
        if (lastList[numOfDir][numOfFile].isEmpty()) {
            dbFile.delete();
            if (dbDir.list().length == 0) {
                if (!dbDir.delete()) {
                    throw new IOException("incorrect file");
                }
            }
            return;
        }
        try (RandomAccessFile db = new RandomAccessFile(dbFile, "rw")) {   
            db.setLength(0);
            Iterator<Map.Entry<String, String>> it;
            it = lastList[numOfDir][numOfFile].entrySet().iterator();
            long[] pointers = new long[lastList[numOfDir][numOfFile].size()];
            int counter = 0;
            while (it.hasNext()) {
                Map.Entry<String, String> m = (Map.Entry<String, String>) it.next();
                String key = m.getKey();
                db.write(key.getBytes("UTF-8"));
                db.write("\0".getBytes("UTF-8"));
                pointers[counter] = db.getFilePointer();
                db.seek(pointers[counter] + 4);
                ++counter;
            }
            it = lastList[numOfDir][numOfFile].entrySet().iterator();
            counter = 0;
            while (it.hasNext()) {
                Map.Entry<String, String> m = (Map.Entry<String, String>) it.next();
                String value = m.getValue();
                int curPointer = (int) db.getFilePointer();
                db.seek(pointers[counter]);
                db.writeInt(curPointer);
                db.seek(curPointer);
                db.write(value.getBytes("UTF-8"));
                ++counter;
            }
        } catch (Exception e) {
            throw new IOException("incorrect file", e);
        }
    }
    
    public int countChanges(boolean isWrite) throws IOException {
        int result = 0;
        for (Integer file : update.get().keySet()) {
            int numberOfFile = file % 16;
            int numberOfDir = (file - numberOfFile) / 16;
            for (Map.Entry entry : diff.get()[numberOfDir][numberOfFile].entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (value == null) {
                    if (lastList[numberOfDir][numberOfFile].containsKey(key)) {
                        ++result;
                        if (isWrite) {
                            lastList[numberOfDir][numberOfFile].remove(key);
                        }
                    }
                } else {
                    if (!value.equals(lastList[numberOfDir][numberOfFile].get(key))) {
                        ++result;
                        if (isWrite) {
                            lastList[numberOfDir][numberOfFile].put(key, value);
                        }
                    }
                }
            }
            if (isWrite) {
                writeIntoFile(numberOfDir, numberOfFile);
            }
        }
        return result;
    }
    
    @Override
    public int commit() throws IOException {
        int result = 0;
        writeLock.lock();
        try {
            result = countChanges(true);
        } finally {
            writeLock.unlock();
        }
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (!diff.get()[i][j].isEmpty()) {
                    diff.get()[i][j].clear();
                }
            }
        }
        return result;
    }

    @Override
    public int rollback() {
        int result = 0;
        readLock.lock();
        try {
            result = countChanges(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            readLock.unlock();
        }
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (!diff.get()[i][j].isEmpty()) {
                    diff.get()[i][j].clear();
                }
            }
        }
        return result;
    }

    @Override
    public int getColumnsCount() {
        return types.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= types.size()) {
            throw new IndexOutOfBoundsException();
        }
        return types.get(columnIndex);
    }
}
