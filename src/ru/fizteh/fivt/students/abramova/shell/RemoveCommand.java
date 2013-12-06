package ru.fizteh.fivt.students.abramova.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class RemoveCommand extends Command {
    public RemoveCommand(String name) {
        super(name);
    }
    @Override
    public int doCommand(String[] args, Status status) throws IOException {
        Stage stage = status.getStage();
        if (stage == null) {
            throw new IllegalStateException(getName() + ": Command do not get stage");
        }
        int returnValue = 0;
        File toRemove = new File(stage.currentDirPath(), args[0]);
        if (!toRemove.exists()) {
            System.out.println(getName() + ": cannot remove \'" + args[0] + "\': No such file or directory");
            returnValue = 2;
        } else {
            if (toRemove.isDirectory()) {
                for (String child : toRemove.list()) {
                    if (returnValue == 0) {
                        String[] arg = new String[1];
                        arg[0] = new File(args[0], child).getPath();
                        returnValue = doCommand(arg, status);
                    }
                }
            }
            if (returnValue == 0) {
                try {
                    Files.deleteIfExists(toRemove.toPath());
                } catch (IOException e) {
                    throw new IOException(getName() + ": Delete error: " + e.getMessage());
                }
            }
        }
        return returnValue;
    }

    @Override
    public boolean correctArgs(String[] args) {
        return args != null && args.length == 1;
    }
}
