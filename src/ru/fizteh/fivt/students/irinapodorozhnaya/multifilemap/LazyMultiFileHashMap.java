package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.FileStorage;
import ru.fizteh.fivt.students.irinapodorozhnaya.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.WeakHashMap;

public class LazyMultiFileHashMap<ValueType> {
    private Map<Integer, Map<String, ValueType>> data = new WeakHashMap<>();
    private int size;
    private final File sizeFile;
    private final File rootDir;
    private GenericTable<ValueType> table;

    public LazyMultiFileHashMap(File rootDir, GenericTable table) throws IOException {
        sizeFile = new File(rootDir, "size.tsv");
        if (!sizeFile.exists()) {
            size = 0;
            for (int i = 0; i < 256; ++i) {
                size += loadFile(i).size();
            }
            if (!sizeFile.createNewFile()) {
                throw new IllegalArgumentException("can't create file");
            }
            try (PrintStream printStream = new PrintStream(sizeFile)) {
                printStream.print(size);
            }
        } else {
            try (Scanner sc = new Scanner(sizeFile)) {
                size = sc.nextInt();
            }
        }
        this.table = table;
        this.rootDir = rootDir;
    }

    public ValueType get(String key) throws IOException {
        int nfile = Utils.getNumberOfFile(key);
        if (!data.containsKey(nfile)) {
            return loadFile(nfile).get(key);
        } else {
            return data.get(nfile).get(key);
        }
    }

    private Map<String, ValueType> loadFile(int nfile) throws IOException {
        File dir = new File(rootDir, nfile / 16 + ".dir");
        if (!dir.isDirectory()) {
            return new HashMap<>();
        } else if (dir.listFiles().length == 0) {
            throw new IOException("empty dir");
        }
        File db = new File(dir, nfile % 16 + ".dat");
        if (db.isFile()) {
            Map<String, ValueType> fromFile = table.deserialize(FileStorage.openDataFile(db, nfile));
            data.put(nfile, fromFile);
            if (fromFile.isEmpty()) {
                throw new IOException("empty file");
            }
            return fromFile;
        }
        return new HashMap<>();
    }

    public Map<String, ValueType> getMap(int nfile) throws IOException {
        if (data.containsKey(nfile)) {
            return data.get(nfile);
        }
        return loadFile(nfile);
    }

    public Map<String, ValueType> putAllInMap(int nfile, Map<String, ValueType> newData) throws IOException {
        Map<String, ValueType> map = getMap(nfile);
        for (Map.Entry<String, ValueType> s: newData.entrySet()) {
            if (s.getValue() == null) {
                map.remove(s.getKey());
            } else {
                map.put(s.getKey(), s.getValue());
            }
        }
        data.put(nfile, map);
        return map;
    }

    public int size() {
        return size;
    }

    public void clear() {
        data.clear();
    }

    public void commitSize(int newSize) throws FileNotFoundException {
        try (PrintStream printStream = new PrintStream(sizeFile)) {
            printStream.print(newSize);
        }
        size = newSize;
    }
}
