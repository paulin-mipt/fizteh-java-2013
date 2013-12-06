package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class CommitCommand extends AbstractCommand {

    public CommitCommand(FilesystemState s) {
        super(s, "commit", 0);
    }

    @Override
    public void execute(String[] args) throws IOException {
        FilesystemState table = getState();
        if (table.getWorkingDirectory() == null) {
            getState().getStream().println("no table");
        } else {
            getState().getStream().println(table.commit());
        }
        
    }
}
