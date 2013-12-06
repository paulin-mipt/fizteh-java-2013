package ru.fizteh.fivt.students.vishnevskiy.filemap.commands;


import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;
import ru.fizteh.fivt.students.vishnevskiy.filemap.SingleFileMap;

public class Put extends Command {
    private static final String NAME = "put";
    private static final int ARGS_NUM = 2;

    public Put() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    public void execute(State state, String[] args) throws CommandException {
        SingleFileMap singleFileMap = SingleFileMap.class.cast(state);
        if (args.length < 2) {
            throw new CommandException("put: arguments expected");
        }
        if (args.length > 2) {
            throw new CommandException("put: wrong number of arguments");
        }
        String status = singleFileMap.put(args[0], args[1]);
        if (status == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(status);
        }
    }
}
