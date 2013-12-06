package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import java.io.File;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;

public class MkDir extends Command {
    private static final String NAME = "mkdir";
    private static final int ARGS_NUM = 1;

    public MkDir() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    public void execute(State fileSystem, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new CommandException("mkdir: arguments expected");
        }
        if (args.length > 1) {
            throw new CommandException("mkdir: wrong number of arguments");
        }
        File dir = fileSystem.compileFile(args[0]);
        if (!dir.mkdirs()) {
            throw new CommandException("mkdir: " + args[0] + ": failed to create directory");
        }
    }
}

