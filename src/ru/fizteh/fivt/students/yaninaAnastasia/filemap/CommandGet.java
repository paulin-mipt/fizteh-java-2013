package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandGet extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (myState.table == null) {
            throw new IllegalArgumentException("no table");
        }
        if (args.length != 1) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        Storeable value = myState.table.get(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(myState.table.provider.serialize(myState.table, value));
        }
        return true;
    }

    public String getCmd() {
        return "get";

    }
}
