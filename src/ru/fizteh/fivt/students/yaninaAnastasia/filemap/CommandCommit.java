package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandCommit extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (myState.table == null) {
            throw new IllegalArgumentException("Wrong table for commit");
        }
        if (args.length != 0) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        for (String step: myState.table.oldData.keySet()) {
            if (myState.table.get(step) != null) {
                myState.database.tables.get(myState.table.getName()).put(step, myState.table.get(step));
            }
        }
        myState.table = myState.database.tables.get(myState.table.getName());
        System.out.println(myState.table.commit());
        return true;
    }

    public String getCmd() {
        return "commit";
    }
}
