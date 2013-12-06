package ru.fizteh.fivt.students.vishnevskiy.filemap.commands;


import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;
import ru.fizteh.fivt.students.vishnevskiy.filemap.SingleFileMap;


public class Remove extends Command {
    private static final String NAME = "remove";
    private static final int ARGS_NUM = 1;

    public Remove() {
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
            throw new CommandException("remove: arguments expected");
        }
        if (args.length > 1) {
            throw new CommandException("remove: wrong number of arguments");
        }
        int status = singleFileMap.remove(args[0]);
        if (status == 0) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
