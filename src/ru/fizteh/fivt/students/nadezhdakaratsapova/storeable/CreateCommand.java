package ru.fizteh.fivt.students.nadezhdakaratsapova.storeable;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;
import ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils.SignatureController;

import java.io.IOException;

public class CreateCommand implements Command {
    private StoreableTableProvider curState;

    public CreateCommand(StoreableTableProvider state) {
        curState = state;
    }

    public String getName() {
        return "create";
    }

    public void execute(String[] args) throws IOException {
        if (curState.createTable(args[1], SignatureController.getSignatureFromArgs(args)) == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount > 1);
    }
}
