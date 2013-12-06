package ru.fizteh.fivt.students.kislenko.junit;

import ru.fizteh.fivt.students.kislenko.shell.Command;

import java.io.IOException;

public class CommandCommit implements Command<TransactionalFatherState> {
    @Override
    public String getName() {
        return "commit";
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public void run(TransactionalFatherState state, String[] args) throws Exception {
        if (!state.hasCurrentTable()) {
            System.out.println("no table");
            throw new IOException("Database haven't initialized.");
        }
        state.dumpCurrentTable();
        System.out.println(state.commitCurrentTable());
    }
}
