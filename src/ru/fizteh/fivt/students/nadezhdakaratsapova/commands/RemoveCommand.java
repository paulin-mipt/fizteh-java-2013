package ru.fizteh.fivt.students.nadezhdakaratsapova.commands;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.UniversalTableProvider;

import java.io.IOException;

public class RemoveCommand implements Command {
    private UniversalTableProvider curState;

    public RemoveCommand(UniversalTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "remove";
    }

    public void execute(String[] args) throws IOException {
        if (curState.getCurTable() != null) {
            Object value = curState.getCurTable().remove(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } else {
            System.out.println("no table");
        }
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 1);
    }
}
