package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;

import java.io.IOException;

public class DirCommand extends AbstractCommand<FileSystemShellState> {

    public DirCommand() {
        super("dir", "dir");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        if (params.length() > 0) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        String[] files = shellState.getFileSystem().listWorkingDirectory();
        if (files == null) {
            return;
        }
        for (final String file : files) {
            System.out.println(file);
        }
    }
}
