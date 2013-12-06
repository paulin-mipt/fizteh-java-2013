package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.File;
import java.io.IOException;

public class MkdirCommand implements Command {

    private ShellState curState;

    public MkdirCommand(ShellState state) {
        curState = state;

    }

    public String getName() {
        return "mkdir";
    }

    public void execute(String[] args) throws IOException {
        File newDir = new File(curState.getCurDir(), args[1]);
        if (newDir.exists()) {
            throw new IOException("mkdir: Directory already exists");
        } else {
            newDir.mkdir();
        }

    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 1);
    }
}
