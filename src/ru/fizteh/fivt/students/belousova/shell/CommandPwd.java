package ru.fizteh.fivt.students.belousova.shell;

import java.io.IOException;

public class CommandPwd implements Command {
    private final String name = "pwd";
    private ShellState state;

    public CommandPwd(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public int getArgCount() {
        return 0;
    }

    public void execute(String[] args) throws IOException {
        System.out.println(state.getCurrentDirectory().getAbsolutePath());
    }
}
