package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MyHashMap {
    int size;
    Set<String> keySet;
    final int numberDir = 16;
    final int numberFile = 16;
    Map<String, Storeable>[][] arrayMap = new HashMap[numberDir][numberFile];

    public MyHashMap() {
        for (int i = 0; i < numberDir; ++i) {
            for (int j = 0; j < numberFile; ++j) {
                arrayMap[i][j] = new HashMap<>();
            }
        }
        size = 0;
        keySet = new HashSet<>();
    }

    private Map<String, Storeable> initMap(String key) {
        Map<String, Storeable> map = arrayMap[getHashDir(key)][getHashFile(key)];
        return map;
    }

    public Storeable get(String key) {
        return initMap(key).get(key);
    }

    public void put(String key, Storeable value) {
        if (!keySet.contains(key)) {
            keySet.add(key);
            ++size;
        }
        initMap(key).put(key, value);
    }

    public void remove(String key) {
        if (keySet.contains(key)) {
            keySet.remove(key);
            --size;
        }
        initMap(key).remove(key);
    }

    public int getHashDir(String key) {
        int hashcode = key.hashCode();
        int ndirectory = hashcode % numberDir;
        if (ndirectory < 0) {
            ndirectory *= -1;
        }
        return ndirectory;
    }

    public int getHashFile(String key) {
        int hashcode = key.hashCode();
        int nfile = hashcode / numberDir % numberFile;
        if (nfile < 0) {
            nfile *= -1;
        }
        return nfile;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public Set<String> keySet() {
        return keySet;
    }

    public int size() {
        return size;
    }

    public Boolean containsKey(String key) {
        return (keySet.contains(key));
    }

    public void clear() {
        for (String key : keySet) {
            initMap(key).remove(key);
        }
        keySet = new HashSet<>();
        size = 0;
    }

    public Map<String, Storeable> getMap(int i, int j) {
        return arrayMap[i][j];
    }
}
