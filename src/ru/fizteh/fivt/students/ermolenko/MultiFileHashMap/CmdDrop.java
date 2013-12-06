package ru.fizteh.fivt.students.ermolenko.multifilehashmap;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class CmdDrop implements Command<MultiFileHashMapState> {

    @Override
    public String getName() {

        return "drop";
    }

    @Override
    public void executeCmd(MultiFileHashMapState inState, String[] args) throws IOException {

        if (inState.getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        } else {
            inState.deleteTable(args[0]);
            System.out.println("dropped");
        }
    }
}