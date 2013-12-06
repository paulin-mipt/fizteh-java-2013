package ru.fizteh.fivt.students.nadezhdakaratsapova.commands;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalTableProvider;

import java.io.IOException;

public class ExitCommand implements Command {
    private UniversalTableProvider curState;

    public ExitCommand(UniversalTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "exit";
    }

    public void execute(String[] args) throws IOException {
        if (curState.getCurTable() != null) {
            curState.getCurTable().writeToDataBase();
        }
        System.exit(0);
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 0);
    }

}
