package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import java.io.File;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;

public class Rm extends Command {
    private static final String NAME = "rm";
    private static final int ARGS_NUM = 1;

    public Rm() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    private void recursivelyRemove(File file) throws CommandException {
        if (file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                recursivelyRemove(innerFile);
            }
        }
        if (!file.delete()) {
            throw new CommandException("rm: " + file.getPath() + ": failed to delete file or directory");
        }
    }

    public void execute(State fileSystem, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new CommandException("rm: arguments expected");
        }
        if (args.length > 1) {
            throw new CommandException("rm: wrong number of arguments");
        }

        File file = fileSystem.compileFile(args[0]);
        recursivelyRemove(file);
    }
}
