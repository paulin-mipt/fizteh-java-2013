package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.IOException;
import java.text.ParseException;

public class MultiFileHashMapUseCommand extends AbstractCommand<State> {
    public MultiFileHashMapUseCommand() {
        super("use", 1);
    }

    @Override
    public void execute(String[] input, State state) throws IOException, ParseException {
        String tableName = input[0];

        if (state.getCurDir().getName().equals(tableName)) {
            System.out.println("using " + tableName);
            return;
        }

        int changesNumber = 0;

        if (state.getCurTable() != null) {
            changesNumber = state.getDiffSize();
        }

        if (changesNumber != 0) {
            throw new IOException(changesNumber + " unsaved changes");
        }

        if (state.getCurDir().toPath().resolve(tableName).toFile().exists()) {
            state.saveTable(state.getCurTable());

            if (state.getCurTable() != null) {
                state.clearContentAndDiff();
            }

            state.setCurTable(tableName);

            state.readTableOff(state.getCurTable());

            System.out.println("using " + tableName);
        } else {
            System.out.println(tableName + " not exists");
        }
    }
}
