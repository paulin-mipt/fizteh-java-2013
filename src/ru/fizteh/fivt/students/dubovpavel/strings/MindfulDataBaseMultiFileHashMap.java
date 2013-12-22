package ru.fizteh.fivt.students.dubovpavel.strings;

import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.DataBaseMultiFileHashMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MindfulDataBaseMultiFileHashMap<V> extends DataBaseMultiFileHashMap<V> {
    private HashMap<String, V> oldDict;
    protected ObjectTransformer<V> transformer;

    MindfulDataBaseMultiFileHashMap(File path, ObjectTransformer<V> transformer) {
        super(path, transformer);
        oldDict = new HashMap<>();
        this.transformer = transformer;
    }

    protected void copyHashMap(HashMap<String, V> from, HashMap<String, V> to) {
        to.clear();
        for (Map.Entry<String, V> entry : from.entrySet()) {
            to.put(entry.getKey(), transformer.copy(entry.getValue()));
        }
    }

    @Override
    public void open() throws DataBaseException {
        try {
            super.open();
        } finally {
            copyHashMap(localDict, oldDict);
        }
    }

    public String getName() {
        return getPath().getName();
    }

    public int size() {
        return localDict.size();
    }

    public int getDiff() {
        int diff = 0;
        for (Map.Entry<String, V> entry : localDict.entrySet()) { // Check for new and changed values
            if (!oldDict.containsKey(entry.getKey())
                    || !transformer.equal(oldDict.get(entry.getKey()), entry.getValue())) {
                    // Order of .equals is important here
                diff++;
            }
        }
        for (Map.Entry<String, V> entry : oldDict.entrySet()) { // Check for removed values
            if (!localDict.containsKey(entry.getKey())) {
                diff++;
            }
        }
        return diff;
    }

    public int commit() throws DataBaseException {
        int diff = getDiff();
        save();
        copyHashMap(localDict, oldDict);
        return diff;
    }

    public int rollback() {
        int diff = getDiff();
        copyHashMap(oldDict, localDict);
        return diff;
    }
}
