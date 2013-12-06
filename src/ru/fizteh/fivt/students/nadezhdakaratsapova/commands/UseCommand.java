package ru.fizteh.fivt.students.nadezhdakaratsapova.commands;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalTableProvider;

import java.io.IOException;

public class UseCommand implements Command {
    private UniversalTableProvider curState;

    public UseCommand(UniversalTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "use";
    }

    public void execute(String[] args) throws IOException {
        int commitSize;
        if (curState.getCurTable() != null) {
            if ((commitSize = curState.getCurTable().commitSize()) != 0) {
                throw new IOException(commitSize + " unsaved changes");
            }
        }
        if (curState.getTable(args[1]) != null) {
            curState.setCurTable(args[1]);
            System.out.println("using " + args[1]);
        } else {
            System.out.println(args[1] + " not exists");
        }

    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 1);
    }
}
