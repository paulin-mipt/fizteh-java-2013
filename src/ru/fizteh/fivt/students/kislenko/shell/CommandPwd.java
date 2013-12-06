package ru.fizteh.fivt.students.kislenko.shell;

import java.io.IOException;

public class CommandPwd implements Command<ShellState> {
    public String getName() {
        return "pwd";
    }

    public int getArgCount() {
        return 0;
    }

    public void run(ShellState state, String[] empty) throws IOException {
        if (empty.length > 0) {
            throw new IOException("pwd: Too many arguments.");
        }
        System.out.println(state.getState());
    }
}
