package ru.fizteh.fivt.students.inaumov.filemap.handlers;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Set;

import ru.fizteh.fivt.students.inaumov.filemap.base.AbstractTable;

public class WriteHandler implements Closeable {
    private RandomAccessFile outputFile = null;

    public WriteHandler(String fileName) throws IOException {
        try {
            outputFile = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException exception) {
            throw new IOException("can't create file " + fileName);
        }

        outputFile.setLength(0);
    }

    public static void saveToFile(String filePath, Set<String> keysToSave, HashMap<String, String> data) throws IOException {
        WriteHandler writer = new WriteHandler(filePath);

        for (final String key: keysToSave) {
            writer.writeEntry(key, data.get(key));
        }

        try {
            writer.close();
        } catch (IOException exception) {

        }
    }

    public void writeEntry(String key, String value) throws IOException {
        outputFile.writeInt(key.getBytes(AbstractTable.CHARSET).length);
        outputFile.writeInt(value.getBytes(AbstractTable.CHARSET).length);

        outputFile.write(key.getBytes(AbstractTable.CHARSET));
        outputFile.write(value.getBytes(AbstractTable.CHARSET));
    }

    public void close() throws IOException {
        outputFile.close();
    }
}