package ru.fizteh.fivt.students.kislenko.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class MapBuilder {
    private static final long MAX_FILE_SIZE = 100000000;

    public void buildMap(FilemapState state) throws IOException {
        File dbCreator = state.getPath().toFile();
        if (!dbCreator.exists()) {
            dbCreator.createNewFile();
        }
        RandomAccessFile database = new RandomAccessFile(state.getPath().toFile(), "r");
        if (database.length() > MAX_FILE_SIZE) {
            database.close();
            throw new IOException("Too big database file.");
        }
        int keyLength;
        int valueLength;
        String key;
        String value;
        while (database.getFilePointer() != database.length()) {
            keyLength = database.readInt();
            if (keyLength < 1 || keyLength > database.length() - database.getFilePointer() + 4) {
                database.close();
                throw new IOException("Incorrect key length in input.");
            }
            valueLength = database.readInt();
            if (valueLength < 1 || valueLength > database.length() - database.getFilePointer() + 4) {
                database.close();
                throw new IOException("Incorrect value length in input.");
            }

            byte[] keySymbols = new byte[keyLength];
            byte[] valueSymbols = new byte[valueLength];
            database.read(keySymbols);
            database.read(valueSymbols);
            key = new String(keySymbols, StandardCharsets.UTF_8);
            value = new String(valueSymbols, StandardCharsets.UTF_8);
            state.put(key, value, null);
        }
        database.close();
    }

    public void fillFile(FilemapState state) throws IOException {
        RandomAccessFile database = new RandomAccessFile(state.getPath().toFile(), "rw");
        database.setLength(0);
        Set<String> keySet = state.getMap().keySet();
        for (String key : keySet) {
            if (database.getFilePointer() > MAX_FILE_SIZE) {
                database.close();
                throw new IOException("Too big database file.");
            }
            database.writeInt(key.getBytes(StandardCharsets.UTF_8).length);
            database.writeInt(state.get(key, null).getBytes(StandardCharsets.UTF_8).length);
            database.write(key.getBytes(StandardCharsets.UTF_8));
            database.write(state.get(key, null).getBytes(StandardCharsets.UTF_8));
        }
        database.close();
    }
}
