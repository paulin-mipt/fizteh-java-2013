package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandSize extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (myState.table == null) {
            throw new IllegalArgumentException("no table");
        }
        if (args.length != 0) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        System.out.println(myState.table.size());
        return true;
    }

    public String getCmd() {
        return "size";
    }

}
