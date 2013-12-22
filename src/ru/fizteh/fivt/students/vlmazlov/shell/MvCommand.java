package ru.fizteh.fivt.students.vlmazlov.shell;

import ru.fizteh.fivt.students.vlmazlov.utils.FileOperationFailException;
import ru.fizteh.fivt.students.vlmazlov.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class MvCommand extends AbstractShellCommand {
    public MvCommand() {
        super("mv", 2);
    }

    ;

    //At this point, destination is guaranteed to be a directory,
    //which is preserved throughout the recursive traverse

    public void execute(String[] args, ShellState state, OutputStream out) throws CommandFailException {
        String sourcePath = args[0];
        String destinationPath = args[1];

        File source = FileUtils.getAbsFile(sourcePath, state.getCurDir());
        File destination = FileUtils.getAbsFile(destinationPath, state.getCurDir());

        if (!source.exists()) {
            throw new CommandFailException("mv: " + sourcePath + " doesn't exist");
        }

        //Renaming
        try {
            if ((source.getParentFile().getCanonicalPath().equals(destination.getParentFile().getCanonicalPath()))
                    && (!destination.isDirectory())) {

                if (!source.renameTo(destination)) {
                    throw new CommandFailException("mv: Unable to rename " + sourcePath + " to " + destinationPath);
                }

                return;
            }
        } catch (IOException ex) {
            throw new CommandFailException("mv: Unable to discern parent directories");
        }

        if (!destination.isDirectory()) {
            throw new CommandFailException("mv: " + destination + " is not a directory");
        }

        try {
            FileUtils.moveToDir(source, destination);
        } catch (FileOperationFailException ex) {
            throw new CommandFailException("mv: " + ex.getMessage());
        }
    }
}
