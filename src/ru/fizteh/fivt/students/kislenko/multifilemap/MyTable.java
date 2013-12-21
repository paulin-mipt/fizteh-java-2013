package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MyTable implements Table {
    private String name;
    private Map<String, String> storage;
    private Map<String, String> changes;
    private boolean[][] uses;
    private long byteSize;
    private int oldCount;
    private int count;

    public MyTable(String tableName) {
        name = tableName;
        storage = new HashMap<String, String>();
        changes = new HashMap<String, String>();
        byteSize = 0;
        uses = new boolean[16][16];
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                uses[i][j] = false;
            }
        }
        oldCount = storage.size();
        count = oldCount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to get.");
        }
        String newKey = key.trim();
        if (newKey.isEmpty()) {
            throw new IllegalArgumentException("Incorrect key to get.");
        }
        if (changes.containsKey(newKey)) {
            return changes.get(newKey);
        }
        return storage.get(newKey);
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Incorrect key/value to put.");
        }
        String newKey = key.trim();
        String newValue = value.trim();
        if (newKey.isEmpty() || newValue.isEmpty()) {
            throw new IllegalArgumentException("Incorrect key/value to put.");
        }
        if ((!changes.containsKey(newKey) && !storage.containsKey(newKey))
                || (changes.containsKey(newKey) && changes.get(newKey) == null)) {
            ++count;
        }
        TwoLayeredString twoLayeredKey = new TwoLayeredString(newKey);
        uses[Utils.getDirNumber(twoLayeredKey)][Utils.getFileNumber(twoLayeredKey)] = true;
        String s = get(newKey);
        changes.put(newKey, newValue);
        if (value.equals(storage.get(newKey))) {
            changes.remove(newKey);
        }
        return s;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        String newKey = key.trim();
        if (newKey.isEmpty()) {
            throw new IllegalArgumentException("Incorrect key to remove.");
        }
        if (changes.get(newKey) != null || (!changes.containsKey(newKey) && storage.get(newKey) != null)) {
            --count;
        }
        TwoLayeredString twoLayeredKey = new TwoLayeredString(newKey);
        uses[Utils.getDirNumber(twoLayeredKey)][Utils.getFileNumber(twoLayeredKey)] = true;
        String s = get(newKey);
        changes.put(newKey, null);
        if (storage.get(newKey) == null) {
            changes.remove(newKey);
        }
        return s;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public int commit() {
        for (String key : changes.keySet()) {
            if (changes.get(key) == null) {
                storage.remove(key);
            } else {
                storage.put(key, changes.get(key));
            }
        }
        int n = changes.size();
        changes.clear();
        oldCount = count;
        return n;
    }

    @Override
    public int rollback() {
        int n = changes.size();
        changes.clear();
        count = oldCount;
        return n;
    }

    public boolean isUsing(int nDirectory, int nFile) {
        return uses[nDirectory][nFile];
    }

    public void clear() {
        storage.clear();
        changes.clear();
    }

    public Map<String, String> getMap() {
        return storage;
    }

    public Path getPath() {
        File f = new File(name);
        return f.toPath();
    }

    public void setUsing(int i, int j, boolean b) {
        uses[i][j] = b;
    }

    public void setByteSize(long newSize) {
        byteSize = newSize;
    }

    public long getByteSize() {
        return byteSize;
    }

    public int getChangeCount() {
        return changes.size();
    }
}
