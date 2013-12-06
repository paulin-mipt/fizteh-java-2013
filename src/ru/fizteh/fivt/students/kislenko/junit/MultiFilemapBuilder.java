package ru.fizteh.fivt.students.kislenko.junit;

import java.io.File;
import java.io.IOException;

public class MultiFilemapBuilder {
    public void build(JUnitState state) {
        File dirCreator = state.getWorkingPath().toFile();
        if (!dirCreator.exists()) {
            dirCreator.mkdir();
        }
        if (dirCreator.listFiles() != null) {
            for (File file : dirCreator.listFiles()) {
                try {
                    state.createTable(new String[]{file.getName()});
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    public void finish(JUnitState state) throws IOException {
        if (state.getCurrentTable() != null) {
            Utils.dumpTable(state.getCurrentTable());
        }
    }
}
