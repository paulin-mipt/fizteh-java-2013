package ru.fizteh.fivt.students.piakovenko.filemap.strings;


import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.piakovenko.filemap.Exit;
import ru.fizteh.fivt.students.piakovenko.filemap.Get;
import ru.fizteh.fivt.students.piakovenko.filemap.GlobalFileMapState;
import ru.fizteh.fivt.students.piakovenko.filemap.Put;
import ru.fizteh.fivt.students.piakovenko.shell.Remove;
import ru.fizteh.fivt.students.piakovenko.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 12.10.13
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */
public class DataBase implements Table {
    private String name;
    private RandomAccessFile raDataBaseFile = null;
    private DataBaseMap map = null;
    private Shell shell = null;
    private File dataBaseStorage = null;
    private int changed;

    private boolean isValidNameDirectory(String name) {
        if (name.length() < 5 || name.length() > 6) {
            return false;
        }
        int number = Integer.parseInt(name.substring(0, name.indexOf('.')), 10);
        if (number > 15 || number < 0) {
            return false;
        }
        if (!name.substring(name.indexOf('.') + 1).equals("dir")) {
            return false;
        }
        return true;
    }

    private boolean isValidNameFile(String name) {
        if (name.length() < 5 || name.length() > 6) {
            return false;
        }
        int number = Integer.parseInt(name.substring(0, name.indexOf('.')), 10);
        if (number > 15 || number < 0) {
            return false;
        }
        if (!name.substring(name.indexOf('.') + 1).equals("dat")) {
            return false;
        }
        return true;
    }

    private int ruleNumberDirectory(String key) {
        int b = Math.abs(key.getBytes()[0]);
        return b % 16;
    }

    private int ruleNumberFile(String key) {
        int b = Math.abs(key.getBytes()[0]);
        return b / 16 % 16;
    }

    private void readFromFile() throws IOException {
        long length = raDataBaseFile.length();
        while (length > 0) {
            int l1 = raDataBaseFile.readInt();
            if (l1 <= 0) {
                throw new IOException("Length of new key less or equals zero");
            } else if (l1 > 1024 * 1024) {
                throw new IOException("Key greater than 1 MB");
            }
            length -= 4;
            int l2 = raDataBaseFile.readInt();
            if (l2 <= 0) {
                throw new IOException("Length of new value less or equals zero");
            } else if (l2 > 1024 * 1024) {
                throw new IOException("Value greater than 1 MB");
            }
            length -= 4;
            byte[] key = new byte[l1];
            byte[] value = new byte[l2];
            if (raDataBaseFile.read(key) < l1) {
                throw new IOException("Key: read less, that it was pointed to read");
            } else {
                length -= l1;
            }
            if (raDataBaseFile.read(value) < l2) {
                throw new IOException("Value: read less, that it was pointed to read");
            } else {
                length -= l2;
            }
            map.primaryPut(new String(key, StandardCharsets.UTF_8), new String(value, StandardCharsets.UTF_8));
        }
    }

    private void readFromFile(File storage, int numberOfDirectory) throws IOException {
        RandomAccessFile ra = null;
        try {
            ra = new RandomAccessFile(storage, "rw");
            int numberOfFile = Integer.parseInt(storage.getName().substring(0, storage.getName().indexOf('.')), 10);
            long length = ra.length();
            while (length > 0) {
                int l1 = ra.readInt();
                if (l1 <= 0) {
                    throw new IOException("Length of new key less or equals zero");
                } else if (l1 > 1024 * 1024) {
                    throw new IOException("Key greater than 1 MB");
                }
                length -= 4;
                int l2 = ra.readInt();
                if (l2 <= 0) {
                    throw new IOException("Length of new value less or equals zero");
                } else if (l2 > 1024 * 1024) {
                    throw new IOException("Value greater than 1 MB");
                }
                length -= 4;
                byte[] key = new byte[l1];
                byte[] value = new byte[l2];
                if (ra.read(key) < l1) {
                    throw new IOException("Key: read less, that it was pointed to read");
                } else {
                    length -= l1;
                }
                if (ra.read(value) < l2) {
                    throw new IOException("Value: read less, that it was pointed to read");
                } else {
                    length -= l2;
                }
                String keyString = new String(key, StandardCharsets.UTF_8);
                String valueString = new String(value, StandardCharsets.UTF_8);
                if (ruleNumberFile(keyString) != numberOfFile || ruleNumberDirectory(keyString) != numberOfDirectory) {
                    throw new IOException("Wrong place of key value! Key: " + keyString + " Value: " + valueString);
                } else {
                    map.primaryPut(keyString, valueString);
                }
            }
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        } finally {
            if (ra != null) {
                ra.close();
            }
        }
    }

