package ru.fizteh.fivt.students.vishnevskiy.shell.commands;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;

public class Pwd extends Command {
    private static final String NAME = "pwd";
    private static final int ARGS_NUM = 0;

    public Pwd() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    public void execute(State fileSystem, String[] args) throws CommandException {
        if (args.length > 0) {
            throw new CommandException("pwd: no arguments needed");
        }
        System.out.println(fileSystem.getCurrentDirectory());
    }
}
