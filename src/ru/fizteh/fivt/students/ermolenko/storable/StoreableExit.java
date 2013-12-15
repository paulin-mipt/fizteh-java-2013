package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class StoreableExit implements Command<StoreableState> {


    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void executeCmd(StoreableState inState, String[] args) throws IOException {
        /*
        if (inState.getCurrentTable() != null) {
            //inState.getCurrentTable().commit();
        }
        */
        System.exit(0);
    }
}
