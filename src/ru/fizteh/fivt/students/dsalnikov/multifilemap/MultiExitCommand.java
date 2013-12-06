package ru.fizteh.fivt.students.dsalnikov.multifilemap;


import ru.fizteh.fivt.students.dsalnikov.shell.Command;

import java.io.IOException;

public class MultiExitCommand implements Command {

    @Override
    public void execute(Object state, String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("wrong usage of command exit: no arguments expected");
        } else {
            MultiFileMapState mfms = (MultiFileMapState) state;
            if (!mfms.getFlag()) {
                System.exit(0);
            } else {
                mfms.getTable().flush();
                System.exit(0);
            }
        }
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
