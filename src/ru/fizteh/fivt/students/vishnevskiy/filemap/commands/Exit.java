package ru.fizteh.fivt.students.vishnevskiy.filemap.commands;

import ru.fizteh.fivt.students.vishnevskiy.shell.Command;
import ru.fizteh.fivt.students.vishnevskiy.shell.CommandException;
import ru.fizteh.fivt.students.vishnevskiy.shell.State;
import ru.fizteh.fivt.students.vishnevskiy.filemap.SingleFileMap;

public class Exit extends Command {
    private static final String NAME = "exit";
    private static final int ARGS_NUM = 0;

    public Exit() {
    }

    public String getName() {
        return NAME;
    }

    public int getArgsNum() {
        return ARGS_NUM;
    }

    public void execute(State state, String[] args) throws CommandException {
        SingleFileMap singleFileMap = SingleFileMap.class.cast(state);
        if (args.length > 0) {
            throw new CommandException("exit: no arguments needed");
        }
        singleFileMap.write();
        System.out.println("exit");
        System.exit(0);
    }
}
