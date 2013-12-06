package ru.fizteh.fivt.students.kislenko.multifilemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Utils {
    private static final int MAX_TABLE_SIZE = 100 * 1024 * 1024;
    private static final int MAX_FILE_SIZE = 50 * 1024 * 1024;

    public static void loadFile(MyTable table, TwoLayeredString key) throws IOException {
        byte nDirectory = getDirNumber(key);
        byte nFile = getFileNumber(key);
        if (!table.isUsing(nDirectory, nFile) && table.getPath().toFile().exists()) {
            File temp = new File(table.getPath().resolve(nDirectory + ".dir").resolve(nFile + ".dat").toString());
            if (temp.exists()) {
                RandomAccessFile file = new RandomAccessFile(temp, "r");
                readFile(table, file);
                file.close();
            }
            table.setUsing(nDirectory, nFile, true);
        }
    }

    public static void readFile(MyTable table, RandomAccessFile datafile) throws IOException {
        int keyLength;
        int valueLength;
        String key;
        String value;
        table.setSize(table.getSize() + datafile.length());
        if (table.getSize() > MAX_TABLE_SIZE) {
            dumpTable(table);
            table.getMap().clear();
        }
        if (datafile.length() > MAX_FILE_SIZE) {
            datafile.close();
            throw new IOException("Too big datafile.");
        }
        table.setSize(table.getSize() + datafile.length());
        while (datafile.getFilePointer() != datafile.length()) {
            keyLength = datafile.readInt();
            if (keyLength < 1 || keyLength > datafile.length() - datafile.getFilePointer() + 4) {
                datafile.close();
                throw new IOException("Incorrect key length in input.");
            }
            valueLength = datafile.readInt();
            if (valueLength < 1 || valueLength > datafile.length() - datafile.getFilePointer() + 4) {
                datafile.close();
                throw new IOException("Incorrect value length in input.");
            }

            byte[] keySymbols = new byte[keyLength];
            byte[] valueSymbols = new byte[valueLength];
            datafile.read(keySymbols);
            datafile.read(valueSymbols);
            key = new String(keySymbols, StandardCharsets.UTF_8);
            value = new String(valueSymbols, StandardCharsets.UTF_8);
            table.put(key, value);
        }
    }

    public static void dumpTable(MyTable table) throws IOException {
        if (table == null) {
            return;
        }
        table.setSize(0);
        File[] dirs = new File[16];
        Map<String, TwoLayeredString> strings = new HashMap<String, TwoLayeredString>();
        Map<Integer, File> files = new TreeMap<Integer, File>();
        Map<Integer, RandomAccessFile> datafiles = new TreeMap<Integer, RandomAccessFile>();
        for (int i = 0; i < 16; ++i) {
            dirs[i] = table.getPath().resolve(i + ".dir").toFile();
            if (!dirs[i].exists()) {
                dirs[i].mkdir();
            }
            for (int j = 0; j < 16; ++j) {
                if (table.isUsing(i, j)) {
                    File tempFile = new File(table.getPath().resolve(i + ".dir").resolve(j + ".dat").toString());
                    files.put(getNumber(i, j), tempFile);
                    if (!files.get(getNumber(i, j)).exists()) {
                        files.get(getNumber(i, j)).createNewFile();
                    }
                    RandomAccessFile tempDatafile = new RandomAccessFile(files.get(getNumber(i, j)), "rw");
                    datafiles.put(getNumber(i, j), tempDatafile);
                    datafiles.get(getNumber(i, j)).setLength(0);
                }
            }
        }
        Set<String> keySet = table.getMap().keySet();
        for (String s : keySet) {
            TwoLayeredString key = new TwoLayeredString(s);
            datafiles.get(getHash(key)).writeInt(key.getKey().getBytes(StandardCharsets.UTF_8).length);
            datafiles.get(getHash(key)).writeInt(table.get(key.getKey()).getBytes(StandardCharsets.UTF_8).length);
            datafiles.get(getHash(key)).write(key.getKey().getBytes(StandardCharsets.UTF_8));
            datafiles.get(getHash(key)).write(table.get(key.getKey()).getBytes(StandardCharsets.UTF_8));
        }
        closeDescriptors(datafiles);
        deleteUnnecessaryFiles(dirs, files);
        setUsings(table);
    }

    private static void deleteUnnecessaryFiles(File[] dirs, Map<Integer, File> files) throws IOException {
        for (Integer i : files.keySet()) {
            RandomAccessFile f = new RandomAccessFile(files.get(i), "rw");
            if (f.length() == 0) {
                f.close();
                files.get(i).delete();
            }
            f.close();
        }
        for (File dir : dirs) {
            if (dir.listFiles().length == 0) {
                dir.delete();
            }
        }
    }

    private static void closeDescriptors(Map<Integer, RandomAccessFile> datafiles) throws IOException {
        for (Integer i : datafiles.keySet()) {
            datafiles.get(i).close();
        }
    }

    private static void setUsings(MyTable table) {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                table.setUsing(i, j, false);
            }
        }
    }

    private static int getHash(TwoLayeredString key) {
        return 16 * getDirNumber(key) + getFileNumber(key);
    }

    private static int getNumber(int dir, int file) {
        return 16 * dir + file;
    }

    public static byte getDirNumber(TwoLayeredString key) {
        return (byte) (Math.abs(key.getBytes()[0]) % 16);
    }

    public static byte getFileNumber(TwoLayeredString key) {
        return (byte) ((Math.abs(key.getBytes()[0]) / 16) % 16);
    }
}
