package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.OutputStream;

public class ExitCommand<T> extends AbstractCommand<T> {
    public ExitCommand() {
        super("exit", 0);
    }

    public void execute(String[] args, T state, OutputStream out) throws UserInterruptionException {
        throw new UserInterruptionException();
    }
}
