package ru.fizteh.fivt.students.kislenko.storeable;

import java.io.File;
import java.io.IOException;

public class StoreableBuilder {
    public void build(StoreableState state) throws IOException, ClassNotFoundException {
        File dirCreator = state.getWorkingPath().toFile();
        if (!dirCreator.exists()) {
            dirCreator.mkdir();
        }
        if (dirCreator.listFiles() != null) {
            for (File file : dirCreator.listFiles()) {
                try {
                    state.createTable(file.getName());
                } catch (Exception e) {
                    // Something went wrong in creating new table. Just ignore it.
                }
            }
        }
    }

    public void finish(StoreableState state) throws IOException {
        if (state.getCurrentTable() != null) {
            Utils.dumpTable(state.getCurrentTable());
        }
    }
}
