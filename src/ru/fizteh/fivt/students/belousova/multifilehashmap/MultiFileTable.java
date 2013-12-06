package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.utils.MultiFileUtils;

import java.io.File;
import java.io.IOException;

public class MultiFileTable extends AbstractTable<String, String> implements ChangesCountingTable {

    public MultiFileTable(File data) {
        try {
            dataDirectory = data;
            MultiFileUtils.read(dataDirectory, dataBase);
        } catch (IOException e) {
            throw new IllegalArgumentException("read error", e);
        }
    }

    @Override
    public int commit() {
        int counter = countChanges();
        for (String key : deletedKeys.get()) {
            dataBase.remove(key);
        }
        dataBase.putAll(addedKeys.get());
        deletedKeys.get().clear();
        addedKeys.get().clear();
        try {
            MultiFileUtils.write(dataDirectory, dataBase);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return counter;
    }
}
