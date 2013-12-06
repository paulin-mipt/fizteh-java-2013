package ru.fizteh.fivt.students.kislenko.junit;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Utils {
    public static void readTable(MyTable table) throws IOException {
        File tableDir = new File(table.getName());
        if (tableDir.listFiles() != null) {
            for (File dir : tableDir.listFiles()) {
                if (dir.listFiles() != null) {
                    for (File file : dir.listFiles()) {
                        RandomAccessFile f = new RandomAccessFile(file, "r");
                        readFile(table, f);
                        f.close();
                    }
                }
            }
        }
        table.commit();
    }

    public static void readFile(MyTable table, RandomAccessFile datafile) throws IOException {
        int keyLength;
        int valueLength;
        String key;
        String value;
        table.setByteSize(table.getByteSize() + datafile.length());
        while (datafile.getFilePointer() != datafile.length()) {
            keyLength = datafile.readInt();
            valueLength = datafile.readInt();
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
        table.setByteSize(0);
        File[] dirs = new File[16];
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
