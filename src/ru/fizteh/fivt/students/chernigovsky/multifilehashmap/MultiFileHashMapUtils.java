package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.chernigovsky.filemap.FileMapState;

public class MultiFileHashMapUtils {

    public static void delete(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            if (file.delete()) {
                return;
            } else {
                throw new IOException("Delete error");
            }
        }
        for (File f : file.listFiles()) {
            delete(f);
        }
        if (!file.delete()) {
            throw new IOException("Delete error");
        }
    }

    public static void readTable(FileMapState fileMapState) throws IOException {

        for (Integer directoryNumber = 0; directoryNumber < 16; ++directoryNumber) {
            File tableFolder = new File(fileMapState.getCurrentTableProvider().getDbDirectory(), fileMapState.getCurrentTable().getName());
            File directory = new File(tableFolder, directoryNumber.toString() + ".dir");
            if (!directory.exists()) {
                continue;
            }
            if (!directory.isDirectory()) {
                throw new IOException("Corrupted database");
            }

            for (Integer fileNumber = 0; fileNumber < 16; ++fileNumber) {
                File file = new File(directory, fileNumber.toString() + ".dat");
                if (!file.exists()) {
                    continue;
                }
                if (!file.isFile()) {
                    throw new IOException("Corrupted database");
                }

                FileInputStream fileInputStream = new FileInputStream(file);
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
                        if (Math.abs(keyBytes[0]) % 16 != directoryNumber || Math.abs(keyBytes[0]) / 16 % 16 != fileNumber) {
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

        }
    }

    public static void writeTable(FileMapState fileMapState) throws IOException {

        for (Integer directoryNumber = 0; directoryNumber < 16; ++directoryNumber) {
            File tableFolder = new File(fileMapState.getCurrentTableProvider().getDbDirectory(), fileMapState.getCurrentTable().getName());
            File dir = new File(tableFolder, directoryNumber.toString() + ".dir");

            for (Integer fileNumber = 0; fileNumber < 16; ++fileNumber) {
                HashMap<String, String> currentMap = new HashMap<String, String>();
                for (Map.Entry<String, String> entry : fileMapState.getCurrentTable().getEntrySet()) {
                    if (Math.abs(entry.getKey().getBytes("UTF-8")[0]) % 16 == directoryNumber && Math.abs(entry.getKey().getBytes("UTF-8")[0]) / 16 % 16 == fileNumber) {
                        currentMap.put(entry.getKey(), entry.getValue());
                    }
                }

                File file = new File(dir, fileNumber.toString() + ".dat");

                if (currentMap.size() == 0) {
                    if (file.exists()) {
                        if (!file.delete()) {
                            throw new IOException("Delete error");
                        }
                    }
                    continue;
                }

                if (!dir.exists()) {
                    dir.mkdir();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.getChannel().truncate(0); // Clear file
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
                try {
                    for (Map.Entry<String, String> entry : currentMap.entrySet()) {
                        dataOutputStream.writeInt(entry.getKey().getBytes("UTF-8").length);
                        dataOutputStream.writeInt(entry.getValue().getBytes("UTF-8").length);
                        dataOutputStream.write(entry.getKey().getBytes("UTF-8"));
                        dataOutputStream.write(entry.getValue().getBytes("UTF-8"));
                    }
                } finally {
                    dataOutputStream.close();
                }

            }

            if (dir.exists() && dir.list().length == 0) {
                if (!dir.delete()) {
                    throw new IOException("Delete");
                }
            }
        }
    }
}
