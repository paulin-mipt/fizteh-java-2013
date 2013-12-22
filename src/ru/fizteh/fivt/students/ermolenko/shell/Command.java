package ru.fizteh.fivt.students.ermolenko.shell;

import java.io.IOException;

public interface Command<State> {

    String getName();

    void executeCmd(State inState, String[] args) throws IOException;
}
