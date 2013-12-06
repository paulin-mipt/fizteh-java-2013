package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;


public class CommandDrop extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (args.length != 1) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        String path = myState.getProperty(myState);
        if (!myState.database.tables.containsKey(args[0])) {
            System.out.println(args[0] + " not exists");
            return false;
        }
        if (myState.table != null && args[0].equals(myState.table.getName())) {
            myState.table = null;
        }
        myState.database.removeTable(args[0]);

        System.out.println("dropped");
        return true;
    }

    public String getCmd() {
        return "drop";
    }
}
