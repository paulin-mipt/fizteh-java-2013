package ru.fizteh.fivt.students.vorotilov.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TableFile {

    private File tableFilePath = null;

    public static class Entry {
        private String key;
        private String value;

        public Entry(String key, String value) {
            if (key == null) {
                throw new IllegalArgumentException("Key is null");
            }
            if (value == null) {
                throw new IllegalArgumentException("Value is null");
            }
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

    }

    TableFile(File tableFilePath) {
        if (tableFilePath == null) {
            throw new IllegalArgumentException("Path to table file is null");
        } else if (!tableFilePath.exists()) {
            try {
                if (!tableFilePath.createNewFile()) {
                    throw new IllegalStateException("New empty file was not created");
                }
            } catch (IOException e) {
                throw new IllegalStateException("Can't create empty table file");
            }
        }
        this.tableFilePath = tableFilePath;
    }

    public List<Entry> readEntries() {
        if (tableFilePath == null) {
            throw new IllegalStateException("Table file is not initialized");
        }
        List<Entry> fileData = new ArrayList<>();
        try (RandomAccessFile file = new RandomAccessFile(tableFilePath, "r")) {
            if (file.length() == 0) {
                throw new IllegalStateException("Table file is empty");
            }
            while (file.getFilePointer() < file.length()) {
                int keyLength = file.readInt();
                int valueLength = file.readInt();
                if (keyLength <= 0 || valueLength <= 0) {
                    throw new IllegalStateException("Table file is damaged");
                }
                if ((long) keyLength + (long) valueLength > file.length() - file.getFilePointer()) {
                    throw new IllegalStateException("Table file is damaged");
                }
                byte[] key = new byte[keyLength];
                byte[] value = new byte[valueLength];
                int readedBytes = 0;
                while (readedBytes != keyLength) {
                    int nowReaded = file.read(key, readedBytes, keyLength - readedBytes);
                    if (nowReaded == -1) {
                        throw new IllegalStateException("Key was not completely readed, but EOF is reached");
                    }
                    readedBytes += nowReaded;
                }
                readedBytes = 0;
                while (readedBytes != valueLength) {
                    int nowReaded = file.read(value, readedBytes, valueLength - readedBytes);
                    if (nowReaded == -1) {
                        throw new IllegalStateException("Value was not completely readed, but EOF is reached");
                    }
                    readedBytes += nowReaded;
                }
                fileData.add(new Entry(new String(key, StandardCharsets.UTF_8),
                        new String(value, StandardCharsets.UTF_8)));
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File for read not found", e);
        } catch (IOException e) {
            throw new IllegalStateException("IO error while reading", e);
        }
        return fileData;
    }

    public void writeEntries(List<Entry> entries) {
        if (tableFilePath == null) {
            throw new IllegalStateException("Table file is not initialized");
        }
        try (RandomAccessFile file = new RandomAccessFile(tableFilePath, "rw")) {
            file.setLength(0);
            for (Entry i : entries) {
                file.writeInt(i.getKey().getBytes(StandardCharsets.UTF_8).length);
                file.writeInt(i.getValue().getBytes(StandardCharsets.UTF_8).length);
                file.write(i.getKey().getBytes(StandardCharsets.UTF_8));
                file.write(i.getValue().getBytes(StandardCharsets.UTF_8));
                file.setLength(file.getFilePointer());
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File for writing not found", e);
        } catch (IOException e) {
            throw new IllegalStateException("IO error while writing", e);
        }
        if (tableFilePath.length() == 0) {
            if (!tableFilePath.delete()) {
                throw new IllegalStateException("Can't delete empty file");
            }
        }
    }

}
