package ru.fizteh.fivt.students.abramova.shell;

import java.io.IOException;
import java.util.Map;

public class ExitCommand extends Command {

    public ExitCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        return -1;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args == null || args.length == 0;
    }
}
