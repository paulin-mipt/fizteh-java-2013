package ru.fizteh.fivt.students.vishnevskiy.shell.commands;


import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;

public class Exit extends Command {
    private static final String NAME = "exit";
    private static final int ARGS_NUM = 0;

    public Exit() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    public void execute(State fileSystem, String[] args) throws CommandException {
        if (args.length > 0) {
            throw new CommandException("exit: no arguments needed");
        }
        System.exit(0);
    }
}
