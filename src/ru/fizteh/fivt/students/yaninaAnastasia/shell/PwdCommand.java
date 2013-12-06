package ru.fizteh.fivt.students.yaninaAnastasia.shell;

import ru.fizteh.fivt.students.yaninaAnastasia.filemap.State;

import java.io.IOException;

public class PwdCommand extends Command {
    public boolean exec(String[] args, State curState) throws IOException {
        ShellState myState = ShellState.class.cast(curState);
        if (args.length != 0) {
            System.err.println("Invalid arguments");
            return false;
        }
        myState.printWorkDir();
        return true;
    }

    public String getCmd() {
        return "pwd";
    }
}
