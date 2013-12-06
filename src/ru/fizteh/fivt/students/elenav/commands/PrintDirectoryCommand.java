package ru.fizteh.fivt.students.elenav.commands;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class PrintDirectoryCommand extends AbstractCommand {
    public PrintDirectoryCommand(FilesystemState s) { 
        super(s, "dir", 0);
    }
    
    public void execute(String[] args) {
        String[] files = getState().getWorkingDirectory().list();
        for (String s : files) {
            getState().getStream().println(s);
        }
    }
}
