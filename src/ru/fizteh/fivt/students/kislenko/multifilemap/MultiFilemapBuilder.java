package ru.fizteh.fivt.students.kislenko.multifilemap;

import java.io.File;
import java.io.IOException;

public class MultiFilemapBuilder {
    public void build(MultiFileHashMapState state) {
        File dirCreator = state.getWorkingPath().toFile();
        if (!dirCreator.exists()) {
            dirCreator.mkdir();
        }
        if (dirCreator.listFiles() != null) {
            for (File file : dirCreator.listFiles()) {
                try {
                    state.createTable(new String[]{file.getName()});
                } catch (Exception e) {
                    // Ignored
                }
            }
        }
    }

    public void finish(MultiFileHashMapState state) throws IOException {
        if (state.getCurrentTable() != null) {
            Utils.dumpTable(state.getCurrentTable());
        }
    }
}
