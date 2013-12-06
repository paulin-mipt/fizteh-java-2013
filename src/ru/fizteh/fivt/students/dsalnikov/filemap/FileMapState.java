package ru.fizteh.fivt.students.dsalnikov.filemap;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;

public class FileMapState {

    String state;
    HashMap<String, String> data = new HashMap<String, String>();

    public String getState() {
        return state;
    }

    public FileMapState(String buildstate) {
        state = buildstate;
    }

    public String setState(String s) {
        state = s;
        return s;
    }

    public void build(String state) throws IOException {
        RandomAccessFile filemap = new RandomAccessFile(state, "r");
        if (filemap.length() > 444444444) throw new IOException("file is too big");
        while (filemap.getFilePointer() != filemap.length()) {
            int klength = filemap.readInt();
            if (klength < 1 || klength > filemap.length() - filemap.getFilePointer() + 4) {
                filemap.close();
                throw new IllegalArgumentException("Illegal key length");
            }
            int vlength = filemap.readInt();
            if (vlength < 1 || vlength > filemap.length() - filemap.getFilePointer() + 4) {
                filemap.close();
                throw new IllegalArgumentException("Illegal value length");
            }
            byte[] bytekey = new byte[klength];
            byte[] bytevalue = new byte[vlength];
            filemap.read(bytekey);
            filemap.read(bytevalue);
            String key = new String(bytekey, StandardCharsets.UTF_8);
            String value = new String(bytevalue, StandardCharsets.UTF_8);
            data.put(key, value);
        }
        filemap.close();
    }

    public void put(String state) throws IOException {
        RandomAccessFile filemp = new RandomAccessFile(state, "rw");
        if (filemp.length() > 444444444) {
            throw new IOException("file is too big");
        }
        filemp.setLength(0);
        Set<String> writedata = data.keySet();
        for (String str : writedata) {
            filemp.writeInt(str.getBytes(StandardCharsets.UTF_8).length);
            filemp.writeInt(getValue(str).getBytes(StandardCharsets.UTF_8).length);
            filemp.write(str.getBytes(StandardCharsets.UTF_8));
            filemp.write(getValue(str).getBytes(StandardCharsets.UTF_8));
        }
        filemp.close();
    }


    public boolean isEmpty() {
        return data.isEmpty();
    }

    public String getValue(String key) {
        return data.get(key);
    }

    public String setValue(String key, String value) {
        return data.put(key, value);
    }

    public String deleteValue(String key) {
        return data.remove(key);
    }
}

