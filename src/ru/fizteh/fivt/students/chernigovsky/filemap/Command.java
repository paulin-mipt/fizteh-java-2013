package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.io.IOException;

public interface Command {
    public String getName();
    public int getArgumentsCount();
    public void execute(State state, String[] args) throws IOException, ExitException;
}
