package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;

public class Mv extends Command {
    private static final String NAME = "mv";
    private static final int ARGS_NUM = 2;

    public Mv() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    public void execute(State fileSystem, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new CommandException("mv: arguments expected");
        }
        if (args.length > 2) {
            throw new CommandException("mv: wrong number of arguments");
        }
        File source = fileSystem.compileFile(args[0]);
        File target = fileSystem.compileFile(args[1]);
        if (!source.exists()) {
            throw new CommandException("mv: " + source.getName() + ": file or directory doesn't exist");
        }
        if (target.isDirectory()) {
            target = new File(target, source.getName());
        }
        if (target.exists()) {
            throw new CommandException("mv: can't move to " + target.getPath() + ": file or directory already exists");
        }
        try {
            Files.move(source.toPath(), target.toPath());
        } catch (IOException e) {
            throw new CommandException("mv: failed to move " + source.getName() + " to " + target.getPath());
        }
    }
}

