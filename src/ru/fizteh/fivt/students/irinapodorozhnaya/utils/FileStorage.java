package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileStorage {

    public static Map<String, String> openDataFile(File file, int n) {
        Map<String, String> data = null;        
        if (file.exists()) {
            try {
                data = loadDataFromFile(file, n);
            } catch (IOException e) {
                throw new IllegalArgumentException("File has wrong format");
            }
        }
        return data;
    }
    
    private static Map<String, String> loadDataFromFile(File file, int n) throws IOException {
        
        RandomAccessFile dbFile = new RandomAccessFile(file, "r");
        Map<String, String> data = new HashMap<>();
        
        if (dbFile.length() == 0) {
            dbFile.close();
            return data;
        }
        data = new HashMap<>();
        long nextOffset = 0;
        
        dbFile.seek(0);
        String key = readKey(dbFile, n);
        long firstOffset = dbFile.readInt();
        long currentOffset = firstOffset;
        long pos = dbFile.getFilePointer();
        String nextKey = key;
        
        while (pos < firstOffset) {
            nextKey = readKey(dbFile, n);
            nextOffset = dbFile.readInt();
            pos = dbFile.getFilePointer();
            dbFile.seek(currentOffset);
            data.put(key, readValue(dbFile, nextOffset - currentOffset));
            dbFile.seek(pos);
            key = nextKey;
            currentOffset = nextOffset;
        }
        dbFile.seek(currentOffset);
        data.put(nextKey, readValue(dbFile, dbFile.length() - currentOffset));
        dbFile.close();
        return data;
    }
    
    private static String readValue(RandomAccessFile dbFile, long l) throws IOException {
        int len = (int) l;
        if (len < 0) {
            throw new IOException("File has incorrect format");
        }
        byte[] bytes = new byte[len];
        dbFile.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    protected static String readKey(RandomAccessFile dbFile, int n) throws IOException {
        byte c = dbFile.readByte();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (c != 0) {
            out.write(c);
            c = dbFile.readByte();
        }
        String key = new String(out.toByteArray(), StandardCharsets.UTF_8);
        if (n >= 0) {
            if (Utils.getNumberOfFile(key) != n) {
                throw new IOException("key lies in wrong file");
            }
        }
        return key;
    }
    
    public static void commitDiff(File file, Map<String, String> data) throws IOException {

        if (data == null) {
            file.delete();
            return;
        }
        File tmp = new File(file.getName() + '~');
        if (!tmp.createNewFile()) {
            throw new IOException("can't create file to write");
        }
        RandomAccessFile tmpR = new RandomAccessFile(tmp, "rw");
        int offset = 0;
        long pos = 0;

        Set<String> keys = data.keySet();
        for (String s: keys) {
            offset += s.getBytes(StandardCharsets.UTF_8).length + 5;
        }
        
        for (Map.Entry<String, String> s: data.entrySet()) {
            tmpR.seek(pos);
            tmpR.write(s.getKey().getBytes(StandardCharsets.UTF_8));
            tmpR.writeByte(0);
            tmpR.writeInt(offset);
            pos = tmpR.getFilePointer();
            tmpR.seek(offset);
            tmpR.write(s.getValue().getBytes(StandardCharsets.UTF_8));
            offset = (int) tmpR.getFilePointer();
        }
        if (tmpR.length() == 0) {
            file.delete();
            tmpR.close();
            tmp.delete();
            return;
        }
        
        tmpR.close();        
        file.delete();
        if (!tmp.renameTo(file)) {
            throw new IOException("can't write to file");
        }
    }
}
