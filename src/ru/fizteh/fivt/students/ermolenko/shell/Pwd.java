package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.IOException;

public class Pwd implements Command<ShellState> {

    public String getName() {

        return "pwd";
    }

    public void executeCmd(ShellState inState, String[] args) throws IOException {

        if (args.length > 0) {
            throw new IOException("too many args");
        }
        System.out.println(inState.getPath().toString());
    }
}
