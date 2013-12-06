package ru.fizteh.fivt.students.nadezhdakaratsapova.commands;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalTableProvider;

import java.io.IOException;

public class RollbackCommand implements Command {
    private UniversalTableProvider curState;

    public RollbackCommand(UniversalTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "rollback";
    }

    public void execute(String[] args) throws IOException {
        System.out.println(curState.getCurTable().rollback());
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 0);
    }

}
