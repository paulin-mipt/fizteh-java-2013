package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class NewTable implements Table, AutoCloseable {
    private final String name;
    private HashMap<String, Storeable> dataMap = new HashMap<>();
    private final NewTableProvider provider;
    private final ArrayList<Class<?>> types;
    private ReadWriteLock controller = new ReentrantReadWriteLock(true);
    private CloseState state = new CloseState();
    private ThreadLocal<HashMap<String, Storeable>> localMap = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        protected HashMap<String, Storeable> initialValue() {
            return new HashMap<String, Storeable>();
        }
    };

    public NewTable(String newName, NewTableProvider newProvider) throws IOException {
        File file = new File(newProvider.getCurrentDirectory(), newName);
        if (file.listFiles().length != 0) {
            for (File directory : file.listFiles()) {
                if (directory.getName().equals("signature.tsv")) {
                    continue;
                }
                if (!checkNameOfDataBaseDirectory(directory.getName()) || !directory.isDirectory()
                        || directory.listFiles().length == 0) {
                    throw new IOException("empty dir");
                }
                for (File dat : directory.listFiles()) {
                    if (!checkNameOfFiles(dat.getName()) || !dat.isFile() || dat.length() == 0) {
                        throw new IOException(dat.getCanonicalPath() + "  empty file");
                    }
                }
            }
        } else {
            throw new IOException("wrong type (no signature)");
        }
        name = newName;
        provider = newProvider;
        types = readSignature();
    }

    private boolean checkNameOfDataBaseDirectory(String dir) {
        return (dir.matches("(([0-9])|(1[0-5]))\\.dir"));
    }

    private boolean checkNameOfFiles(String file) {
        return file.matches("(([0-9])|(1[0-5]))\\.dat");
    }

    private ArrayList<Class<?>> readSignature() throws IOException {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        Scanner scanner = null;
        FileInputStream stream = null;
        try {
            File sign = new File(provider.getCurrentDirectory() + File.separator + this.name + File.separator
                    + "signature.tsv");
            if (!sign.exists()) {
                throw new IOException("Signature does not exist");
            }
            stream = new FileInputStream(sign);
            scanner = new Scanner(stream);
            int i = 0;
            while (scanner.hasNext()) {
                list.add(provider.getNameClass(scanner.next()));
                if (list.get(i) == null) {
                    throw new IOException("Bad signature");
                }
                ++i;
            }
        } finally {
            scanner.close();
            stream.close();
        }
        if (list.isEmpty()) {
            throw new IOException("Signature is empty");
        }
        return list;
    }

    @Override
    public String getName() {
        state.checkClosed();
        return name;
    }

    private boolean checkName(String name) {
        return (name == null || name.trim().isEmpty() || name.split("\\s").length > 1 || name.contains("\t")
                || name.contains(System.lineSeparator()) || name.contains("[") || name.contains("]"));
    }

    private void checkKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("wrong type (incorrect key)");
        }
    }

    @Override
    public int commit() throws IOException {
        state.checkClosed();
        int count = 0;
        controller.writeLock().lock();
        try {
            count = unsavedChanges();
            if (count != 0) {
                for (Map.Entry<String, Storeable> entry : localMap.get().entrySet()) {
                    if (dataMap.containsKey(entry.getKey()) && !dataMap.get(entry.getKey()).equals(entry.getValue())) {
                        if (entry.getValue() != null) {
                            dataMap.put(entry.getKey(), entry.getValue());
                        } else {
                            dataMap.remove(entry.getKey());
                        }
                    } else {
                        if (entry.getValue() != null) {
                            dataMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                provider.saveChanges(this);
            }
        } finally {
            controller.writeLock().unlock();
        }
        localMap.get().clear();
        return count;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        state.checkClosed();
        if (checkName(key)) {
            throw new IllegalArgumentException("wrong type (incorrect key)");
        }
        if (value == null) {
            throw new IllegalArgumentException("wrong type (value is null)");
        }
        checkStoreable(value);
        Storeable result = null;
        if (localMap.get().containsKey(key)) {
            result = localMap.get().get(key);
        } else {
            controller.readLock().lock();
            try {
                if (dataMap.containsKey(key)) {
                    result = dataMap.get(key);
                }
            } finally {
                controller.readLock().unlock();
            }
        }
        localMap.get().put(key, value);
        return result;
    }

    private void checkStoreable(Storeable value) {
        int i = 0;
        try {
            for (i = 0; i < types.size(); ++i) {
                if (value.getColumnAt(i) != null && !value.getColumnAt(i).getClass().equals(types.get(i))) {
                    throw new ColumnFormatException("wrong type (Storeable invalid in types)");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("wrong type (Storeable invalid in index)");
        }
        try {
            value.getColumnAt(i);
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        throw new ColumnFormatException("wrong type (Storeable invalid out of range)");
    }

    public int unsavedChanges() {
        int count = 0;
        for (Map.Entry<String, Storeable> entry : localMap.get().entrySet()) {
            if (dataMap.containsKey(entry.getKey())) {
                if (!dataMap.get(entry.getKey()).equals(entry.getValue())) {
                    ++count;
                }
            } else {
                if (entry.getValue() != null) {
                    ++count;
                }
            }
        }
        return count;
    }

    @Override
    public Storeable remove(String key) {
        state.checkClosed();
        checkKey(key);
        Storeable oldVal = null;
        if (localMap.get().containsKey(key)) {
            oldVal = localMap.get().get(key);
        } else {
            controller.readLock().lock();
            try {
                if (dataMap.containsKey(key)) {
                    oldVal = dataMap.get(key);
                }
            } finally {
                controller.readLock().unlock();
            }
        }
        localMap.get().put(key, null);
        return oldVal;
    }

    @Override
    public Storeable get(String key) {
        state.checkClosed();
        checkKey(key);
        if (!localMap.get().containsKey(key)) {
            controller.readLock().lock();
            try {
                return dataMap.get(key);
            } finally {
                controller.readLock().unlock();
            }
        } else {
            return localMap.get().get(key);
        }
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        state.checkClosed();
        checkIndex(columnIndex);
        return types.get(columnIndex);
    }

    private void checkIndex(int columnIndex) {
        if (columnIndex < 0 || columnIndex > types.size() - 1) {
            throw new IndexOutOfBoundsException("wrong type (Incorrect column index)");
        }
    }

    @Override
    public int getColumnsCount() {
        state.checkClosed();
        return types.size();
    }

    @Override
    public int rollback() {
        state.checkClosed();
        int count = unsavedChanges();
        localMap.get().clear();
        return count;
    }

    @Override
    public int size() {
        state.checkClosed();
        int count = 0;
        controller.readLock().lock();
        try {
            count = dataMap.size();
            for (Map.Entry<String, Storeable> entry : localMap.get().entrySet()) {
                if (dataMap.containsKey(entry.getKey())) {
                    if (entry.getValue() == null) {
                        --count;
                    }
                } else if (entry.getValue() != null) {
                    ++count;
                }
            }
        } finally {
            controller.readLock().unlock();
        }
        return count;
    }

    public void loadCommitedValues(HashMap<String, Storeable> load) throws IOException, ParseException {
        controller.writeLock().lock();
        try {
            dataMap = load;
        } finally {
            controller.writeLock().unlock();
        }
    }

    public HashMap<String, String> returnMap() {
        HashMap<String, String> map = new HashMap<>();
        controller.readLock().lock();
        try {
            for (String key : dataMap.keySet()) {
                map.put(key, JSONSerializer.serialize(this, dataMap.get(key)));
            }
        } finally {
            controller.readLock().unlock();
        }
        return map;
    }

    public NewTableProvider getTableProvider() {
        return provider;
    }

    @Override
    public void close() throws IOException {
        if (!state.isClose()) {
            rollback();
            state.setClose();
            provider.setClose(name);
        }

    }

    @Override
    public String toString() {
        state.checkClosed();
        File file = new File(provider.getCurrentDirectory(), name);
        return this.getClass().getSimpleName() + "[" + file.getAbsolutePath() + "]";
    }

}
