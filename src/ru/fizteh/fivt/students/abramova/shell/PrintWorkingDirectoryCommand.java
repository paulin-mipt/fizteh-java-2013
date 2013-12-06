package ru.fizteh.fivt.students.abramova.shell;

import java.io.IOException;

public class PrintWorkingDirectoryCommand extends Command {
    public PrintWorkingDirectoryCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        Stage stage = status.getStage();
        if (stage == null) {
            throw new IllegalStateException(getName() + ": Command do not get stage");
        }
        System.out.println(stage.currentDirPath());
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args == null || args.length == 0;
    }
}
