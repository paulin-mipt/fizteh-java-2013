package ru.fizteh.fivt.students.vlmazlov.utils;

import ru.fizteh.fivt.students.vlmazlov.generics.GenericTable;
import ru.fizteh.fivt.students.vlmazlov.generics.GenericTableProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

public class TableWriter {
    private static <V> int countFirstOffSet(GenericTable<V> table) throws IOException {
        int curOffset = 0;

        for (Map.Entry<String, V> entry : table) {
            curOffset += entry.getKey().getBytes("UTF-8").length + 1 + 4;
        }

        return curOffset;

    }

    private static void storeKey(RandomAccessFile dataBaseStorage, String key, int offSet) throws IOException {
        dataBaseStorage.write(key.getBytes("UTF-8"));
        dataBaseStorage.writeByte('\0');
        dataBaseStorage.writeInt(offSet);
    }

    public static <V, T extends GenericTable<V>> void writeTable(
        File root, File storage, T table, GenericTableProvider<V, T> provider) throws IOException {

        if (root == null) {
            throw new FileNotFoundException("Directory not specified");
        }

        storage.delete();

        RandomAccessFile dataBaseStorage = new RandomAccessFile(storage, "rw");

        try {

            long curOffset = countFirstOffSet(table);
            long writePosition;

            for (Map.Entry<String, V> entry : table) {
                if (entry.getValue() == null) {
                    continue;
                }

                storeKey(dataBaseStorage, entry.getKey(), (int) curOffset);
                writePosition = dataBaseStorage.getFilePointer();

                dataBaseStorage.seek(curOffset);
                dataBaseStorage.write(provider.serialize(table, entry.getValue()).getBytes("UTF-8"));
                curOffset = dataBaseStorage.getFilePointer();

                dataBaseStorage.seek(writePosition);
            }

        } finally {
            QuietCloser.closeQuietly(dataBaseStorage);
        }

        if (storage.length() == 0) {
            storage.delete();
        }
    }
}
