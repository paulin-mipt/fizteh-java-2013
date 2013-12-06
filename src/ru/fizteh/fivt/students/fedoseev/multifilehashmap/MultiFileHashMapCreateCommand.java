package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapCreateCommand extends AbstractCommand<MultiFileHashMapState> {
    public MultiFileHashMapCreateCommand() {
        super("create", 1);
    }

    @Override
    public void execute(String[] input, MultiFileHashMapState state) throws IOException {
        String tableName = input[0];
        File curTableDir = new File(state.getCurDir(), tableName);

        if (!curTableDir.exists()) {
            state.createTable(tableName);
            curTableDir.mkdirs();

            System.out.println("created");
        } else {
            System.out.println(tableName + " exists");
        }
    }
}
