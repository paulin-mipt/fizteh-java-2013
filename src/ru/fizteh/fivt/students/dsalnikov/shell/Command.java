package ru.fizteh.fivt.students.dsalnikov.shell;


import java.io.IOException;

public interface Command {
    void execute(Object state, String[] args) throws IOException;

    String getName();

    int getArgsCount();
}
