package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.IOException;

import ru.fizteh.fivt.students.chernigovsky.filemap.Command;
import ru.fizteh.fivt.students.chernigovsky.filemap.ExitException;
import ru.fizteh.fivt.students.chernigovsky.filemap.FileMapState;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;

public class CommandUse implements Command {
    public String getName() {
        return "use";
    }
    public int getArgumentsCount() {
        return 1;
    }
    public void execute(State state, String[] args) throws IOException, ExitException {
        if (!state.currentTableIsNull() && state.getDiffCount() != 0) {
            System.out.print(state.getDiffCount());
            System.out.println(" unsaved changes");
            return;
        }
        if (!state.isTableExists(args[1])) {
            System.out.println(args[1] + " not exists");
            return;
        } else {
            if (!state.currentTableIsNull()) {
                state.writeTable();
            }

            state.changeCurrentTable(args[1]);
            state.readTable();
            System.out.println("using " + args[1]);
        }
    }
}
