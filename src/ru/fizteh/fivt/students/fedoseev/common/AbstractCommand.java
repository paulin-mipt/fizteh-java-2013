package ru.fizteh.fivt.students.fedoseev.common;

import java.io.IOException;
import java.text.ParseException;

public abstract class AbstractCommand<State> implements Frame.Command<State> {
    private String cmdName;
    private int argsCount;

    public AbstractCommand(String cmdName, int argsCount) {
        this.cmdName = cmdName;
        this.argsCount = argsCount;
    }

    public String getCmdName() {
        return cmdName;
    }

    public int getArgsCount() {
        return argsCount;
    }

    public abstract void execute(String[] input, State state)
            throws IOException, InterruptedException, ClassNotFoundException, ParseException;
}
