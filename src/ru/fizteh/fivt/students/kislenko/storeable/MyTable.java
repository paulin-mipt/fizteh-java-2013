package ru.fizteh.fivt.students.kislenko.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kislenko.multifilemap.TwoLayeredString;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTable implements Table, AutoCloseable {
    private String name;
    private MyTableProvider provider;
    private ArrayList<Class<?>> types;
    private HashMap<String, Storeable> storage;
    private boolean[][] globalUses;
    private int revision;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean closed = false;

    private ThreadLocal<boolean[][]> uses = new ThreadLocal<boolean[][]>() {
        @Override
        public boolean[][] initialValue() {
            boolean[][] temp = new boolean[16][16];
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    temp[i][j] = false;
                }
            }
            return temp;
        }
    };
    private ThreadLocal<Integer> count = new ThreadLocal<Integer>() {
        @Override
        public Integer initialValue() {
            return 0;
        }
    };
    private ThreadLocal<Integer> threadRevision = new ThreadLocal<Integer>() {
        @Override
        public Integer initialValue() {
            return 0;
        }
    };
    private ThreadLocal<HashMap<String, Storeable>> changes = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        public HashMap<String, Storeable> initialValue() {
            return new HashMap<String, Storeable>();
        }
    };
    private ThreadLocal<HashMap<String, Storeable>> fuckingDiff = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        public HashMap<String, Storeable> initialValue() {
            return new HashMap<String, Storeable>();
        }
    };

    public MyTable(String tableName, List<Class<?>> columnTypes, MyTableProvider parent) {
        name = tableName;
        provider = parent;
        storage = new HashMap<String, Storeable>();
        revision = 0;
        globalUses = new boolean[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                globalUses[i][j] = false;
            }
        }
        types = new ArrayList<Class<?>>(columnTypes);
    }

    @Override
    public String getName() {
        assertClosed();
        return name;
    }

    @Override
    public Storeable get(String key) {
        assertClosed();
        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to get.");
        }
        if (key.trim().isEmpty() || key.matches("(.+\\s+.+)+")) {
            throw new IllegalArgumentException("Incorrect key to get.");
        }
        lock.readLock().lock();
        try {
            resetTable();
            if (changes.get().containsKey(key)) {
                return changes.get().get(key);
            }
            assertClosed();
            return storage.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        assertClosed();
        if (key == null || value == null) {
            throw new IllegalArgumentException("Incorrect key/value to put.");
        }
        if (key.trim().isEmpty() || key.matches("(.+\\s+.+)+")) {
            throw new IllegalArgumentException("Incorrect key to put.");
        }
        try {
            for (int i = 0; i < types.size(); ++i) {
                if (value.getColumnAt(i) != null && !types.get(i).equals(value.getColumnAt(i).getClass())) {
                    throw new ColumnFormatException("Incorrect value to put.");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("Incorrect value to put.");
        }
        if (!tryToGetUnnecessaryColumn(value)) {
            throw new ColumnFormatException("Incorrect value to put.");
        }
        lock.readLock().lock();
        try {
            resetTable();
            if ((!changes.get().containsKey(key) && !storage.containsKey(key))
                    || (changes.get().containsKey(key) && changes.get().get(key) == null)) {
                count.set(count.get() + 1);
            }
            TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
            uses.get()[Utils.getDirNumber(twoLayeredKey)][Utils.getFileNumber(twoLayeredKey)] = true;
            Storeable v = get(key);
            String copyOfKey = "".concat(key);
            Storeable copyOfValue = provider.createFor(this);
            for (int i = 0; i < types.size(); ++i) {
                copyOfValue.setColumnAt(i, value.getColumnAt(i));
            }
            changes.get().put(copyOfKey, copyOfValue);
            if (fuckingDiff.get().containsKey(key)
                    && !provider.serialize(this, fuckingDiff.get().get(key)).equals(provider.serialize(this, value))) {
                fuckingDiff.get().remove(key);
            }
            if (storage.get(key) != null
                    && provider.serialize(this, value).equals(provider.serialize(this, storage.get(key)))) {
                changes.get().remove(key);
                fuckingDiff.get().put(copyOfKey, copyOfValue);
            }
            assertClosed();
            return v;
        } finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public Storeable remove(String key) {
        assertClosed();
        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        if (key.trim().isEmpty() || key.matches("(.+\\s+.+)+")) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        lock.readLock().lock();
        try {
            resetTable();
            if (changes.get().get(key) != null || (!changes.get().containsKey(key) && storage.get(key) != null)) {
                count.set(count.get() - 1);
            }
            TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
            uses.get()[Utils.getDirNumber(twoLayeredKey)][Utils.getFileNumber(twoLayeredKey)] = true;
            Storeable v = get(key);
            changes.get().put(key, null);
            if (fuckingDiff.get().containsKey(key) && fuckingDiff.get().get(key) != null) {
                fuckingDiff.get().remove(key);
            }
            if (storage.get(key) == null) {
                changes.get().remove(key);
                fuckingDiff.get().put(key, null);
            }
            assertClosed();
            return v;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        assertClosed();
        try {
            lock.readLock().lock();
            resetTable();
            assertClosed();
            return count.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int commit() throws IOException {
        assertClosed();
        lock.writeLock().lock();
        try {
            resetTable();
            int n = 0;
            for (String key : fuckingDiff.get().keySet()) {
                if (fuckingDiff.get().get(key) == null && storage.get(key) != null) {
                    storage.remove(key);
                    ++n;
                } else if (fuckingDiff.get().get(key) != null && !provider.serialize(this, fuckingDiff.get().get(key)).
                        equals(provider.serialize(this, storage.get(key)))) {
                    storage.put(key, fuckingDiff.get().get(key));
                    ++n;
                }
            }
            for (String key : changes.get().keySet()) {
                if (changes.get().get(key) == null) {
                    storage.remove(key);
                } else {
                    storage.put(key, changes.get().get(key));
                }
            }
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    globalUses[i][j] = uses.get()[i][j];
                }
            }
            n += changes.get().size();
            changes.get().clear();
            fuckingDiff.get().clear();
            revision++;
            threadRevision.set(revision);
            assertClosed();
            return n;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int rollback() {
        assertClosed();
        return privateRollback();
    }

    private int privateRollback() {
        lock.readLock().lock();
        try {
            resetTable();
            int n = changes.get().size();
            changes.get().clear();
            fuckingDiff.get().clear();
            count.set(storage.size());
            return n;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getColumnsCount() {
        assertClosed();
        return types.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        assertClosed();
        if (columnIndex < 0 || columnIndex > types.size()) {
            throw new IndexOutOfBoundsException("Incorrect column number.");
        }
        assertClosed();
        return types.get(columnIndex);
    }

    public ArrayList<Class<?>> getTypes() {
        return types;
    }

    public HashMap<String, Storeable> getStorage() {
        return storage;
    }

    public void setStorage(HashMap<String, Storeable> s) {
        storage.putAll(s);
    }

    public Path getPath() {
        assertClosed();
        return Paths.get(name);
    }

    private boolean tryToGetUnnecessaryColumn(Storeable value) {
        try {
            value.getColumnAt(types.size());
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
        return false;
    }

    private void resetTable() {
        if (revision == threadRevision.get()) {
            return;
        }
        threadRevision.set(revision);
        count.set(storage.size());
        HashMap<String, Storeable> tempMap = new HashMap<String, Storeable>();
        for (String key : changes.get().keySet()) {
            if (changes.get().get(key) == null && !storage.containsKey(key)) {
                changes.get().remove(key);
            } else if (changes.get().get(key) == null && storage.containsKey(key)) {
                tempMap.put(key, null);
                count.set(count.get() - 1);
            } else if (!storage.containsKey(key)) {
                tempMap.put(key, changes.get().get(key));
                count.set(count.get() + 1);
            } else if (storage.containsKey(key) && !(storage.get(key) != null && provider.
                    serialize(this, changes.get().get(key)).equals(provider.serialize(this, storage.get(key))))) {
                tempMap.put(key, changes.get().get(key));
            }
        }
        changes.set(tempMap);
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                uses.get()[i][j] = globalUses[i][j];
            }
        }
    }

    @Override
    public void close() throws Exception {
        privateRollback();
        closed = true;
    }

    private void assertClosed() {
        if (closed) {
            throw new IllegalStateException("Table was closed.");
        }
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + provider.getPath().resolve(name).toFile().getAbsolutePath() + "]";
    }

    public MyTableProvider getProvider() {
        return provider;
    }

    public HashMap<String, Storeable> getMap() {
        return storage;
    }

    public void clear() {
        storage.clear();
    }

    public boolean isUsing(int i, int j) {
        return uses.get()[i][j];
    }

    public int getChangeCount() {
        return changes.get().size();
    }
}
