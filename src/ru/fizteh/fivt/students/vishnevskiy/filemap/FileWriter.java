package ru.fizteh.fivt.students.vishnevskiy.filemap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Set;
import java.io.File;

public class FileWriter {
    private RandomAccessFile stream;
    private File datebase;

    public FileWriter(File file) throws IOException {
        this.datebase = file;
        if (!datebase.exists()) {
            datebase.createNewFile();
        }
        stream = new RandomAccessFile(datebase, "rw");
        stream.setLength(0);
    }

    public void writeFile(Map<String, String> map) throws IOException {
        if (map.isEmpty()) {
            stream.close();
            datebase.delete();
            return;
        }
        Set<String> keysSet = map.keySet();
        long offset = 0;
        for (String key : keysSet) {
            offset += key.getBytes("UTF-8").length;
        }
        offset += keysSet.size() * 5;
        for (String key : keysSet) {
            stream.write(key.getBytes("UTF-8"));
            stream.writeByte(0);
            stream.writeInt((int) offset);
            offset += map.get(key).getBytes("UTF-8").length;
        }
        for (String value : map.values()) {
            stream.write(value.getBytes("UTF-8"));
        }
        stream.close();
    }

}
