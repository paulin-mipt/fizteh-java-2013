package ru.fizteh.fivt.students.chernigovsky.filemap;

import java.io.IOException;

public class CommandExit implements Command {
    public String getName() {
        return "exit";
    }
    public int getArgumentsCount() {
        return 0;
    }
    public void execute(State state, String[] args) throws IOException, ExitException {
        System.out.println("exit");
        throw new ExitException();
    }
}
