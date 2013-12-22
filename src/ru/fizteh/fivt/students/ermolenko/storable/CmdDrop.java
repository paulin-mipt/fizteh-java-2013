package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class CmdDrop implements Command<StoreableState> {

    @Override
    public String getName() {

        return "drop";
    }

    @Override
    public void executeCmd(StoreableState inState, String[] args) throws IOException {

        if (inState.getTable(args[0]) == null) {
            throw new IllegalStateException(args[0] + " not exists");
        } else {
            inState.deleteTable(args[0]);
            System.out.println("dropped");
        }
    }
}
