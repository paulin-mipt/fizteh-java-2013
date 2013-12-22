package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.OutputStream;

public interface Command<T> {
    String getName();

    int getArgNum();

    void execute(String[] args, T state, OutputStream out) 
    throws CommandFailException, UserInterruptionException;

}