    private void saveToFile() throws IOException {
        long length = 0;
        raDataBaseFile.seek(0);
        for (String key : map.getMap().keySet()) {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = map.getMap().get(key).getBytes(StandardCharsets.UTF_8);
            raDataBaseFile.writeInt(keyBytes.length);
            raDataBaseFile.writeInt(valueBytes.length);
            raDataBaseFile.write(keyBytes);
            raDataBaseFile.write(valueBytes);
            length += 4 + 4 + keyBytes.length + valueBytes.length;
        }
        raDataBaseFile.setLength(length);
    }

    private void saveToFile(File f, String key, String value) throws IOException {
        RandomAccessFile ra = null;
        try {
            ra = new RandomAccessFile(f, "rw");
            ra.seek(ra.length());
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
            ra.writeInt(keyBytes.length);
            ra.writeInt(valueBytes.length);
            ra.write(keyBytes);
            ra.write(valueBytes);
        } finally {
            ra.close();
        }
    }

    private void saveToDirectory() throws IOException {
        if (dataBaseStorage.exists()) {
            Remove.removeRecursively(dataBaseStorage);
        }
        if (!dataBaseStorage.mkdirs()) {
            throw new IOException("Unable to create this directory - " + dataBaseStorage.getCanonicalPath());
        }
        for (String key : map.getMap().keySet()) {
            Integer numberOfDirectory = ruleNumberDirectory(key);
            Integer numberOfFile = ruleNumberFile(key);
            File directory = new File(dataBaseStorage, numberOfDirectory.toString() + ".dir");
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Unable to create this directory - " + directory.getCanonicalPath());
                }
            }
            File writeFile = new File(directory, numberOfFile.toString() + ".dat");
            if (!writeFile.exists()) {
                writeFile.createNewFile();
            }
            saveToFile(writeFile, key, map.getMap().get(key));
        }
    }

    private void loadDataBase(File dataBaseFile) throws IOException {
        raDataBaseFile = new RandomAccessFile(dataBaseFile, "rw");
        try {
            readFromFile();
        } catch (IOException e) {
            System.err.println("Error! " + e.getCause());
            System.exit(1);
        }
    }

    private void readFromDirectory(File dir, int numberOfDirectory) throws IOException {
        for (File f : dir.listFiles()) {
            if (!isValidNameFile(f.getName())) {
                throw new IOException("Wrong name of file!");
            }
            readFromFile(f, numberOfDirectory);
        }
    }

    private void loadFromDirectory(File directory) throws IOException {
        for (File f : directory.listFiles()) {
            if (!isValidNameDirectory(f.getName())) {
                throw new IOException("Wrong name of directory!");
            }
            int numberOfDirectory = Integer.parseInt(f.getName().substring(0, f.getName().indexOf('.')), 10);
            readFromDirectory(f, numberOfDirectory);
        }
    }

    public DataBase(Shell sl, File storage) {
        map = new DataBaseMap();
        shell = sl;
        dataBaseStorage = storage;
        name = storage.getName();
        changed = 0;
    }

    public DataBase() {
        map = new DataBaseMap();
        shell = new Shell();
        dataBaseStorage = new File(System.getProperty("fizteh.db.dir"));
        name = dataBaseStorage.getName();
        changed = 0;
    }

    public void load() throws IOException {
        if (dataBaseStorage.isFile()) {
            loadDataBase(dataBaseStorage);
        } else {
            loadFromDirectory(dataBaseStorage);
        }
    }

    public String getName() {
        return name;
    }

    public void initialize(GlobalFileMapState state) {
        shell.addCommand(new Exit(state));
        shell.addCommand(new Put(state));
        shell.addCommand(new Get(state));
        shell.addCommand(new ru.fizteh.fivt.students.piakovenko.filemap.Remove(state));
        try {
            load();
        } catch (IOException e) {
            System.err.println("Error! " + e.getMessage());
            System.exit(1);
        }
    }

    public void saveDataBase() throws IOException {
        if (dataBaseStorage.isFile()) {
            try {
                saveToFile();
            } finally {
                raDataBaseFile.close();
            }
        } else {
            saveToDirectory();
        }
    }

    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key equals NULL");
        }
        return map.get(key);
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null || key.trim().equals("") || value.trim().equals("")) {
            throw new IllegalArgumentException("key or value equals NULL");
        }
        String putValue = map.put(key, value);
        return putValue;
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key equals null");
        }
        String removed = map.remove(key);
        return removed;
    }

    public File returnFiledirectory() {
        return dataBaseStorage;
    }

    public int size() {
        System.out.println(map.getMap().size());
        return map.getMap().size();
    }

    public int commit() {
        int tempChanged = map.changed();
        map.commit();
        System.out.println(tempChanged);
        return tempChanged;
    }

    public int rollback() {
        int tempChanged = map.changed();
        map.getMap().clear();
        map.rollback();
        System.out.println(tempChanged);
        return tempChanged;
    }

    public int numberOfChanges() {
        return changed;
    }
}
