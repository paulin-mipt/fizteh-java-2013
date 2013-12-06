package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;

public class Cd extends Command {
    private static final String NAME = "cd";
    private static final int ARGS_NUM = 1;

    public Cd() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    public void execute(State fileSystem, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new CommandException("cd: arguments expected");
        }
        if (args.length > 1) {
            throw new CommandException("cd: wrong number of arguments");
        }

        fileSystem.changeCurrentDirectory(args[0]);
    }
}
