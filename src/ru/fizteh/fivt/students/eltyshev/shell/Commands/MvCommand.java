package ru.fizteh.fivt.students.eltyshev.shell.commands;

import ru.fizteh.fivt.students.eltyshev.shell.FileSystemShellState;

import java.io.IOException;
import java.util.ArrayList;

public class MvCommand extends AbstractCommand<FileSystemShellState> {

    public MvCommand() {
        super("mv", "mv <source> <destination>");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);
        if (parameters.size() > 2) {
            throw new IOException("too many arguments!");
        }
        if (parameters.size() < 2) {
            throw new IOException("missing operand");
        }
        shellState.getFileSystem().moveFiles(parameters.get(0), parameters.get(1));
    }
}
