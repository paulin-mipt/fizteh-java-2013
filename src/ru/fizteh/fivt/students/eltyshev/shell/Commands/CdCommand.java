package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;

import java.io.IOException;
import java.util.ArrayList;

public class CdCommand extends AbstractCommand<FileSystemShellState> {

    public CdCommand() {
        super("cd", "cd <directory name>");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 1) {
            throw new IllegalArgumentException("Too many arguments");
        }
        if (parameters.size() > 0) {
            shellState.getFileSystem().setWorkingDirectory(parameters.get(0));
        }
    }
}
