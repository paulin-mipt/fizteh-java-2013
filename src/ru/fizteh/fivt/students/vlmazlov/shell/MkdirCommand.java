package ru.fizteh.fivt.students.vlmazlov.shell;

import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;

import java.io.File;
import java.io.OutputStream;

public class MkdirCommand extends AbstractShellCommand {
    public MkdirCommand() {
        super("mkdir", 1);
    }

    ;

    public void execute(String[] args, ShellState state, OutputStream out) throws CommandFailException {
        String dirname = args[0];

        File toBeCreated = FileUtils.getAbsFile(dirname, state.getCurDir());

        if (!toBeCreated.mkdir()) {
            throw new CommandFailException("mkdir: Unable to create directory: " + dirname);
        }
    }
}
