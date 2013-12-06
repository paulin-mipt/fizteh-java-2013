package ru.fizteh.fivt.students.belousova.shell;

import java.io.IOException;

public class CommandCd implements Command {
    private final String name = "cd";
    private ShellState state;

    public CommandCd(ShellState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public int getArgCount() {
        return 1;
    }

    public void execute(String[] args) throws IOException {
        String directory = args[1];
        state.setCurrentDirectory(directory);
    }
}
