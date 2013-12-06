package ru.fizteh.fivt.students.chernigovsky.multifilehashmap;

import java.io.IOException;
import ru.fizteh.fivt.students.chernigovsky.filemap.Command;
import ru.fizteh.fivt.students.chernigovsky.filemap.ExitException;
import ru.fizteh.fivt.students.chernigovsky.filemap.FileMapState;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;

public class CommandCreate implements Command {
    public String getName() {
        return "create";
    }
    public int getArgumentsCount() {
        return 1;
    }
    public void execute(State state, String[] args) throws IOException, ExitException {
        if (state.createTable(args[1])) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }
}
