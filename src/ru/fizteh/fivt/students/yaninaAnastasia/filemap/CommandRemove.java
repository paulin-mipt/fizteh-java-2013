package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandRemove extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (myState.table == null) {
            throw new IllegalArgumentException("no table");
        }
        if (args.length != 1) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        if (myState.table.remove(args[0]) != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }

    public String getCmd() {
        return "remove";
    }

}
