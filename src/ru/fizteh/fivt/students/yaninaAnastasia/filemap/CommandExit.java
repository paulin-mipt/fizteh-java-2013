package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import ru.fizteh.fivt.students.yaninaAnastasia.shell.Command;

import java.io.IOException;

public class CommandExit extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        ServletState myState = ServletState.class.cast(curState);
        if (args.length != 0) {
            throw new IllegalArgumentException("Illegal arguments");
        }
        if (myState.server.isStarted()) {
            try {
                myState.server.stop();
            } catch (Exception e) {
                //
            }
        }
        return true;
    }

    public String getCmd() {
        return "exit";
    }
}
