package ru.fizteh.fivt.students.dobrinevski.multiFileHashMap;

import java.io.File;
import java.util.AbstractMap;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.nio.ByteBuffer;
import java.io.FileInputStream;

public class MyMultiHashMap {
    public File curTable = null;
    public HashMap<Integer, HashMap<String, String>> dataBase = null;
    public boolean[] check;

    public MyMultiHashMap() {
        dataBase = new HashMap<Integer, HashMap<String, String>>();
        check = new boolean[256];
        for (int i = 0; i < 256; i++) {
            dataBase.put(i, new HashMap<String, String>());
            check[i] = false;
        }
    }

    public void parseFile(File dbFile, int firstControlValue, int secondControlValue) throws Exception {
        if (!check[firstControlValue * 16 + secondControlValue]) {
            if (dbFile.exists() && dbFile.isFile()) {
                FileInputStream fstream = new FileInputStream(dbFile);
                while (fstream.available() > 0) {
                    Map.Entry<String, String> newEntry = parseEntry(fstream);
                    Integer hashCode = newEntry.getKey().hashCode();
                    hashCode = Math.abs(hashCode);
                    Integer nDirectory = hashCode % 16;
                    Integer nFile = hashCode / 16 % 16;
                    if (firstControlValue != nDirectory || secondControlValue != nFile) {
                        throw new Exception("Error: bad file");
                    }
                    dataBase.get(nDirectory * 16 + nFile).put(newEntry.getKey(), newEntry.getValue());
                }
                fstream.close();
            } else if (dbFile.exists() && !dbFile.isFile()) {
                throw new Exception("Bad Table");
            }
            check[firstControlValue * 16 + secondControlValue] = true;
        }
    }

    private static Map.Entry<String, String> parseEntry(FileInputStream fstream) throws Exception {
        byte[] buf = new byte[4];
        read(fstream, buf, 4);
        int keySize = ByteBuffer.wrap(buf).getInt();
        read(fstream, buf, 4);
        int valueSize = ByteBuffer.wrap(buf).getInt();
        if (keySize <= 0 || valueSize <= 0 || keySize > (1 << 24) || valueSize > (1 << 24)) {
            throw new Exception("Error: bad file");
        }
        byte[] kBuf = new byte[keySize];
        read(fstream, kBuf, keySize);
        byte[] vBuf = new byte[valueSize];
        read(fstream, vBuf, valueSize);
        return new AbstractMap.SimpleEntry<String, String>(new String(kBuf, "UTF-8"), new String(vBuf, "UTF-8"));
    }

    private static void read(FileInputStream fstream, byte[] buf, int readCount) throws Exception {
        int readed = 0;
        if (readCount < 0) {
            throw new Exception("Error: bad file");
        }
        while (readed < readCount) {
            int readNow = fstream.read(buf, readed, readCount - readed);
            if (readNow < 0) {
                throw new Exception("Error: bad file");
            }
            readed += readNow;
        }
    }

    public void writeOut() throws Exception {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (check[i * 16 + j]) {
                    File dir = new File(curTable.getCanonicalPath()
                            + File.separator + i + ".dir" + File.separator + j + ".dat");
                    if (dir.exists()) {
                        if (dir.isDirectory()) {
                            throw new Exception("Bad table");
                        }
                        if (!dir.delete()) {
                            throw new Exception("Failed in delete" + dir.getCanonicalPath());
                        }
                    }
                    if (dataBase.get(i * 16 + j).isEmpty()) {
                        dir = new File(curTable.getCanonicalPath() + File.separator + i + ".dir");
                        if (dir.exists()) {
                            String[] child = dir.list();
                            if (child.length == 0) {
                                dir.delete();
                            }
                        }
                    } else {
                        String way = curTable.getCanonicalPath()
                                + File.separator + i + ".dir";
                        File workFile = new File(way);
                        if (workFile.isFile()) {
                            throw new Exception("Bad table");
                        }
                        if (!workFile.exists()) {
                            if (!workFile.mkdir()) {
                                throw new Exception("Directory wasn't created");
                            }
                        }
                        File workFile2 = new File(way + File.separator + j + ".dat");
                        try (FileOutputStream fstream = new FileOutputStream(workFile2)) {
                            for (Map.Entry<String, String> entry : dataBase.get(16 * i + j).entrySet()) {
                                writeEntry(entry, fstream);
                            }
                            dataBase.get(i * 16 + j).clear();
                        }
                    }
                    check[i * 16 + j] = false;
                }
            }
        }
    }

    private static void writeEntry(Map.Entry<String, String> e, FileOutputStream fstream) throws IOException {
        byte[] keyBuf = e.getKey().getBytes(StandardCharsets.UTF_8);
        byte[] valueBuf = e.getValue().getBytes(StandardCharsets.UTF_8);
        fstream.write(ByteBuffer.allocate(4).putInt(keyBuf.length).array());
        fstream.write(ByteBuffer.allocate(4).putInt(valueBuf.length).array());
        fstream.write(keyBuf);
        fstream.write(valueBuf);
    }
}
