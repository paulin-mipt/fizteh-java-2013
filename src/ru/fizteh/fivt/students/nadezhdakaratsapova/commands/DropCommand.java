package ru.fizteh.fivt.students.nadezhdakaratsapova.commands;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalTableProvider;

import java.io.IOException;

public class DropCommand implements Command {
    private UniversalTableProvider curState;

    public DropCommand(UniversalTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "drop";
    }

    public void execute(String[] args) throws IOException {
        curState.removeTable(args[1]);
        if (args[1].equals(curState.getCurTable().getName())) {
            curState.setCurTable(null);
        }
        System.out.println("dropped");
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 1);
    }

}
