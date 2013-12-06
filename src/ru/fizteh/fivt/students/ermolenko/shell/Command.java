package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.IOException;

public interface Command<State> {

    public String getName();

    public void executeCmd(State shell, String[] args) throws IOException;
}
