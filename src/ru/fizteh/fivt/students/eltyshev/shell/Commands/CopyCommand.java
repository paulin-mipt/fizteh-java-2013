package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;

import java.util.ArrayList;
import java.io.IOException;

public class CopyCommand extends AbstractCommand<FileSystemShellState> {

    public CopyCommand() {
        super("cp", "cp <source> <destination>");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 2) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        shellState.getFileSystem().copyFiles(parameters.get(0), parameters.get(1));
    }
}
