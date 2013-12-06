package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;

public class Cp extends Command {
    private static final String NAME = "cp";
    private static final int ARGS_NUM = 2;

    public Cp() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    public void execute(State fileSystem, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new CommandException("cp: arguments expected");
        }
        if (args.length > 2) {
            throw new CommandException("cp: wrong number of arguments");
        }
        File source = fileSystem.compileFile(args[0]);
        File target = fileSystem.compileFile(args[1]);
        if (!source.exists()) {
            throw new CommandException("cp: " + source.getName() + ": file or directory doesn't exist");
        }
        if (target.isDirectory()) {
            target = new File(target, source.getName());
        }
        if (target.exists()) {
            throw new CommandException("cp: can't copy to " + target.getPath() + ": file or directory already exists");
        }
        try {
            Files.copy(source.toPath(), target.toPath());
        } catch (IOException e) {
            throw new CommandException("cp: failed to copy " + source.getName() + " to " + target.getPath());
        }
    }
}

