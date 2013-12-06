package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ShellRmCommand extends AbstractCommand<ShellState> {
    public ShellRmCommand() {
        super("rm", 1);
    }


    public void deleteFile(File curFile) throws IOException {
        if (curFile.isDirectory()) {
            while (curFile.listFiles().length != 0) {
                deleteFile(curFile.listFiles()[0]);
            }
        }

        if (!curFile.delete()) {
            throw new SecurityException("RM ERROR: cannot delete file \""
                    + curFile.getCanonicalFile().toString() + "\"");
        }
    }

    @Override
    public void execute(String[] input, ShellState state) throws IOException {
        File curFile = new File(state.getCurState().toPath().resolve(input[0]).toString());

        if (!curFile.exists()) {
            throw new FileNotFoundException("RM ERROR: not existing file or directory \"" + input[0] + "\"");
        }

        deleteFile(curFile);
        while (!curFile.exists()) {
            curFile = curFile.getParentFile();
        }
        state.setCurState(curFile);
    }
}
