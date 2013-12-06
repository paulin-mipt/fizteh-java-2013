package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import java.io.File;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;

public class Dir extends Command {
    private static final String NAME = "dir";
    private static final int ARGS_NUM = 0;

    public Dir() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    public void execute(State fileSystem, String[] args) throws CommandException {
        if (args.length > 0) {
            throw new CommandException("dir: no arguments needed");
        }
        File currentDir = new File(fileSystem.getCurrentDirectory());
        String[] files = currentDir.list();
        for (String file : files) {
            System.out.println(file);
        }
    }
}
