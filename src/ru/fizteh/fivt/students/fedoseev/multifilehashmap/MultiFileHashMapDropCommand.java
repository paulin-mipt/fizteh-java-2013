package ru.fizteh.fivt.students.fedoseev.multifilehashmap;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.State;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMapDropCommand extends AbstractCommand<State> {
    public MultiFileHashMapDropCommand() {
        super("drop", 1);
    }

    @Override
    public void execute(String[] input, State state) throws IOException {
        String tableName = input[0];
        File curTableDir = new File(state.getCurDir(), tableName);

        if (curTableDir.exists()) {
            if (curTableDir.listFiles() != null) {
                for (File dir : curTableDir.listFiles()) {
                    if (dir.listFiles() != null) {
                        for (File file : dir.listFiles()) {
                            file.delete();
                        }
                    }

                    dir.delete();
                }
            }

            if (state.getCurTable() != null && tableName.equals(state.getCurTableDir().getName())) {
                state.clearContentAndDiff();
                state.setCurTable(null);
            }

            curTableDir.delete();
            state.removeTable(curTableDir.toString());

            System.out.println("dropped");
        } else {
            System.out.println(tableName + " not exists");
        }
    }
}
