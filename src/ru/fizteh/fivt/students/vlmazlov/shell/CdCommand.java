package ru.fizteh.fivt.students.vlmazlov.shell;

import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class CdCommand extends AbstractShellCommand {
    public CdCommand() {
        super("cd", 1);
    }

    ;

    public void execute(String[] args, ShellState state, OutputStream out) throws CommandFailException {
        String newPath = args[0];

        File newDir = FileUtils.getAbsFile(newPath, state.getCurDir());

        if (!newDir.isDirectory()) {
            throw new CommandFailException("cd: " + newPath + " is not a directory");
        }

        try {
            state.changeCurDir(newDir.getCanonicalPath());
        } catch (IOException ex) {
            throw new CommandFailException("cd: " + ex.getMessage());
        }
    }
}

