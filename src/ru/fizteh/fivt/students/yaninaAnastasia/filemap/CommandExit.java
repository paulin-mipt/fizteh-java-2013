package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandExit extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        MultiDBState myState = MultiDBState.class.cast(curState);
        if (args.length != 0) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        return true;
    }

    public String getCmd() {
        return "exit";
    }
}
