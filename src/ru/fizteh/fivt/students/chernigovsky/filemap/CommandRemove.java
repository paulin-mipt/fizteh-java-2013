package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.io.IOException;

public class CommandRemove implements Command {
    public String getName() {
        return "remove";
    }
    public int getArgumentsCount() {
        return 1;
    }
    public void execute(State state, String[] args) throws IOException, ExitException {
        if (state.currentTableIsNull()) {
            System.out.println("no table");
            return;
        }
        String oldValue = state.removeFromCurrentTable(args[1]);
        if (oldValue == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
