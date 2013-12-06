package ru.fizteh.fivt.students.vishnevskiy.filemap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;

public class FileReader {
    private RandomAccessFile stream;
    private int valuesOffset;

    public FileReader(File file) throws FileNotFoundException {
        stream = new RandomAccessFile(file, "r");
    }

    private boolean eof() throws IOException {
        return (stream.getFilePointer() >= valuesOffset);
    }

    private String readNextKey() throws IOException {
        List<Byte> buf = new ArrayList<Byte>();
        Byte b = stream.readByte();
        while (b != 0) {
            buf.add(b);
            b = stream.readByte();
        }
        byte[] keyByte = new byte[buf.size()];
        for (int i = 0; i < buf.size(); ++i) {
            keyByte[i] = buf.get(i).byteValue();
        }
        return new String(keyByte, "UTF-8");
    }

    private String readNextValue(long offset1, long offset2) throws IOException {
        long savedPosition = stream.getFilePointer();
        stream.seek(offset1);
        List<Byte> buf = new ArrayList<Byte>();
        Byte b = stream.readByte();
        while (stream.getFilePointer() != offset2) {
            buf.add(b);
            b = stream.readByte();
        }
        buf.add(b);
        byte[] keyByte = new byte[buf.size()];
        for (int i = 0; i < buf.size(); ++i) {
            keyByte[i] = buf.get(i).byteValue();
        }
        stream.seek(savedPosition);
        return new String(keyByte, "UTF-8");
    }


    public void readFile(Map<String, String> map) throws IOException {
        if (stream.length() == 0) {
            stream.close();
            return;
        }
        String key1 = readNextKey();
        valuesOffset = stream.readInt();
        long offset1 = valuesOffset;
        while (!eof()) {
            String key2 = readNextKey();
            long offset2 = stream.readInt();
            String value = readNextValue(offset1, offset2);
            map.put(key1, value);
            key1 = key2;
            offset1 = offset2;
        }
        String value = readNextValue(offset1, stream.length());
        map.put(key1, value);
        stream.close();
    }

}
