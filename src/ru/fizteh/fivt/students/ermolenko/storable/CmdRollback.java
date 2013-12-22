package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class CmdRollback implements Command<StoreableState> {

    @Override
    public String getName() {

        return "rollback";
    }

    @Override
    public void executeCmd(StoreableState inState, String[] args) throws IOException {

        System.out.println(inState.getCurrentTable().rollback());
    }
}
