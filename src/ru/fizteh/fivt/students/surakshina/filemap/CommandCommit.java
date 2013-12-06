package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.IOException;

public class CommandCommit extends DataBaseCommand {
    public CommandCommit(TableState state) {
        super(state);
        name = "commit";
        numberOfArguments = 0;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTable() != null) {
            try {
                int count = state.getTable().commit();
                System.out.println(count);
            } catch (IOException e) {
                state.printError(e.getMessage());
                return;
            }
        } else {
            System.out.println("no table");
        }

    }
}
