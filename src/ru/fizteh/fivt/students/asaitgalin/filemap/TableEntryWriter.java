package ru.fizteh.fivt.students.asaitgalin.filemap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class TableEntryWriter {
    private DataOutputStream fileStream;

    public TableEntryWriter(File name) throws IOException {
        fileStream = new DataOutputStream(new FileOutputStream(name));
    }

    public void writeEntries(Map<String, String> items) throws IOException {
        long valuesOffset = 0;
        for (String s : items.keySet()) {
            valuesOffset += s.getBytes("UTF-8").length;
        }
        valuesOffset += items.size() * Integer.SIZE / 8;  // sizes
        valuesOffset += items.size();  // zero bytes
        for (String s : items.keySet()) {
            long valueLen = items.get(s).getBytes("UTF-8").length;
            fileStream.write(s.getBytes("UTF-8"));
            fileStream.writeByte(0);
            fileStream.writeInt((int) valuesOffset);
            valuesOffset += valueLen;
        }
        for (String s : items.values()) {
            fileStream.write(s.getBytes("UTF-8"));
        }
    }
}
