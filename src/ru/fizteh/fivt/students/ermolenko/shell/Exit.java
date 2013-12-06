package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.IOException;

public class Exit implements Command<ShellState> {

    @Override
    public String getName() {

        return "exit";
    }

    @Override
    public void executeCmd(ShellState inState, String[] args) throws IOException {

        System.exit(0);
    }
}
