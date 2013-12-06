package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class PrintWorkingDirectoryCommand extends AbstractCommand {
    public PrintWorkingDirectoryCommand(FilesystemState s) { 
        super(s, "pwd", 0);
    }
    
    public void execute(String[] args) throws IOException {
        try {
            getState().getStream().println(getState().getWorkingDirectory().getCanonicalPath());
        } catch (SecurityException e) {
            throw new IOException(e.getMessage());
        }
    }
}
