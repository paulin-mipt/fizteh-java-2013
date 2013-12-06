package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class RemoveCommand extends AbstractCommand {

    public RemoveCommand(FilesystemState s) {
        super(s, "remove", 1);
    }

    public void execute(String[] args) throws IOException {
        FilesystemState table = getState();
        String key = args[1];
        if (table.getWorkingDirectory() == null) {
            getState().getStream().println("no table");
        } else {
            if (table.getValue(key) != null) {
                getState().getStream().println("removed");
                getState().getStream().println(table.removeKey(key));
            } else {
                getState().getStream().println("not found");
            }
        }
    }
    
}
