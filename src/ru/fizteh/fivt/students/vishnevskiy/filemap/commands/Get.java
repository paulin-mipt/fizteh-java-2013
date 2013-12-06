package ru.fizteh.fivt.students.vishnevskiy.filemap.commands;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;
import ru.fizteh.fivt.students.vishnevskiy.filemap.SingleFileMap;

public class Get extends Command {
    private static final String NAME = "get";
    private static final int ARGS_NUM = 1;

    public Get() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    public void execute(State state, String[] args) throws CommandException {
        SingleFileMap singleFileMap = SingleFileMap.class.cast(state);
        if (args.length == 0) {
            throw new CommandException("get: arguments expected");
        }
        if (args.length > 1) {
            throw new CommandException("get: wrong number of arguments");
        }
        String value = singleFileMap.get(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
