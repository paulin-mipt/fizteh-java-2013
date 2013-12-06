package ru.fizteh.fivt.students.vlmazlov.shell;

import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;

import java.io.File;
import java.io.OutputStream;

public class RmCommand extends AbstractShellCommand {
    public RmCommand() {
        super("rm", 1);
    }

    ;

    public void execute(String[] args, ShellState state, OutputStream out) throws CommandFailException {
        String pathToDelete = args[0];
        File toDelete = FileUtils.getAbsFile(pathToDelete, state.getCurDir());

        if (toDelete.exists()) {
            FileUtils.recursiveDelete(toDelete);
        } else {
            throw new CommandFailException("rm: " + pathToDelete + " doesn't exist");
        }
    }
}
