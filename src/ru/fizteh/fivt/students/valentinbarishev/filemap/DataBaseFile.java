package ru.fizteh.fivt.students.valentinbarishev.filemap;


import ru.fizteh.fivt.storage.structured.TableProvider;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.locks.Lock;

public class DataBaseFile implements AutoCloseable {

    protected final String fileName;
    protected File file;
    private File dir;
    private int fileNumber;
    private int direcotryNumber;
    private DataBase table;
    private TableProvider provider;

    private ClassState state = new ClassState(this);

    private ThreadLocal<HashMap<String, String>> diff = new ThreadLocal<HashMap<String, String>>() {
        @Override
        public HashMap<String, String> initialValue() {
            return new HashMap<String, String>();
        }
    };

    private ThreadLocal<HashSet<String>> deleted = new ThreadLocal<HashSet<String>>() {
        @Override
        public HashSet<String> initialValue() {
            return new HashSet<String>();
        }
    };

    private Lock readLock;
    private Lock writeLock;

    private Map<String, String> old;

    public DataBaseFile(final String newFileName, final int newDirectoryNumber, final int newFileNumber,
                        DataBase newTable, TableProvider newProvider) throws IOException {
        fileName = newFileName;

        table = newTable;
        provider = newProvider;

        readLock = table.readLock;
        writeLock = table.writeLock;

        fileNumber = newFileNumber;
        direcotryNumber = newDirectoryNumber;

        file = new File(fileName);
        dir = new File(file.getParent());

        old = new HashMap<>();

        load();
        check();
    }

    public boolean check() throws IOException {
        for (Map.Entry<String, String> node : old.entrySet()) {
            int zeroByte = Math.abs(node.getKey().getBytes(StandardCharsets.UTF_8)[0]);
            if (!((zeroByte % 16 == direcotryNumber) && ((zeroByte / 16) % 16 == fileNumber))) {
                throw new IOException("Wrong file format key[0] =  " + String.valueOf(zeroByte)
                        + " in file " + fileName);
            }
            try {
                provider.deserialize(table, node.getValue());
            } catch (ParseException e) {
                throw new IOException("Invalid file format! (parse exception error!)");
            }
        }
        return true;
    }

    private void load() {
        try {
            if (dir.exists() && dir.list().length == 0) {
                throw new IOException("Empty dir!");
            }
            if (!dir.exists() || !file.exists()) {
                return;
            }
            try (RandomAccessFile inputFile = new RandomAccessFile(fileName, "rw")) {
                while (inputFile.getFilePointer() < inputFile.length() - 1) {

                    int keyLength = inputFile.readInt();
                    int valueLength = inputFile.readInt();

                    if ((keyLength <= 0) || (valueLength <= 0)) {
                        throw new DataBaseWrongFileFormat("Wrong file format! " + file.getName());
                    }

                    byte[] key;
                    byte[] value;

                    try {
                        key = new byte[keyLength];
                        value = new byte[valueLength];
                    } catch (OutOfMemoryError e) {
                        throw new DataBaseWrongFileFormat("Some key or value are too large in " + file.getName());
                    }

                    inputFile.read(key);
                    inputFile.read(value);

                    old.put(new String(key, StandardCharsets.UTF_8), new String(value, StandardCharsets.UTF_8));
                }
            }
            if (old.size() == 0) {
                throw new IOException("Empty file!");
            }
        } catch (FileNotFoundException e) {
            throw new DataBaseException("File not found!");
        } catch (IOException e) {
            throw new DataBaseException("File load error!");
        }
    }

    public void createPath() {
        if (dir.exists()) {
            return;
        }

        if (!dir.mkdir()) {
            throw new DataBaseException("Cannot create directory!");
        }
    }

    public void deletePath() {
        if (!dir.exists()) {
            return;
        }

        if (dir.list().length != 0) {
            return;
        }

        if (!dir.delete()) {
            throw new DataBaseException("Cannot delete a directory!");
        }
    }

