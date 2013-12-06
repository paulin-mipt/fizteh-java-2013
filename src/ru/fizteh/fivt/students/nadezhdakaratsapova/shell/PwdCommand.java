package ru.fizteh.fivt.students.nadezhdakaratsapova.shell;

import java.io.IOException;

public class PwdCommand implements Command {

    private ShellState curState;

    public PwdCommand(ShellState state) {
        curState = state;

    }

    public String getName() {
        return "pwd";
    }

    public void execute(String[] args) throws IOException {
        System.out.println(curState.getCurDir().getAbsolutePath());
    }

    public boolean compareArgsCount(int inputArgsCount) {
        return (inputArgsCount == 0);
    }
}
