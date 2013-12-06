package ru.fizteh.fivt.students.abramova.filemap;

import com.sun.corba.se.impl.orb.ParserTable;

import java.io.*;
import java.util.*;

public class FileMap implements Closeable {
    protected final String fileMapName;
    protected Map<String, String> fileMap;

    public FileMap(String name, String directory) throws IOException {
        if (directory != null) {
            fileMapName = new File(directory, name).getCanonicalPath();
        } else {
            fileMapName = name;
        }
        DataInputStream reader;
        try {
            File dbDat = new File(fileMapName);
            if (!dbDat.exists()) {
                if (!dbDat.createNewFile()) {
                    throw new IOException("File " + fileMapName + " was not created");
                }
            }
            reader = new DataInputStream(new FileInputStream(fileMapName));
        } catch (IOException e) {
            throw new IOException(fileMapName + ": Opening error: " + e.getMessage());
        }
        fileMap = new HashMap<String, String>();
        reading(fileMap, reader);
        try {
            reader.close();
        } catch (IOException e) {
            throw new IOException("Close error: " + e.getMessage());
        }
    }

    private void reading(Map<String, String> fileMap, DataInputStream reader) throws IOException {
        int keyLen;
        int valueLen;
        byte[] key;
        byte[] value;
        try {
            while (true) {   //До EOFException
                keyLen = reader.readInt();
                try {
                    valueLen = reader.readInt();
                    if (keyLen + valueLen > reader.available()) {
                        throw new IOException("Bad file");
                    }
                    key = new byte[keyLen];
                    value = new byte[valueLen];
                    reader.read(key, 0, keyLen);
                    reader.read(value, 0, valueLen);
                    fileMap.put(new String(key, "UTF8"), new String(value, "UTF8"));
                } catch (EOFException e) { //Если конец файла не в том месте
                    throw new IOException("Bad file");
                } catch (IOException e) {
                    throw new IOException("Read error: " + e.getMessage());
                }
            }
        } catch (EOFException e) {
            //Если конец файла при первом чтении, не падаем, а продолжаем выполнение программы
        }
    }

    public void close() {
        DataOutputStream writer = null;
        try {
            writer = new DataOutputStream(new FileOutputStream(fileMapName));
            writing(fileMap, writer);
        } catch (IOException e) {
            throw new RuntimeException();
        } finally {
            try {
                if (writer != null)
                writer.close();
            } catch (IOException e) {

            }
        }

    }

    private void writing(Map<String, String> fileMap, DataOutputStream writer) throws IOException {
        int keyLen;
        int valueLen;
        byte[] key;
        byte[] value;
        Set<String> keys = fileMap.keySet();
        for (String keyString : keys) {
            key = keyString.getBytes("UTF8");
            value = fileMap.get(keyString).getBytes("UTF8");
            keyLen = key.length;
            valueLen = value.length;
            try {
                writer.writeInt(keyLen);
                writer.writeInt(valueLen);
                writer.write(key, 0, keyLen);
                writer.write(value, 0, valueLen);
            } catch (IOException e) {
                throw new IOException("Write error: " + e.getMessage());
            }
        }
        try {
            writer.flush();
        } catch (IOException e) {
            throw new IOException("Flush error: " + e.getMessage());
        }
    }

    public Map<String, String> getMap() {
        return fileMap;
    }

    public void setMap (Map<String, String> map) {
        fileMap = map;
    }

    public String getFileMapName() {
        return fileMapName;
    }
}
