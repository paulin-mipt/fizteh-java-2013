package ru.fizteh.fivt.students.elenav.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class GetCommand extends AbstractCommand {
    public GetCommand(FilesystemState s) {
        super(s, "get", 1);
    }
    
    public void execute(String[] args) throws IOException {
        FilesystemState table = getState();
        String key = args[1];
        if (table.getWorkingDirectory() == null) {
            getState().getStream().println("no table");
        } else {
            if (table.getValue(key) != null) {
                getState().getStream().println("found");
                getState().getStream().println(table.getValue(key));
            } else {
                getState().getStream().println("not found");
            }
        }
    }
}
