package ru.fizteh.fivt.students.elenav.commands;

import ru.fizteh.fivt.students.elenav.states.FilesystemState;

public class CreateTableCommand extends AbstractCommand {

    public CreateTableCommand(FilesystemState s) {
        super(s, "create", 1);
    }

    public void execute(String[] args) {
        String name = args[1];
        if (getState().provider.createTable(name) != null) {
            getState().getStream().println("created");
        } else { 
            getState().getStream().println(name + " exists");
        }
    }
}
