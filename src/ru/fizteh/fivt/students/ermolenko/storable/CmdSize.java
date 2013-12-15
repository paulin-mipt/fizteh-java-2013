package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class CmdSize implements Command<StoreableState> {

    @Override
    public String getName() {

        return "size";
    }

    @Override
    public void executeCmd(StoreableState inState, String[] args) throws IOException {

        if (inState.getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }

        System.out.println(inState.getCurrentTable().size());
    }
}
