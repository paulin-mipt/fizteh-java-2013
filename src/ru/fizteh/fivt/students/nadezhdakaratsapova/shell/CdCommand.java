package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.io.IOException;

public class CdCommand implements Command {

    private ShellState curState;

    public CdCommand(ShellState state) {
        curState = state;

    }

    public String getName() {
        return "cd";
    }

    public void execute(String[] args) throws IOException {
        File newDir = new File(args[1]);
        if (!newDir.isAbsolute()) {
            newDir = new File(curState.getCurDir(), args[1]);
        }
        if (!newDir.exists() | !newDir.isDirectory()) {
            throw new IOException("cd: " + newDir.getName() + ": directory doesn't exist");
        } else {
            curState.changeCurDir(newDir.getCanonicalFile());
        }
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 1);
    }
}
