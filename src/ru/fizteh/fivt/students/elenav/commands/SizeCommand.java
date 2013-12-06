package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class SizeCommand extends AbstractCommand {

    public SizeCommand(FilesystemState s) {
        super(s, "size", 0);
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (getState().getWorkingDirectory() != null) {
            getState().getStream().println(getState().size());
        }
        
    }

}
