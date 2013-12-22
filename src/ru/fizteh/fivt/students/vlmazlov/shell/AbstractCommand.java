package ru.fizteh.fivt.students.vlmazlov.shell;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractCommand<T> implements Command<T> {
    public static final String SEPARATOR = System.getProperty("line.separator");
    private final String name;
    private final int argNum;

    public String getName() {
        return name;
    }

    public int getArgNum() {
        return argNum;
    }

    protected void displayMessage(String message, OutputStream out) throws CommandFailException {
        try {
            out.write(message.getBytes());
        } catch (IOException ex) {
            throw new CommandFailException(getName() + ": Unable to display result message");
        }
    }

    public AbstractCommand(String name, int argNum) {
        this.name = name;
        this.argNum = argNum;
    }

    public abstract void execute(String[] args, T state, OutputStream out) 
    throws CommandFailException, UserInterruptionException;
}
