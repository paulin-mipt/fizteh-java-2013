package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class StoreableGet implements Command<StoreableState> {

    @Override
    public String getName() {

        return "get";
    }

    @Override
    public void executeCmd(StoreableState inState, String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("incorrect number of arguments");
            return;
        }
        if (inState.getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        Storeable value = inState.getFromCurrentTable(args[0]);
        if (null == value) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            String result = inState.getProvider().serialize(inState.getCurrentTable(), value);
            System.out.println(result);
        }
    }
}
