package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;
import java.text.ParseException;

public class CommandPut extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (myState.table == null) {
            throw new IllegalArgumentException("no table");
        }
        if (args.length != 2) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        Storeable value;
        try {
            value = myState.table.provider.deserialize(myState.table, args[1]);
        } catch (ParseException e) {
            System.err.println("ParseException" + e.getMessage());
            return false;
        }
        Storeable prevValue = myState.table.put(args[0], value);
        if (prevValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(myState.table.provider.serialize(myState.table, prevValue));
        }
        return true;
    }

    public String getCmd() {
        return "put";
    }
}
