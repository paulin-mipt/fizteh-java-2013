package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.io.*;
import java.util.Map;

public class FileMapUtils {
    public static void readTable(FileMapState fileMapState) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(fileMapState.getCurrentTableProvider().getDbDirectory(), fileMapState.getCurrentTable().getName()));
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
        try {
            while (true) {
                int keyLength;
                int valueLength;

                try {
                    keyLength = dataInputStream.readInt();
                } catch (EOFException ex) {
                    break;
                }
                valueLength = dataInputStream.readInt();

                if (keyLength <= 0 || valueLength <= 0 || keyLength > 1048576 || valueLength > 1048576) {
                    throw new IOException("Wrong string size");
                }
                byte[] keyBytes = new byte[keyLength];
                byte[] valueBytes = new byte[valueLength];

                dataInputStream.readFully(keyBytes);
                dataInputStream.readFully(valueBytes);

                if (keyBytes.length != keyLength || valueBytes.length != valueLength) {
                    throw new IOException("Corrupted database");
                }
                String key = new String(keyBytes, "UTF-8");
                String value = new String(valueBytes, "UTF-8");
                fileMapState.getCurrentTable().put(key, value);
            }
        } finally {
            dataInputStream.close();
        }
    }

    public static void writeTable(FileMapState fileMapState) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(fileMapState.getCurrentTableProvider().getDbDirectory(), fileMapState.getCurrentTable().getName()));
        fileOutputStream.getChannel().truncate(0); // Clear file
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
        try {
            for (Map.Entry<String, String> entry : fileMapState.getCurrentTable().getEntrySet()) {
                dataOutputStream.writeInt(entry.getKey().getBytes("UTF-8").length);
                dataOutputStream.writeInt(entry.getValue().getBytes("UTF-8").length);
                dataOutputStream.write(entry.getKey().getBytes("UTF-8"));
                dataOutputStream.write(entry.getValue().getBytes("UTF-8"));
            }
        } finally {
            dataOutputStream.close();
        }

    }
}