    public void save() {
        try {
            if (getSize() == 0) {
                if ((file.exists()) && (!file.delete())) {
                    throw new DataBaseException("Cannot delete a file!");
                }
                deletePath();
            } else {
                createPath();
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        throw new DataBaseException("Cannot create a file " + fileName);
                    }
                }
                try (RandomAccessFile outputFile = new RandomAccessFile(fileName, "rw")) {
                    for (Map.Entry<String, String> node : old.entrySet()) {
                        outputFile.writeInt(node.getKey().getBytes(StandardCharsets.UTF_8).length);
                        outputFile.writeInt(node.getValue().getBytes(StandardCharsets.UTF_8).length);
                        outputFile.write(node.getKey().getBytes(StandardCharsets.UTF_8));
                        outputFile.write(node.getValue().getBytes(StandardCharsets.UTF_8));
                    }
                    outputFile.setLength(outputFile.getFilePointer());
                }
            }
        } catch (FileNotFoundException e) {
            throw new DataBaseException("File save error!");
        } catch (IOException e) {
            throw new DataBaseException("Write to file error!");
        }
    }

    public String put(final String key, final String value) {
        state.check();
        String result = null;

        if (diff.get().containsKey(key)) {
            result = diff.get().get(key);
        } else {
            readLock.lock();
            try {
                if (old.containsKey(key)) {
                    result = old.get(key);
                }
            } finally {
                readLock.unlock();
            }
        }

        if (deleted.get().contains(key)) {
            deleted.get().remove(key);
            result = null;
        }

        diff.get().put(key, value);

        return result;
    }

    public String get(final String key) {
        state.check();
        if (deleted.get().contains(key)) {
            return null;
        }

        if (diff.get().containsKey(key)) {
            return diff.get().get(key);
        }

        readLock.lock();
        try {
            if (old.containsKey(key)) {
                return old.get(key);
            }
        } finally {
            readLock.unlock();
        }

        return null;
    }

    public String remove(final String key) {
        state.check();
        if (deleted.get().contains(key)) {
            return null;
        }

        String result = null;

        if (diff.get().containsKey(key)) {
            result = diff.get().get(key);
            diff.get().remove(key);
            deleted.get().add(key);
            return result;
        }

        readLock.lock();
        try {
            if (old.containsKey(key)) {
                result = old.get(key);
                deleted.get().add(key);
            }
        } finally {
            readLock.unlock();
        }

        return result;
    }

    private void normalize() {
        state.check();
        Set<String> newDeleted = new HashSet<>();
        newDeleted.addAll(deleted.get());

        for (String key : old.keySet()) {
            if (old.get(key).equals(diff.get().get(key))) {
                diff.get().remove(key);
            }
            if (newDeleted.contains(key)) {
                newDeleted.remove(key);
            }
        }

        for (String key : deleted.get()) {
            if (diff.get().containsKey(key)) {
                diff.get().remove(key);
            }
        }

        for (String key : newDeleted) {
            deleted.get().remove(key);
        }
    }

    public int getNewKeys() {
        state.check();
        readLock.lock();
        try {
            normalize();
            return diff.get().size() + deleted.get().size();
        } finally {
            readLock.unlock();
        }
    }

    public int getSize() {
        state.check();
        readLock.lock();
        try {
            normalize();
            int result = diff.get().size() + old.size() - deleted.get().size();
            for (String key : diff.get().keySet()) {
                if (old.containsKey(key)) {
                    --result;
                }
            }
            return result;
        } finally {
            readLock.unlock();
        }
    }

    public void commit() {
        state.check();
        normalize();

        if (diff.get().size() == 0 && deleted.get().size() == 0) {
            return;
        }

        for (Map.Entry<String, String> node : diff.get().entrySet()) {
            old.put(node.getKey(), node.getValue());
        }

        for (String key : deleted.get()) {
            old.remove(key);
        }


        diff.get().clear();
        deleted.get().clear();

        save();
    }

    public void rollback() {
        state.check();
        diff.get().clear();
        deleted.get().clear();
    }

    @Override
    public void close() {
        if (state.isClosed()) {
            return;
        }
        rollback();
        old.clear();
        state.close();
    }

    @Override
    public String toString() {
        state.check();
        return String.format("%s[%s]", getClass().getSimpleName(), fileName);
    }
}
