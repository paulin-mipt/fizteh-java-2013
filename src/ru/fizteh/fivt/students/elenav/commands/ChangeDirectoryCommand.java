package ru.fizteh.fivt.students.elenav.commands;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class ChangeDirectoryCommand extends AbstractCommand {
    public ChangeDirectoryCommand(FilesystemState s) { 
        super(s, "cd", 1); 
    }
    
    public void execute(String[] args) throws IOException {
        File f = new File(absolutePath(args[1]));
        if (f.isDirectory()) {
            getState().setWorkingDirectory(f);
        } else {
            throw new IOException("cd: '" + args[1] + "': No such file or directory");
        }
    }
}
