package ru.fizteh.fivt.students.drozdowsky.database;

import java.awt.geom.IllegalPathStateException;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.drozdowsky.utils.Utils;

public class FileHashMap implements Table {
    private static final int NDIRS = 16;
    private static final int NFILES = 16;

    private File db;
    private FileMap[][] base;
    private FileMap[][] baseBackUp;

    public FileHashMap(File db) {
        this.db = db;
        base = new FileMap[NDIRS][NFILES];
        readDB();
        baseBackUp = new FileMap[NDIRS][NFILES];
        copy(baseBackUp, base);
    }

    public String getName() {
        return db.getName();
    }

    public String get(String key) {
        if (!Utils.isValid(key)) {
            throw new IllegalArgumentException();
        }
        int nDir = getDirNum(key);
        int nFile = getFileNum(key);
        return base[nDir][nFile].get(key);
    }

    public String put(String key, String value) {
        if (value == null || value.equals("") || value.contains(System.lineSeparator())) {
            throw new IllegalArgumentException();
        }
        if (!Utils.isValid(key)) {
            throw new IllegalArgumentException();
        }
        int nDir = getDirNum(key);
        int nFile = getFileNum(key);

        return base[nDir][nFile].put(key, value);
    }

    public String remove(String key) {
        if (!Utils.isValid(key)) {
            throw new IllegalArgumentException();
        }
        int nDir = getDirNum(key);
        int nFile = getFileNum(key);
        return base[nDir][nFile].remove(key);
    }

    public int size() {
        int result = 0;
        for (int i = 0; i < NDIRS; i++) {
            for (int j = 0; j < NDIRS; j++) {
                result += base[i][j].size();
            }
        }
        return result;
    }

    public int commit() {
        int result = difference();
        writeDB();
        copy(baseBackUp, base);
        return result;
    }

    public int rollback() {
        int result = difference();
        copy(base, baseBackUp);
        return result;
    }

    public int difference() {
        int result = 0;
        for (int i = 0; i < NDIRS; i++) {
            for (int j = 0; j < NDIRS; j++) {
                result += compare(base[i][j], baseBackUp[i][j]);
            }
        }
        return result;
    }

    private int compare(FileMap a, FileMap b) {
        int result = 0;
        Set<String> tmp = new TreeSet<>(a.getKeys());
        tmp.removeAll(b.getKeys());
        result += tmp.size();
        tmp = new TreeSet<>(b.getKeys());
        tmp.removeAll(a.getKeys());
        result += tmp.size();

        for (String x : a.getKeys()) {
            if (b.getKeys().contains(x)) {
                if (!a.get(x).equals(b.get(x))) {
                    result++;
                }
            }
        }
        return result;
    }

    public void close() {
        writeDB();
    }

    private int getDirNum(String key) {
        if (!Utils.isValid(key)) {
            throw new IllegalArgumentException();
        }
        byte b = key.getBytes()[0];
        if (b < 0) {
            b *= -1;
        }
        return b % 16;
    }

    private int getFileNum(String key) {
        if (!Utils.isValid(key)) {
            throw new IllegalArgumentException();
        }
        byte b = key.getBytes()[0];
        if (b < 0) {
            b *= -1;
        }
        return (b / 16) % 16;
    }

    private void readDB() {
        if (db.exists() && !db.isDirectory()) {
            throw new IllegalPathStateException(db.getAbsolutePath() + ": Not a directory");
        }

        for (int i = 0; i < NDIRS; i++) {
            for (int j = 0; j < NFILES; j++) {
                base[i][j] = new FileMap();
            }
        }

        File[] directories = db.listFiles();
        for (File directory : (directories != null ? directories : new File[0])) {
            int nDir = dirNameInRange(directory.getName(), NDIRS);
            if (nDir == -1 || !(directory.isDirectory())) {
                throw new IllegalStateException(db.getAbsolutePath() + ": Not valid database " + directory.getName());
            }

            File[] files = directory.listFiles();
            for (File file : (files != null ? files : new File[0])) {
                int nFile = fileNameInRange(file.getName(), NFILES);
                if (nFile == -1 || !(file.isFile())) {
                    throw new IllegalStateException(db.getAbsolutePath() + ": Not valid database " + file.getName());
                }

                base[nDir][nFile].read(file);
                Set<String> keys = base[nDir][nFile].getKeys();
                for (String key : keys) {
                    int realNDir = getDirNum(key);
                    int realNFile = getFileNum(key);
                    if (!(nDir == realNDir && nFile == realNFile)) {
                        throw new IllegalStateException(db.getAbsolutePath() + ": Not valid database");
                    }
                }
            }
        }
    }

    private void writeDB() {
        for (int i = 0; i < NDIRS; i++) {
            File dirPath = new File(db.getAbsolutePath() + File.separator + Integer.toString(i) + ".dir");
            if (!dirPath.exists() && !dirPath.mkdir()) {
                throw new IllegalPathStateException(dirPath.getAbsolutePath() + ": Permission denied");
            }
            for (int j = 0; j < NFILES; j++) {
                if (base[i][j] != null) {
                    File filePath = new File(dirPath.getAbsolutePath() + File.separator + Integer.toString(j) + ".dat");
                    if (!filePath.exists()) {
                        try {
                            if (!filePath.createNewFile()) {
                                throw new IllegalPathStateException(filePath.getAbsolutePath() + ": Permission denied");
                            }
                        } catch (IOException e) {
                            throw new IllegalPathStateException(filePath.getAbsolutePath() + ": Permission denied");
                        }
                    }
                    if (base[i][j].getKeys().size() == 0) {
                        if (filePath.exists() && !filePath.delete()) {
                            throw new IllegalPathStateException(filePath.getAbsolutePath() + ": Permission denied");
                        }
                    } else {
                        base[i][j].write(filePath);
                    }
                }
            }
            if (dirPath.exists() && dirPath.list().length == 0 && !dirPath.delete()) {
                throw new IllegalPathStateException(dirPath.getAbsolutePath() + ": Permission denied");
            }
        }
    }

    private void copy(FileMap[][] a, FileMap[][] b) {
        for (int i = 0; i < NDIRS; i++) {
            for (int j = 0; j < NFILES; j++) {
                a[i][j] = b[i][j].clone();
            }
        }
    }

    private int dirNameInRange(String s, int range) {
        for (int i = 0; i < range; i++) {
            if ((Integer.toString(i) + ".dir").equals(s)) {
                return i;
            }
        }
        return -1;
    }

    private int fileNameInRange(String s, int range) {
        for (int i = 0; i < range; i++) {
            if ((Integer.toString(i) + ".dat").equals(s)) {
                return i;
            }
        }
        return -1;
    }
}
