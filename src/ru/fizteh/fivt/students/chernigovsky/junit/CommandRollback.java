package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.students.chernigovsky.filemap.Command;
import ru.fizteh.fivt.students.chernigovsky.filemap.ExitException;
import ru.fizteh.fivt.students.chernigovsky.filemap.FileMapState;
import ru.fizteh.fivt.students.chernigovsky.filemap.State;

import java.io.IOException;

public class CommandRollback implements Command {
    public String getName() {
        return "rollback";
    }
    public int getArgumentsCount() {
        return 0;
    }
    public void execute(State state, String[] args) throws IOException, ExitException {
        if (state.currentTableIsNull()) {
            System.out.println("no table");
        } else {
            System.out.println(state.rollback());
        }
    }
}