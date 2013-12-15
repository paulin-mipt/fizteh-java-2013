package ru.fizteh.fivt.students.ermolenko.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ermolenko.shell.Command;

import java.io.IOException;

public class StoreablePut implements Command<StoreableState> {

    @Override
    public String getName() {

        return "put";
    }

    @Override
    public void executeCmd(StoreableState inState, String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("incorrect number of arguments");
            return;
        }
        if (inState.getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        try {
            Storeable deserialized =
                    inState.getCurrentTable().getTableProvider().deserialize(inState.getCurrentTable(), args[1]);

            Storeable value = inState.putToCurrentTable(args[0], deserialized);

            if (null == value) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(inState.getProvider().serialize(inState.getCurrentTable(), value));
            }
        } catch (Exception e) {
            throw new IOException("bullshit");
        }
    }
}
