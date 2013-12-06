package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.shell.Command;


import java.io.IOException;

public class CreateCommand implements Command {
    private MultiFileHashMapProvider curState;

    public CreateCommand(MultiFileHashMapProvider state) {
        curState = state;
    }

    public String getName() {
        return "create";
    }

    public void execute(String[] args) throws IOException {
        if (curState.createTable(args[1]) == null) {
            System.out.println(args[1] + " exists");
        } else {
            System.out.println("created");
        }
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 1);
    }
}
