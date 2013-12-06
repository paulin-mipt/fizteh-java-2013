package ru.fizteh.fivt.students.fedoseev.filemap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.IOException;

public class FileMapGetCommand extends AbstractCommand<State> {
    public FileMapGetCommand() {
        super("get", 1);
    }

    @Override
    public void execute(String[] input, State state) throws IOException {
        if (state.usingTables()) {
            if (state.getCurTable() == null) {
                throw new IOException("no table");
            }
        }

        String gotValue = state.get(input[0]);

        if (gotValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("found\n" + gotValue);
        }
    }
}
