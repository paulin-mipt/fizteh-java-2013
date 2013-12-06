package ru.fizteh.fivt.students.abramova.shell;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DirectoryCommand extends Command {

    public DirectoryCommand(String name) {
        super(name);
    }

    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        Stage stage = status.getStage();
        if (stage == null) {
            throw new IllegalStateException(getName() + ": Command do not get stage");
        }
        String[] directories = new File(stage.currentDirPath()).list();
        for (String file : directories) {
            System.out.println(file);
        }
        return 0;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args == null || args.length == 0;
    }
}
